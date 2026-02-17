package com.newsletter.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object for Success Message Page
 */
public class SuccessPage extends BasePage {

    // Locators using @FindBy annotation
    @FindBy(css = ".success-message, [class*='success']")
    private WebElement successMessage;

    @FindBy(css = "h1, h2, [class*='heading']")
    private WebElement successHeading;

    @FindBy(id = "dismiss-btn")
    private WebElement dismissButton;

    @FindBy(id = "user-email")
    private WebElement emailConfirmation;

    /**
     * Constructor
     */
    public SuccessPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Check if success message is displayed
     */
    public boolean isSuccessMessageDisplayed() {
        return isDisplayed(successMessage);
    }

    /**
     * Get success message text
     */
    public String getSuccessMessage() {
        try {
            waitForElementToBeVisible(successMessage);
            return getText(successMessage);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get success heading text
     */
    public String getSuccessHeading() {
        try {
            return getText(successHeading);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check if dismiss button is displayed
     */
    public boolean isDismissButtonDisplayed() {
        return isDisplayed(dismissButton);
    }

    /**
     * Click dismiss button
     */
    public void clickDismissButton() {
        try {
            click(dismissButton);
        } catch (Exception primaryError) {
            WebElement fallbackDismissButton = driver.findElement(
                    By.xpath("//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'dismiss')]")
            );
            click(fallbackDismissButton);
        }
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.id("email")),
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("form"))
            ));
        } catch (Exception ignored) {
        }
    }

    /**
     * Get email confirmation text
     */
    public String getEmailConfirmation() {
        try {
            waitForElementToBeVisible(emailConfirmation);
            return getText(emailConfirmation);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Verify user is on success page
     */
    public boolean isOnSuccessPage() {
        try {
            waitForElementToBeVisible(successMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
