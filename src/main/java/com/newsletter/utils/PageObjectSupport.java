package com.newsletter.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Shared page-object support with common Selenium helpers.
 *
 * <p>This centralises WebDriver and wait utilities used by page objects
 * without prescribing any specific test flow.</p>
 */
public abstract class PageObjectSupport {
    // Added a comment to force re-compilation
    protected static final String BASE_PAGE_URL = "https://newsletter-sign-up-form-bay.vercel.app/";

    protected WebDriver driver;
    protected WebDriverWait wait;

    public PageObjectSupport(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    /**
     * Wait for element to be visible
     */
    protected void waitForElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait for element to become invisible or not present in the DOM.
     *
     * @param element the element to wait to disappear
     */
    protected void waitForElementToDisappear(WebElement element) {
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    /**
     * Wait for element to be clickable
     */
    protected void waitForElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Enter text into an input field
     */
    protected void enterText(WebElement element, String text) {
        waitForElementToBeVisible(element);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Click on an element
     */
    protected void click(WebElement element) {
        waitForElementToBeClickable(element);
        element.click();
    }

    /**
     * Get text from an element
     */
    protected String getText(WebElement element) {
        waitForElementToBeVisible(element);
        return element.getText();
    }

    /**
     * Check if element is displayed
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
