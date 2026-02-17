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
     * Check that the browser is currently displaying the sign-up form.
     * This uses visible form/email controls instead of relying on raw URL strings.
     *
     * @return true if the sign-up form context is visible
     */
    public boolean isOnSignUpPage() {
        return isEmailInputDisplayed() || isFormDisplayed();
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
     * Gets the validation error message in a normalized form.
     *
     * <p>Normalization makes assertions more stable across minor UI copy changes (extra whitespace,
     * line breaks, different capitalization).</p>
     *
     * @return trimmed, single-spaced error message; empty string if not present
     */
    public String getNormalizedErrorMessage() {
        return getErrorMessage()
                .replace("\n", " ")
                .replace("\r", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }

    /**
     * Check if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }

    /**
     * Wait for the validation error message to become visible.
     *
     * @return true if the error message becomes visible within the default timeout, false otherwise
     */
    public boolean waitForErrorMessageToBeVisible() {
        try {
            waitForElementToBeVisible(errorMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for the validation error message to disappear.
     *
     * @return true if the error message disappears within the default timeout, false otherwise
     */
    public boolean waitForErrorMessageToDisappear() {
        try {
            waitForElementToDisappear(errorMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
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
        try {
            waitForElementToBeVisible(emailInput);
            String value = emailInput.getAttribute("value");
            return value != null ? value : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Wait for email input to be ready after page load/refresh
     */
    public void waitForEmailInputReady() {
        waitForElementToBeVisible(emailInput);
    }

    /**
     * Complete sign-up process
     */
    public void completeSignUp(String email) {
        enterEmail(email);
        clickSubscribe();
    }


}
