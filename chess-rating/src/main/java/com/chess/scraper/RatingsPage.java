package com.chess.scraper;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class RatingsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//table/tbody/tr")
    private List<WebElement> rows;

    @FindBy(xpath = "//a[@aria-label='Trang kế']")
    private WebElement nextPageButton;

    public RatingsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public List<WebElement> getRows() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table/tbody/tr")));
        return rows;
    }

    public boolean clickNextPage() throws InterruptedException {
        try {
            wait.until(ExpectedConditions.visibilityOf(nextPageButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextPageButton);
            Thread.sleep(500);

            if (nextPageButton.isDisplayed() && nextPageButton.isEnabled()) {
                List<WebElement> oldRows = rows;
                WebElement oldFirstRow = oldRows.isEmpty() ? null : oldRows.get(0);

                nextPageButton.click();

                if (oldFirstRow != null) {
                    wait.until(ExpectedConditions.stalenessOf(oldFirstRow));
                }
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table/tbody/tr")));

                PageFactory.initElements(driver, this);

                return true;
            }
        } catch (NoSuchElementException | TimeoutException e) {
        }
        return false;
    }
}
