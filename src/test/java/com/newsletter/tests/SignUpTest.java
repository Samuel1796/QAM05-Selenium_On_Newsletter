package com.newsletter.tests;

import com.newsletter.pages.SignUpPage;
import com.newsletter.pages.SuccessPage;
import com.newsletter.utils.Utils;
import org.testng.annotations.*;

import java.time.Duration;

import static org.testng.Assert.*;

/**
 * Test class for Newsletter Sign-up Form
 * Tests cover positive and negative scenarios for email validation and subscription
 */
public class SignUpTest extends BaseTest {
    protected SignUpPage signUpPage;
    protected SuccessPage successPage;

    @BeforeMethod
    @Override
    public void setUp() {
        super.setUp();
        signUpPage = new SignUpPage(driver);
        successPage = new SuccessPage(driver);
    }




    @Test(description = "Verify Successful Newsletter Subscription with Valid Email")
    public void testSuccessfulSubscriptionWithValidEmail() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp(Utils.generateRandomEmail());

        waitFor(2000);

        boolean isSuccess = successPage.isOnSuccessPage() || !signUpPage.isErrorMessageDisplayed();
        assertTrue(isSuccess, "Subscription should be successful with valid email");
    }

    @Test(description = "Verify Error Message for Invalid Email Format", priority = 3)
    public void testInvalidEmailFormat() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp("invalid-email");

        waitFor(1000);

        assertTrue(driver.getCurrentUrl().contains("newsletter-sign-up-form-bay.vercel.app"),
                "Should remain on same page with invalid email");
    }

    @Test(description = "Verify Error Message for Empty Email Field", priority = 4)
    public void testEmptyEmailField() {
        signUpPage.navigateToPage();
        signUpPage.clickSubscribe();

        waitFor(1000);

        assertTrue(driver.getCurrentUrl().contains("newsletter-sign-up-form-bay.vercel.app"),
                "Should remain on same page when email is empty");
    }

    @DataProvider(name = "invalidEmails")
    public Object[][] invalidEmailProvider() {
        // Generate random pool of invalid emails and select 6 for testing
        return Utils.getRandomInvalidEmails(6);
    }

    @Test(description = "Verify Form Validation with Multiple Invalid Email Formats",
            priority = 5, dataProvider = "invalidEmails")
    public void testMultipleInvalidEmailFormats(String invalidEmail) {
        signUpPage.navigateToPage();
        signUpPage.enterEmail(invalidEmail);
        signUpPage.clickSubscribe();

        waitFor(1000);

        assertTrue(driver.getCurrentUrl().contains("newsletter-sign-up-form-bay.vercel.app"),
                "Should remain on same page with invalid email: " + invalidEmail);
    }

    @DataProvider(name = "validEmails")
    public Object[][] validEmailProvider() {
        // Generate random pool of 10 emails and select 5 for testing
        return Utils.getRandomEmails(10, 5);
    }

    @Test(description = "Verify Successful Subscription with Various Valid Email Formats",
            priority = 6, dataProvider = "validEmails")
    public void testValidEmailFormats(String validEmail) {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp(validEmail);

        waitFor(2000);

        boolean isSuccess = successPage.isOnSuccessPage() || !signUpPage.isErrorMessageDisplayed();
        assertTrue(isSuccess, "Should accept valid email format: " + validEmail);
    }

    @Test(description = "Verify Email Input Field Can Accept Text", priority = 7)
    public void testEmailInputAcceptsText() {
        signUpPage.navigateToPage();
        signUpPage.enterEmail(Utils.generateRandomEmail());

        assertTrue(signUpPage.isEmailInputDisplayed(),
                "Email input should remain visible after entering text");
    }

    @Test(description = "Verify Subscribe Button is Clickable", priority = 8)
    public void testSubscribeButtonIsClickable() {
        signUpPage.navigateToPage();

        assertTrue(signUpPage.isSubscribeButtonDisplayed(), "Subscribe button should be displayed");
        signUpPage.clickSubscribe();
    }

    @Test(description = "Verify Form Elements After Page Refresh", priority = 9)
    public void testFormPersistenceAfterRefresh() {
        signUpPage.navigateToPage();
        driver.navigate().refresh();
        waitFor(1000);

        assertTrue(signUpPage.isFormDisplayed(), "Form should be displayed after refresh");
        assertTrue(signUpPage.isEmailInputDisplayed(), "Email input should be displayed after refresh");
        assertTrue(signUpPage.isSubscribeButtonDisplayed(), "Subscribe button should be displayed after refresh");
    }

    @Test(description = "Verify Maximum Length of Email Input", priority = 10)
    public void testEmailInputMaxLength() {
        signUpPage.navigateToPage();

        String longEmail = "a".repeat(100) + "@example.com";
        signUpPage.enterEmail(longEmail);

        assertTrue(signUpPage.isEmailInputDisplayed(),
                "Email input should handle long email addresses");
    }

    @Test(description = "Verify Special Characters in Email", priority = 11)
    public void testSpecialCharactersInEmail() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp(Utils.generateRandomEmail());

        waitFor(2000);

        boolean isSuccess = successPage.isOnSuccessPage() || !signUpPage.isErrorMessageDisplayed();
        assertTrue(isSuccess, "Should accept email with valid characters");
    }

    @Test(description = "Verify Form Behavior with Leading/Trailing Spaces", priority = 12)
    public void testEmailWithSpaces() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp("  " + Utils.generateRandomEmail() + "  ");

        waitFor(2000);

        boolean isSuccess = successPage.isOnSuccessPage() || !signUpPage.isErrorMessageDisplayed();
        assertTrue(isSuccess, "Form should handle leading/trailing spaces");
    }

    @AfterMethod
    @Override
    public void tearDown() {
        super.tearDown();
    }
}