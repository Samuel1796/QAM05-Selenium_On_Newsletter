package com.newsletter.tests.base;

import com.newsletter.pages.SignUpPage;
import com.newsletter.pages.SuccessPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
    protected SignUpPage signUpPage;
    protected SuccessPage successPage;
    protected static final Logger logger = Logger.getLogger(BaseTest.class.getName());

    /**
     * Configure java.util.logging to log both to the console and to a per-run
     * file under target/logs/. This runs once per JVM (per Maven test run).
     */
    private static void configureRootLogging() {
        Logger rootLogger = Logger.getLogger("");

        // Avoid re-configuring if a FileHandler is already present (e.g. from another test suite).
        boolean alreadyConfigured = Arrays.stream(rootLogger.getHandlers())
                .anyMatch(h -> h instanceof FileHandler);
        if (alreadyConfigured) {
            return;
        }

        try {
            // Remove default handlers to prevent duplicate console output.
            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            rootLogger.setLevel(Level.INFO);

            // Console output for live visibility (local runs and CI logs).
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(consoleHandler);

            // Per-run log file under target/logs for traceability.
            String logsDirPath = "target/logs";
            File logsDir = new File(logsDirPath);
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            String logFilePath = logsDirPath + "/selenium-tests-" + timestamp + ".log";

            FileHandler fileHandler = new FileHandler(logFilePath, true);
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);

            rootLogger.info("Logging configured. Writing to console and " + logFilePath);
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.WARNING,
                    "Failed to configure file logging; falling back to defaults.", e);
        }
    }

    @BeforeEach
    public void setUp() {
        logger.info("Setting up WebDriver for test.");

        boolean headless =
                Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS",
                        System.getenv().getOrDefault("CI", "false")));

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            logger.info("Running Chrome in headless mode (CI/headless environment detected).");
            // Required for Linux CI runners without a display server. (Restrictions on CI pipeline)
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
        } else {
            logger.info("Running Chrome in headed mode.");
        }

        driver = new ChromeDriver(options);

        // Initialize commonly used page objects so test classes stay focused on assertions.
        signUpPage = new SignUpPage(driver);
        successPage = new SuccessPage(driver);

        // Implicit wait for simple element lookups; explicit waits live in the Page Objects.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        logger.info("WebDriver setup complete.");
    }

    static {
        configureRootLogging();
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

