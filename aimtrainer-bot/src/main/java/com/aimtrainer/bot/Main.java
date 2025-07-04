package com.aimtrainer.bot;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        WebDriverManager.chromedriver().setup();

        WebDriver driver = new ChromeDriver();
//        driver.manage().window().setSize(new Dimension(900, 700));
        driver.manage().window().setPosition(new Point(0, 0));

        driver.get("https://aimtrainer.io/challenge");
        System.out.println("Open https://aimtrainer.io/challenge");

        Thread.sleep(1000);

        boolean clickedPlay = false;
        for (WebElement btn : driver.findElements(By.tagName("button"))) {
            String text = btn.getText();
            System.out.println("Find button: '" + text + "'");
            if ("PLAY".equalsIgnoreCase(text.trim())) {
                btn.click();
                clickedPlay = true;
                System.out.println("Clicked PLAY button");
                break;
            }
        }
        if (!clickedPlay) {
            System.out.println("Not found PLAY button, exit.");
            driver.quit();
            return;
        }

        Thread.sleep(1000);

        Actions actions = new Actions(driver);

        WebElement canvas = driver.findElement(By.tagName("canvas"));
        org.openqa.selenium.Point canvasLocation = canvas.getLocation();
        System.out.println("Canvas location: " + canvasLocation);

        int missedCount = 0;
        final int maxMissed = 5;

        for (int round = 0; round < 100; round++) {

            boolean playAgainFound = false;
            for (WebElement btn : driver.findElements(By.tagName("button"))) {
                String text = btn.getText();
                if ("PLAY AGAIN".equalsIgnoreCase(text.trim())) {
                    System.out.println("Detect PLAY AGAIN button, stop program.");
                    playAgainFound = true;
                    break;
                }
            }
            if (playAgainFound) break;

//            File screenshot = canvas.getScreenshotAs(OutputType.FILE);
//            BufferedImage img = ImageIO.read(screenshot);
            BufferedImage img = ImageIO.read(
                    new ByteArrayInputStream(
                            canvas.getScreenshotAs(OutputType.BYTES)
                    )
            );


            List<MyPoint> redPoints = findRedPoints(img);

            if (redPoints.isEmpty()) {
                missedCount++;
                System.out.println("Round " + round + ": Not found red circle, try (" + missedCount + "/" + maxMissed + ")");
                if (missedCount >= maxMissed) {
                    System.out.println("Try max times, stop.");
                    break;
                }
                Thread.sleep(1000);
                continue;
            }

            missedCount = 0;

            List<MyPoint> clusters = clusterPoints(redPoints, 25);

            System.out.println("Round " + round + ": Found " + redPoints.size() + " red circle, combine " + clusters.size() + " cluster.");

            for (MyPoint p : clusters) {
                int clickX = canvasLocation.getX() + p.x;
                int clickY = canvasLocation.getY() + p.y;

                actions.moveByOffset(clickX, clickY).click().moveByOffset(-clickX, -clickY);
            }

            actions.perform();

//            Thread.sleep(1);
        }

        driver.quit();
        System.out.println("End program.");
    }

    private static List<MyPoint> findRedPoints(BufferedImage img) {
        List<MyPoint> points = new ArrayList<>();
        int step = 5;

        for (int y = 0; y < img.getHeight(); y += step) {
            for (int x = 0; x < img.getWidth(); x += step) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                if (r >= 150 && g >= 60 && g <= 180 && b <= 120) {
                    points.add(new MyPoint(x, y));
                }
            }
        }
        return points;
    }

    private static List<MyPoint> clusterPoints(List<MyPoint> points, int distance) {
        List<List<MyPoint>> clusters = new ArrayList<>();

        outer:
        for (MyPoint p : points) {
            for (List<MyPoint> cluster : clusters) {
                for (MyPoint cp : cluster) {
                    if (p.distance(cp) < distance) {
                        cluster.add(p);
                        continue outer;
                    }
                }
            }
            List<MyPoint> newCluster = new ArrayList<>();
            newCluster.add(p);
            clusters.add(newCluster);
        }

        List<MyPoint> centers = new ArrayList<>();
        for (List<MyPoint> cluster : clusters) {
            int sumX = 0, sumY = 0;
            for (MyPoint pt : cluster) {
                sumX += pt.x;
                sumY += pt.y;
            }
            centers.add(new MyPoint(sumX / cluster.size(), sumY / cluster.size()));
        }
        return centers;
    }

    static class MyPoint {
        int x, y;
        MyPoint(int x, int y) { this.x = x; this.y = y; }
        double distance(MyPoint other) {
            int dx = this.x - other.x;
            int dy = this.y - other.y;
            return Math.sqrt(dx*dx + dy*dy);
        }
    }
}
