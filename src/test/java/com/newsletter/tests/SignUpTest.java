package com.newsletter.tests;

import com.newsletter.pages.SignUpPage;
import com.newsletter.pages.SuccessPage;
import com.newsletter.utils.Utils;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 * End-to-end UI tests for the Newsletter Sign-up Form.
 *
 * <p>These tests are written against a public demo app and focus on realistic user flows:
 * valid subscription, client-side validation, and success screen behaviour.</p>
 *
 * <p><strong>Design notes:</strong></p>
 * <ul>
 *   <li>Tests interact with the UI via Page Objects (`SignUpPage`, `SuccessPage`) to keep locators
 *   and wait logic out of the test layer (DRY).</li>
 *   <li>We avoid hard sleeps and instead rely on explicit waits implemented inside the Page Objects
 *   for stability in CI environments.</li>
 *   <li>Each test uses a fresh browser from `BaseTest` to prevent state leakage between tests.</li>
 * </ul>
 */
public class SignUpTest extends BaseTest {
    protected SignUpPage signUpPage;
    protected SuccessPage successPage;

    /**
     * Asserts that a client-side validation error is shown for invalid submission attempts.
     *
     * <p>This uses keyword-based matching rather than an exact string to keep the test robust to minor
     * copy tweaks (e.g. punctuation). It still validates that the UI is showing a meaningful message.</p>
     *
     * @param expectedKeywords one or more keywords expected to appear in the error text (case-insensitive)
     */
    private void assertValidationErrorContains(String... expectedKeywords) {
        assertTrue(signUpPage.waitForErrorMessageToBeVisible(),
                "Expected a validation error message to appear, but it did not.");

        String error = signUpPage.getNormalizedErrorMessage();
        assertFalse(error.isBlank(), "Validation error message should not be blank.");

        String lower = error.toLowerCase();
        for (String keyword : expectedKeywords) {
            assertTrue(lower.contains(keyword.toLowerCase()),
                    "Validation error message should contain '" + keyword + "' but was: " + error);
        }
    }

    @BeforeMethod
    @Override
    public void setUp() {
        super.setUp();
        signUpPage = new SignUpPage(driver);
        successPage = new SuccessPage(driver);
    }




