package com.chess.scraper;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter minimum rating: ");
        int minRating = scanner.nextInt();

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.chess.com/vi/ratings");

        Thread.sleep(5000);

        RatingsPage ratingsPage = new RatingsPage(driver);
        List<String[]> filteredRows = new ArrayList<>();
        boolean hasNextPage = true;

        while (hasNextPage) {
            List<WebElement> rows = ratingsPage.getRows();
            int lastRowIndex = rows.size() - 1;

            boolean shouldGoNextPage = false;

            for (int i = 0; i < rows.size(); i++) {
                WebElement row = rows.get(i);
                List<WebElement> cols = row.findElements(By.tagName("td"));

                if (cols.size() >= 6) {
                    try {
                        WebElement rankDiv = cols.get(0).findElement(By.xpath("./div[1]"));
                        String rankText = rankDiv.getText().trim();
                        String rank = rankText.contains("\n") ? rankText.split("\n")[1].trim() : rankText;

                        WebElement nameLink = cols.get(1).findElement(By.tagName("a"));
                        String playerName = nameLink.getText().trim();

                        int classical = Integer.parseInt(cols.get(3).findElement(By.tagName("div")).getText().trim());
                        int rapid = Integer.parseInt(cols.get(4).findElement(By.tagName("div")).getText().trim());
                        int blitz = Integer.parseInt(cols.get(5).findElement(By.tagName("div")).getText().trim());

                        boolean allAboveMin = classical >= minRating && rapid >= minRating && blitz >= minRating;

                        if (allAboveMin) {
                            filteredRows.add(new String[] {rank, playerName, String.valueOf(classical), String.valueOf(rapid), String.valueOf(blitz)});
                        }

                        if (i == lastRowIndex) {
                            if (classical >= minRating || rapid >= minRating || blitz >= minRating) {
                                shouldGoNextPage = true;
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Number format error at row " + (i + 1));
                    }
                }
            }

            if (shouldGoNextPage) {
                hasNextPage = ratingsPage.clickNextPage();
            } else {
                hasNextPage = false;
            }
        }

        driver.quit();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String sheetName = "ratings_" + timestamp;

        GoogleSheetsExporter exporter = new GoogleSheetsExporter();
        String link = exporter.exportToSheet(sheetName, filteredRows);

        System.out.println("Export successful. Link: " + link);

        try (FileWriter writer = new FileWriter("output.txt")) {
            writer.write(link);
        }
    }
}
