package com.newsletter.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
        click(dismissButton);
    }

    /**
     * Get email confirmation text
     */
    public String getEmailConfirmation() {
        try {
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
