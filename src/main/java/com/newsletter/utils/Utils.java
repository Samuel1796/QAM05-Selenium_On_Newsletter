package com.newsletter.utils;

import java.util.*;

/**
 * Test utility helpers.
 *
 * <p>These helpers generate random test data (valid/invalid emails) to reduce duplication in tests.
 * Randomized data improves coverage but can reduce reproducibility; therefore, the datasets are kept
 * small and constrained to realistic patterns.</p>
 */
public class Utils {

    private static final Random RANDOM = new Random();

    // Basic email domains
    private static final String[] DOMAINS = {
            "gmail.com", "yahoo.com", "outlook.com", "example.com"
    };

    // Basic username prefixes
    private static final String[] USERNAMES = {
            "user", "test", "john", "jane", "demo"
    };

    /**
     * Generate a random valid email address.
     *
     * @return a syntactically valid email using a known domain list
     */
    public static String generateRandomEmail() {
        String username = USERNAMES[RANDOM.nextInt(USERNAMES.length)] + RANDOM.nextInt(1000);
        String domain = DOMAINS[RANDOM.nextInt(DOMAINS.length)];
        return username + "@" + domain;
    }

    /**
     * Generate a pool of random (unique) valid emails.
     *
     * @param size number of unique emails to generate
     * @return a list of unique emails
     */
    public static List<String> generateEmailPool(int size) {
        Set<String> emails = new HashSet<>();
        while (emails.size() < size) {
            emails.add(generateRandomEmail());
        }
        return new ArrayList<>(emails);
    }

    /**
     * Build a TestNG {@code Object[][]} data-provider payload containing a random subset of emails.
     *
     * @param poolSize how many emails to generate initially
     * @param selectionSize how many emails to return (max = poolSize)
     * @return {@code Object[][]} suitable for {@code @DataProvider}
     */
    public static Object[][] getRandomEmails(int poolSize, int selectionSize) {
        List<String> pool = generateEmailPool(poolSize);
        Collections.shuffle(pool);

        int actualSize = Math.min(selectionSize, pool.size());
        Object[][] data = new Object[actualSize][1];

        for (int i = 0; i < actualSize; i++) {
            data[i][0] = pool.get(i);
        }
        return data;
    }

    /**
     * Generate a random invalid email.
     *
     * <p>Invalid formats are intentionally diverse (missing parts, illegal characters, etc.) to
     * exercise client-side validation and backend constraints.</p>
     *
     * @return a syntactically invalid email string
     */
    public static String generateInvalidEmail() {
        String username = USERNAMES[RANDOM.nextInt(USERNAMES.length)] + RANDOM.nextInt(1000);
        String domain = DOMAINS[RANDOM.nextInt(DOMAINS.length)];

        String[] invalidFormats = {
                username, // No @ symbol or domain
                username + "@", // Missing domain
                "@" + domain, // Missing username
                username + " " + "@" + domain, // Space in username
                username + "@" + domain + ".", // Trailing dot in domain
                username + ".." + "@" + domain, // Double dot in username
                "." + username + "@" + domain, // Leading dot in username
                username + "@." + domain, // Leading dot in domain part
                username + "@" + domain + "-, " // Invalid character in domain
        };

        return invalidFormats[RANDOM.nextInt(invalidFormats.length)];
    }

    /**
     * Build a TestNG {@code Object[][]} data-provider payload containing invalid emails.
     *
     * @param count number of invalid emails
     * @return {@code Object[][]} suitable for {@code @DataProvider}
     */
    public static Object[][] getRandomInvalidEmails(int count) {
        Set<String> invalidEmails = new HashSet<>();
        while (invalidEmails.size() < count) {
            invalidEmails.add(generateInvalidEmail());
        }

        Object[][] data = new Object[count][1];
        int i = 0;
        for (String email : invalidEmails) {
            data[i++][0] = email;
        }
        return data;
    }
}