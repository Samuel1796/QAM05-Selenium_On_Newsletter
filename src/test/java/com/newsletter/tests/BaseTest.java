package com.newsletter.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base Test class that manages WebDriver lifecycle.
 *
 * <p>This project now uses JUnit 5. Each test method gets a fresh browser instance to avoid state
 * leakage between tests (cookies, local storage, navigation state, etc.).</p>
 *
 * <p>In CI environments (for example GitHub Actions), the browser is started in headless mode when
 * the environment variable {@code CI=true} or {@code HEADLESS=true} is set.</p>
 */
public class BaseTest {
    protected WebDriver driver;
    protected static final Logger logger = Logger.getLogger(BaseTest.class.getName());

    @BeforeEach
    public void setUp() {
        logger.info("Setting up WebDriver for test.");
        WebDriverManager.chromedriver().setup();

        boolean headless =
                Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS",
                        System.getenv().getOrDefault("CI", "false")));

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            logger.info("Running Chrome in headless mode (CI/headless environment detected).");
            // Required for Linux CI runners without a display server.
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
        } else {
            logger.info("Running Chrome in headed mode.");
        }

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();

        // Implicit wait for simple element lookups; explicit waits live in the Page Objects.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        logger.info("WebDriver setup complete.");
    }

    @AfterEach
    public void tearDown() {
        // Always close the browser to prevent resource leaks.
        logger.info("Tearing down WebDriver after test.");
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver quit successfully.");
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while quitting WebDriver.", e);
            }
        }
    }
}