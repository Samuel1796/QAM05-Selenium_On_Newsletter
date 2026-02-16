package com.newsletter.tests;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * Base Test class that manages WebDriver lifecycle.
 *
 * <p>This project uses TestNG. Each test method gets a fresh browser instance to avoid state leakage
 * between tests (cookies, local storage, navigation state, etc.).</p>
 *
 * <p>In CI environments (for example GitHub Actions), the browser is started in headless mode when
 * the environment variable {@code CI=true} or {@code HEADLESS=true} is set.</p>
 */
public class BaseTest {
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        boolean headless =
                Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS",
                        System.getenv().getOrDefault("CI", "false")));

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            // Required for Linux CI runners without a display server.
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
        }

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();



        // Implicit wait for simple element lookups; explicit waits live in the Page Objects.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @AfterMethod
    public void tearDown() {
        // Always close the browser to prevent resource leaks.
        if (driver != null) {
            driver.quit();
        }
    }


}