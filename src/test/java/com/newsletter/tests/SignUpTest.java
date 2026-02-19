package com.newsletter.tests;


import com.newsletter.utils.Utils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SignUpTest extends BaseTest {
    private static final Logger logger = Logger.getLogger(SignUpTest.class.getName());

    /**
     * Asserts that a client-side validation error is shown for invalid submission
     * attempts.
     *
     * <p>
     * This uses keyword-based matching rather than an exact string to keep the test
     * robust to minor
     * copy tweaks (e.g. punctuation). It still validates that the UI is showing a
     * meaningful message.
     * </p>
     *
     * @param expectedKeywords one or more keywords expected to appear in the error
     *                         text (case-insensitive)
     */
    private void assertValidationErrorContains(String... expectedKeywords) {
        assertValidationErrorContainsForInput(null, expectedKeywords);
    }



    /**
     * Same as {@link #assertValidationErrorContains(String...)} but includes the
     * input value in failure
     * messages for traceability when used in parameterized tests.
     *
     * @param inputValue       the input that triggered validation (e.g. invalid
     *                         email), or null to omit
     * @param expectedKeywords one or more keywords expected to appear in the error
     *                         text (case-insensitive)
     */
    private void assertValidationErrorContainsForInput(String inputValue, String... expectedKeywords) {
        String prefix = (inputValue != null ? "Input: \"" + inputValue + "\". " : "");
        assertTrue(signUpPage.waitForErrorMessageToBeVisible(),
                prefix + "Expected a validation error message to appear, but it did not.");

        String error = signUpPage.getNormalizedErrorMessage();
        assertFalse(error.isBlank(), prefix + "Validation error message should not be blank.");

        String lower = error.toLowerCase();
        for (String keyword : expectedKeywords) {
            assertTrue(lower.contains(keyword.toLowerCase()),
                    prefix + "Validation error message should contain '" + keyword + "' but was: " + error);
        }
    }

    /**
     * Verifies that an invalid email format does not allow subscription.
     *
     * <p>
     * Expected result: user stays on the sign-up form. (Optionally, UI shows a
     * validation error.)
     * </p>
     */
    @Test
    @DisplayName("[TC001] Verify Error Message for Invalid Email Format")
    @Tag("signup")
    @Tag("negative")
    public void testInvalidEmailFormat() {
        logger.info("Running testInvalidEmailFormat");
        signUpPage.navigateToPage();
        signUpPage.completeSignUp("invalid-email");
        assertTrue(signUpPage.isOnSignUpPage(),
                "Should remain on sign-up form with invalid email");

        // Typical copy includes "valid" + "email" (e.g., "Valid email required").
        assertValidationErrorContains("email", "valid");
    }



    /**
     * Verifies that submitting the form with an empty email field is blocked by
     * validation.
     *
     * <p>
     * Expected result: user remains on the sign-up form (no success navigation).
     * </p>
     */
    @Test
    @DisplayName("[TC002] Verify Error Message for Empty Email Field")
    @Tag("signup")
    @Tag("negative")
    public void testEmptyEmailField() {
        logger.info("Running testEmptyEmailField");
        signUpPage.navigateToPage();
        signUpPage.clickSubscribe();
        assertTrue(signUpPage.isOnSignUpPage(),
                "Should remain on sign-up form when email is empty");
        // Typical copy includes "email" and "required" for empty submissions.
        assertValidationErrorContains("email", "required");
    }



    /**
     * Provides a small random subset of invalid email strings for negative testing.
     *
     * <p>
     * We keep the dataset small to balance coverage and runtime in CI.
     * </p>
     *
     * @return stream of invalid email inputs
     */
    static Stream<Arguments> invalidEmailProvider() {
        // Generate random pool of invalid emails and select 3 for testing
        Object[][] raw = Utils.getRandomInvalidEmails(3);
        return Stream.of(raw).map(args -> Arguments.of(args[0]));
    }

    /**
     * Verifies that multiple invalid email patterns are rejected.
     *
     * <p>
     * Expected result: user stays on the sign-up form for every invalid input.
     * </p>
     *
     * @param invalidEmail invalid email string supplied by the data provider
     */
    @ParameterizedTest(name = "Invalid email should be rejected: {0}")
    @MethodSource("invalidEmailProvider")
    @DisplayName("[TC003] Verify Form Validation with Multiple Invalid Email Formats")
    @Tag("signup")
    @Tag("negative")
    public void testMultipleInvalidEmailFormats(String invalidEmail) {
        logger.info(() -> "Running testMultipleInvalidEmailFormats with invalidEmail=" + invalidEmail);
        signUpPage.navigateToPage();
        signUpPage.enterEmail(invalidEmail);
        signUpPage.clickSubscribe();

        // Wait for validation error to appear (this is handled by
        // assertValidationErrorContains)
        // but first ensure we're still on the sign-up page
        assertTrue(signUpPage.isOnSignUpPage(),
                "Should remain on sign-up form with invalid email: " + invalidEmail);
        assertValidationErrorContainsForInput(invalidEmail, "email");
    }

    /**
     * Provides a random subset of valid email strings for positive coverage.
     *
     * @return stream of valid email inputs
     */
    static Stream<Arguments> validEmailProvider() {
        // Generate random pool of 6 emails and select 3 for testing
        Object[][] raw = Utils.getRandomEmails(6, 3);
        return Stream.of(raw).map(args -> Arguments.of(args[0]));
    }

    /**
     * Verifies that multiple valid email patterns are accepted.
     *
     * <p>
     * Expected result: user reaches the success screen for each valid email.
     * </p>
     *
     * @param validEmail valid email string supplied by the data provider
     */
    @ParameterizedTest(name = "Valid email should be accepted: {0}")
    @MethodSource("validEmailProvider")
    @DisplayName("[TC003] Verify Successful Subscription with Various Valid Email Formats")
    @Tag("signup")
    @Tag("positive")
    public void testValidEmailFormats(String validEmail) {
        logger.info(() -> "Running testValidEmailFormats with validEmail=" + validEmail);
        signUpPage.navigateToPage();
        signUpPage.completeSignUp(validEmail);
        assertTrue(successPage.isOnSuccessPage(), "Should accept valid email format: " + validEmail);
    }

    /**
     * Sanity check that the subscribe button is present and can be clicked.
     *
     * <p>
     * This is a lightweight UI smoke test (it does not assert success).
     * </p>
     */
    @Test
    @DisplayName("[TC004] Verify Subscribe Button is Clickable")
    @Tag("signup")
    @Tag("smoke")
    public void testSubscribeButtonIsClickable() {
        logger.info("Running testSubscribeButtonIsClickable");
        signUpPage.navigateToPage();

        assertTrue(signUpPage.isSubscribeButtonDisplayed(), "Subscribe button should be displayed");
        signUpPage.clickSubscribe();
    }


    /**
     * Verifies that the email field can accept a long email string without breaking
     * the UI.
     *
     * <p>
     * Improvement opportunity: assert {@code maxlength} or a specific validation
     * message if the product
     * defines strict limits.
     * </p>
     */
    @Test
    @DisplayName("[TC005] Verify Maximum Length of Email Input")
    @Tag("signup")
    @Tag("regression")
    public void testEmailInputMaxLength() {
        logger.info("Running testEmailInputMaxLength");
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

    /**
     * Verifies that commonly valid email characters are accepted (e.g. dot and plus
     * tag).
     *
     * <p>
     * Expected result: valid email variant should reach the success screen.
     * </p>
     */
    @Test
    @DisplayName("[TC006] Verify Special Characters in Email")
    @Tag("signup")
    @Tag("positive")
    public void testSpecialCharactersInEmail() {
        logger.info("Running testSpecialCharactersInEmail");
        signUpPage.navigateToPage();
        // Explicitly use a realistic "special" email pattern rather than a random
        // generator.
        signUpPage.completeSignUp("john.doe+tag@example.com");
        assertTrue(successPage.isOnSuccessPage(), "Should accept email with valid characters");
    }

    /**
     * Verifies the form behaviour when users paste an email with leading/trailing
     * spaces.
     *
     * <p>
     * Expected result: app trims input (or otherwise accepts it) and successfully
     * subscribes.
     * </p>
     */
    @Test
    @DisplayName("[TC007] Verify Form Behavior with Leading/Trailing Spaces")
    @Tag("signup")
    @Tag("positive")
    public void testEmailWithSpaces() {
        logger.info("Running testEmailWithSpaces");
        signUpPage.navigateToPage();
        signUpPage.completeSignUp("  " + Utils.generateRandomEmail() + "  ");
        assertTrue(successPage.isOnSuccessPage(), "Form should handle leading/trailing spaces");
    }

    /**
     * Verifies that the success screen reflects the subscribed email back to the
     * user.
     *
     * <p>
     * Expected result: confirmation text contains the input email.
     * </p>
     */
    @Test
    @DisplayName("[TC008] Verify Success Page Displays Correct Email After Subscription")
    @Tag("signup")
    @Tag("regression")
    public void testSuccessPageDisplaysEmail() {
        logger.info("Running testSuccessPageDisplaysEmail");
        String testEmail = Utils.generateRandomEmail();
        signUpPage.navigateToPage();
        signUpPage.completeSignUp(testEmail);

        assertTrue(successPage.isOnSuccessPage(), "Should redirect to success page");
        String displayedEmail = successPage.getEmailConfirmation();
        assertTrue(displayedEmail.contains(testEmail),
                "Success page should display subscribed email: " + testEmail);
    }

    /**
     * Verifies that the validation error state is not "sticky".
     *
     * <p>
     * Flow: submit invalid email → error appears → replace with valid email → error
     * disappears.
     * </p>
     */
    @Test
    @DisplayName("[TC009] Verify Error Message Disappears When Valid Email Entered")
    @Tag("signup")
    @Tag("negative")
    public void testErrorMessageClearsOnValidEmail() {
        logger.info("Running testErrorMessageClearsOnValidEmail");
        signUpPage.navigateToPage();
        signUpPage.completeSignUp("invalid");
        assertTrue(signUpPage.waitForErrorMessageToBeVisible(), "Error should be displayed for invalid email");

        signUpPage.clearEmailInput();
        signUpPage.enterEmail(Utils.generateRandomEmail());
        assertTrue(signUpPage.waitForErrorMessageToDisappear(), "Error message should no longer be displayed");
    }


}
