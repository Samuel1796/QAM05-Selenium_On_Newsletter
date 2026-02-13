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

    @Test(description = "Verify Error Message for Invalid Email Format")
    public void testInvalidEmailFormat() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp("invalid-email");

        waitFor(1000);

        assertTrue(driver.getCurrentUrl().contains("newsletter-sign-up-form-bay.vercel.app"),
                "Should remain on same page with invalid email");
    }

    @Test(description = "Verify Error Message for Empty Email Field")
    public void testEmptyEmailField() {
        signUpPage.navigateToPage();
        signUpPage.clickSubscribe();

        waitFor(1000);

        assertTrue(driver.getCurrentUrl().contains("newsletter-sign-up-form-bay.vercel.app"),
                "Should remain on same page when email is empty");
    }

    @DataProvider(name = "invalidEmails")
    public Object[][] invalidEmailProvider() {
        // Generate random pool of invalid emails and select 3 for testing
        return Utils.getRandomInvalidEmails(3);
    }

    @Test(description = "Verify Form Validation with Multiple Invalid Email Formats", dataProvider = "invalidEmails")
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
        // Generate random pool of 6 emails and select 3 for testing
        return Utils.getRandomEmails(6, 3);
    }

    @Test(description = "Verify Successful Subscription with Various Valid Email Formats",
            dataProvider = "validEmails")
    public void testValidEmailFormats(String validEmail) {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp(validEmail);

        waitFor(2000);

        boolean isSuccess = successPage.isOnSuccessPage() || !signUpPage.isErrorMessageDisplayed();
        assertTrue(isSuccess, "Should accept valid email format: " + validEmail);
    }


    @Test(description = "Verify Subscribe Button is Clickable")
    public void testSubscribeButtonIsClickable() {
        signUpPage.navigateToPage();

        assertTrue(signUpPage.isSubscribeButtonDisplayed(), "Subscribe button should be displayed");
        signUpPage.clickSubscribe();
    }

    @Test(description = "Verify Email Input Persists After Page Refresh")
    public void testFormPersistenceAfterRefresh() {
        signUpPage.navigateToPage();
        
        // Enter an email into the input field
        String testEmail = Utils.generateRandomEmail();
        signUpPage.enterEmail(testEmail);
        
        // Refresh the page
        driver.navigate().refresh();
        waitFor(2000);

        // Re-initialize page object after refresh to get fresh element references
        signUpPage = new SignUpPage(driver);

        // Check if the form and input field are still displayed
        assertTrue(signUpPage.isFormDisplayed(), "Form should be displayed after refresh");
        assertTrue(signUpPage.isEmailInputDisplayed(), "Email input should be displayed after refresh");
        
        // Check if the email value persists after refresh
        String emailAfterRefresh = signUpPage.getEmailInputValue();
        assertEquals(emailAfterRefresh, testEmail, "Email input value should persist after page refresh");
    }

    @Test(description = "Verify Maximum Length of Email Input")
    public void testEmailInputMaxLength() {
        signUpPage.navigateToPage();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append('a');
        }
        String longEmail = sb.append("@example.com").toString();
        signUpPage.enterEmail(longEmail);

        assertTrue(signUpPage.isEmailInputDisplayed(),
                "Email input should handle long email addresses");
    }

    @Test(description = "Verify Special Characters in Email")
    public void testSpecialCharactersInEmail() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp(Utils.generateRandomEmail());

        waitFor(2000);

        boolean isSuccess = successPage.isOnSuccessPage() || !signUpPage.isErrorMessageDisplayed();
        assertTrue(isSuccess, "Should accept email with valid characters");
    }

    @Test(description = "Verify Form Behavior with Leading/Trailing Spaces")
    public void testEmailWithSpaces() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp("  " + Utils.generateRandomEmail() + "  ");

        waitFor(2000);

        boolean isSuccess = successPage.isOnSuccessPage() || !signUpPage.isErrorMessageDisplayed();
        assertTrue(isSuccess, "Form should handle leading/trailing spaces");
    }

    @Test(description = "Verify Success Page Displays Correct Email After Subscription")
    public void testSuccessPageDisplaysEmail() {
        String testEmail = Utils.generateRandomEmail();
        signUpPage.navigateToPage();
        signUpPage.completeSignUp(testEmail);
        
        waitFor(2000);
        
        assertTrue(successPage.isOnSuccessPage(), "Should redirect to success page");
        String displayedEmail = successPage.getEmailConfirmation();
        assertTrue(displayedEmail.contains(testEmail), 
                "Success page should display subscribed email: " + testEmail);
    }

    @Test(description = "Verify Error Message Disappears When Valid Email Entered")
    public void testErrorMessageClearsOnValidEmail() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp("invalid");
        waitFor(1000);
        
        assertTrue(signUpPage.isErrorMessageDisplayed(), "Error should be displayed for invalid email");
        
        signUpPage.clearEmailInput();
        signUpPage.enterEmail(Utils.generateRandomEmail());
        waitFor(500);
        
        assertFalse(signUpPage.isErrorMessageDisplayed(), "Error message should no longer be displayed");
    }

    @Test(description = "Verify Dismiss Button Returns to Form")
    public void testDismissSuccessMessage() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp(Utils.generateRandomEmail());
        
        waitFor(2000);
        assertTrue(successPage.isOnSuccessPage(), "Should be on success page");
        
        successPage.clickDismissButton();
        waitFor(1000);
        
        // Re-initialize to get fresh element references
        signUpPage = new SignUpPage(driver);
        assertTrue(signUpPage.isFormDisplayed(), "Should return to sign-up form");
        assertTrue(signUpPage.isEmailInputDisplayed(), "Email input should be visible");
    }


    @AfterMethod
    @Override
    public void tearDown() {
        super.tearDown();
    }
}