    @Test(description = "Verify Successful Newsletter Subscription with Valid Email")
    /**
     * Verifies that a syntactically valid email results in a successful subscription.
     *
     * <p>Expected result: user is redirected to the success screen (or success content becomes visible).</p>
     */
    public void testSuccessfulSubscriptionWithValidEmail() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp(Utils.generateRandomEmail());
        assertTrue(successPage.isOnSuccessPage(), "Subscription should be successful with valid email");
    }

    @Test(description = "Verify Error Message for Invalid Email Format")
    /**
     * Verifies that an invalid email format does not allow subscription.
     *
     * <p>Expected result: user stays on the sign-up form. (Optionally, UI shows a validation error.)</p>
     */
    public void testInvalidEmailFormat() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp("invalid-email");
        assertTrue(signUpPage.isOnSignUpPage(),
                "Should remain on sign-up form with invalid email");
        // Typical copy includes "valid" + "email" (e.g., "Valid email required").
        assertValidationErrorContains("email", "valid");
    }

    @Test(description = "Verify Error Message for Empty Email Field")
    /**
     * Verifies that submitting the form with an empty email field is blocked by validation.
     *
     * <p>Expected result: user remains on the sign-up form (no success navigation).</p>
     */
    public void testEmptyEmailField() {
        signUpPage.navigateToPage();
        signUpPage.clickSubscribe();
        assertTrue(signUpPage.isOnSignUpPage(),
                "Should remain on sign-up form when email is empty");
        // Typical copy includes "email" and "required" for empty submissions.
        assertValidationErrorContains("email", "required");
    }

    @DataProvider(name = "invalidEmails")
    /**
     * Provides a small random subset of invalid email strings for negative testing.
     *
     * <p>We keep the dataset small to balance coverage and runtime in CI.</p>
     *
     * @return TestNG data provider containing invalid email inputs
     */
    public Object[][] invalidEmailProvider() {
        // Generate random pool of invalid emails and select 3 for testing
        return Utils.getRandomInvalidEmails(3);
    }

    @Test(description = "Verify Form Validation with Multiple Invalid Email Formats", dataProvider = "invalidEmails")
    /**
     * Verifies that multiple invalid email patterns are rejected.
     *
     * <p>Expected result: user stays on the sign-up form for every invalid input.</p>
     *
     * @param invalidEmail invalid email string supplied by the data provider
     */
    public void testMultipleInvalidEmailFormats(String invalidEmail) {
        signUpPage.navigateToPage();
        signUpPage.enterEmail(invalidEmail);
        signUpPage.clickSubscribe();
        assertTrue(signUpPage.isOnSignUpPage(),
                "Should remain on sign-up form with invalid email: " + invalidEmail);
        assertValidationErrorContains("email");
    }

    @DataProvider(name = "validEmails")
    /**
     * Provides a random subset of valid email strings for positive coverage.
     *
     * @return TestNG data provider containing valid email inputs
     */
    public Object[][] validEmailProvider() {
        // Generate random pool of 6 emails and select 3 for testing
        return Utils.getRandomEmails(6, 3);
    }

    @Test(description = "Verify Successful Subscription with Various Valid Email Formats",
            dataProvider = "validEmails")
    /**
     * Verifies that multiple valid email patterns are accepted.
     *
     * <p>Expected result: user reaches the success screen for each valid email.</p>
     *
     * @param validEmail valid email string supplied by the data provider
     */
    public void testValidEmailFormats(String validEmail) {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp(validEmail);
        assertTrue(successPage.isOnSuccessPage(), "Should accept valid email format: " + validEmail);
    }


    @Test(description = "Verify Subscribe Button is Clickable")
    /**
     * Sanity check that the subscribe button is present and can be clicked.
     *
     * <p>This is a lightweight UI smoke test (it does not assert success).</p>
     */
    public void testSubscribeButtonIsClickable() {
        signUpPage.navigateToPage();

        assertTrue(signUpPage.isSubscribeButtonDisplayed(), "Subscribe button should be displayed");
        signUpPage.clickSubscribe();
    }

    @Test(description = "Verify Email Input Persists After Page Refresh")
    /**
     * Verifies input behaviour across a browser refresh.
     *
     * <p><strong>Note:</strong> persisting values across refresh is a product decision. Many apps clear the form
     * on refresh. If your target behaviour is to clear inputs, change the expected assertion accordingly.</p>
     */
    public void testFormPersistenceAfterRefresh() {
        signUpPage.navigateToPage();
        
        // Enter an email into the input field
        String testEmail = Utils.generateRandomEmail();
        signUpPage.enterEmail(testEmail);

        // Refresh the page
        driver.navigate().refresh();

        // Re-initialize page object after refresh to get fresh element references
        signUpPage = new SignUpPage(driver);

        // Check if the input is visible and page returned to sign-up form context
        assertTrue(signUpPage.isEmailInputDisplayed(), "Email input should be displayed after refresh");
        assertTrue(signUpPage.isFormDisplayed() || signUpPage.isEmailInputDisplayed(),
                "Form should be displayed after refresh");
        
        // Check if the email value persists after refresh
        String emailAfterRefresh = signUpPage.getEmailInputValue();
        assertEquals(emailAfterRefresh, testEmail, "Email input value should persist after page refresh");
    }

    @Test(description = "Verify Maximum Length of Email Input")
    /**
     * Verifies that the email field can accept a long email string without breaking the UI.
     *
     * <p>Improvement opportunity: assert {@code maxlength} or a specific validation message if the product
     * defines strict limits.</p>
     */
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
    /**
     * Verifies that commonly valid email characters are accepted (e.g. dot and plus tag).
     *
     * <p>Expected result: valid email variant should reach the success screen.</p>
     */
    public void testSpecialCharactersInEmail() {
        signUpPage.navigateToPage();
        // Explicitly use a realistic "special" email pattern rather than a random generator.
        signUpPage.completeSignUp("john.doe+tag@example.com");
        assertTrue(successPage.isOnSuccessPage(), "Should accept email with valid characters");
    }

    @Test(description = "Verify Form Behavior with Leading/Trailing Spaces")
    /**
     * Verifies the form behaviour when users paste an email with leading/trailing spaces.
     *
     * <p>Expected result: app trims input (or otherwise accepts it) and successfully subscribes.</p>
     */
    public void testEmailWithSpaces() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp("  " + Utils.generateRandomEmail() + "  ");
        assertTrue(successPage.isOnSuccessPage(), "Form should handle leading/trailing spaces");
    }

    @Test(description = "Verify Success Page Displays Correct Email After Subscription")
    /**
     * Verifies that the success screen reflects the subscribed email back to the user.
     *
     * <p>Expected result: confirmation text contains the input email.</p>
     */
    public void testSuccessPageDisplaysEmail() {
        String testEmail = Utils.generateRandomEmail();
        signUpPage.navigateToPage();
        signUpPage.completeSignUp(testEmail);

        assertTrue(successPage.isOnSuccessPage(), "Should redirect to success page");
        String displayedEmail = successPage.getEmailConfirmation();
        assertTrue(displayedEmail.contains(testEmail), 
                "Success page should display subscribed email: " + testEmail);
    }

    @Test(description = "Verify Error Message Disappears When Valid Email Entered")
    /**
     * Verifies that the validation error state is not "sticky".
     *
     * <p>Flow: submit invalid email → error appears → replace with valid email → error disappears.</p>
     */
    public void testErrorMessageClearsOnValidEmail() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp("invalid");
        assertTrue(signUpPage.waitForErrorMessageToBeVisible(), "Error should be displayed for invalid email");

        signUpPage.clearEmailInput();
        signUpPage.enterEmail(Utils.generateRandomEmail());
        assertTrue(signUpPage.waitForErrorMessageToDisappear(), "Error message should no longer be displayed");
    }

    @Test(description = "Verify Dismiss Button Returns to Form")
    /**
     * Verifies that the "Dismiss" action on the success screen returns the user to the sign-up form.
     *
     * <p>Expected result: sign-up form becomes visible again after dismissal.</p>
     */
    public void testDismissSuccessMessage() {
        signUpPage.navigateToPage();
        signUpPage.completeSignUp(Utils.generateRandomEmail());
        assertTrue(successPage.isOnSuccessPage(), "Should be on success page");

        successPage.clickDismissButton();

        // Re-initialize to get fresh element references
        signUpPage = new SignUpPage(driver);
        assertTrue(signUpPage.isOnSignUpPage(), "Should return to sign-up form");
    }


    @AfterMethod
    @Override
    public void tearDown() {
        super.tearDown();
    }
}
