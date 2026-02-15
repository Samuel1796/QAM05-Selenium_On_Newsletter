package com.newsletter.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;

/**
 * Page Object for Newsletter Sign-up Form
 */
public class SignUpPage extends BasePage {

    // Page URL
    private static final String PAGE_URL = "https://newsletter-sign-up-form-bay.vercel.app/";

    // Locators using @FindBy annotation (Page Factory Pattern)
    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(css = "button[type='submit']")
    private WebElement subscribeButton;

    @FindBy(id = "error-message")
    private WebElement errorMessage;

    @FindBy(css = "h1, [class*='heading']")
    private WebElement pageHeading;

    @FindBy(id = "sign-form")
    private WebElement signUpForm;

    /**
     * Constructor
     */
    public SignUpPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigate to the newsletter sign-up page
     */
    public SignUpPage navigateToPage() {
        driver.get(PAGE_URL);
        return this;
    }

    /**
     * Enter email address
     */
    public SignUpPage enterEmail(String email) {
        enterText(emailInput, email);
        return this;
    }

    /**
     * Click subscribe button
     */
    public void clickSubscribe() {
        click(subscribeButton);
    }

    /**
     * Get error message text
     */
    public String getErrorMessage() {
        try {
            waitForElementToBeVisible(errorMessage);
            return getText(errorMessage);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }

    /**
     * Get page heading text
     */
    public String getPageHeading() {
        return getText(pageHeading);
    }

    /**
     * Check if email input field is displayed
     */
    public boolean isEmailInputDisplayed() {
        return isDisplayed(emailInput);
    }

    /**
     * Check if subscribe button is displayed
     */
    public boolean isSubscribeButtonDisplayed() {
        return isDisplayed(subscribeButton);
    }

    /**
     * Check if form is displayed
     */
    public boolean isFormDisplayed() {
        if (isDisplayed(signUpForm)) {
            return true;
        }
        try {
            // Fallback for deployments where the form id differs or is missing.
            return driver.findElement(By.cssSelector("form")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get subscribe button text
     */
    public String getSubscribeButtonText() {
        return getText(subscribeButton);
    }

    /**
     * Get email input placeholder
     */
    public String getEmailPlaceholder() {
        return emailInput.getAttribute("placeholder");
    }

    /**
     * Clear email input
     */
    public SignUpPage clearEmailInput() {
        emailInput.clear();
        return this;
    }

    /**
     * Get email input value
     */
    public String getEmailInputValue() {
        return emailInput.getAttribute("value");
    }

    /**
     * Complete sign-up process
     */
    public void completeSignUp(String email) {
        enterEmail(email);
        clickSubscribe();
    }


}
