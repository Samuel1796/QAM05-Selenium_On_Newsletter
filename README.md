## Newsletter Selenium Test Suite

This repository contains an automated UI test suite for a newsletter sign-up experience, implemented with **Java**, **JUnit 5**, and **Selenium WebDriver**.  
The tests exercise the newsletter form end-to-end (valid/invalid email flows, validation messages, success page behaviour, success screen, and more).

The suite is designed to be:

- **Fast feedback** for UI changes via a GitHub Actions CI pipeline.
- **Readable and maintainable** using the Page Object Model pattern.
- **Extensible** so new newsletter scenarios can be added easily.

---

## Tech Stack

- **Language**: Java (17)
- **Test framework**: JUnit 5
- **UI automation**: Selenium WebDriver
- **Build tool**: Maven
- **CI**: GitHub Actions

---

## Project Structure

- **`src/test/java/com/newsletter/tests`**
  - JUnit 5 test classes (for example, `SignUpTest`) that describe end-to-end scenarios for the newsletter form.

- **`src/test/java/com/newsletter/pages`**
  - Page Object Model (POM) classes that wrap interactions with the UI (e.g. `SignUpPage`, `SuccessPage`).
  - Each page exposes clear methods like `navigateToPage()`, `enterEmail()`, `clickSubscribe()`, `isOnSuccessPage()`, etc.

- **`src/test/java/com/newsletter/utils`**
  - Utility classes such as random email generators and shared helpers used across tests.

- **`.github/workflows/ci.yml`**
  - GitHub Actions workflow that builds the project, runs the Selenium tests, and publishes reports/notifications.
  - Detailed documentation of this workflow lives in `CI_WORKFLOW.md`.

> File and package names may vary slightly, but the responsibilities follow this layout: tests → pages → utilities → CI.

---

## Getting Started

### Prerequisites

- Java 17 (or the version configured in `.github/workflows/ci.yml`)
- Maven installed and available on your `PATH`
- A compatible browser and WebDriver (for example, Chrome + ChromeDriver)

### Install Dependencies & Build (without tests)

```bash
mvn clean install -DskipTests
```

### Run the Full Test Suite

```bash
mvn test
```

### Test Reports & Artifacts

- XML reports: `target/surefire-reports/`
- Screenshots (if captured by tests): usually under `target/**/*.png` or `screenshots/**/*.png`

---

## CI / CD Overview

This project includes an automated pipeline defined in `.github/workflows/ci.yml` that:

- Runs on pushes and pull requests to `master`
- Builds the Maven project and executes the Selenium test suite
- Uploads test reports and screenshots as GitHub Actions artifacts
- Optionally sends Slack and email notifications with rich test summaries

For a deep dive into how the workflow is structured and how each step works, see **`CI_WORKFLOW.md`**.

---

## Extending the Test Suite

- **Add new scenarios**
  - Create a new test class under `src/test/java/com/newsletter/tests`.
  - Reuse or extend existing page objects under `src/test/java/com/newsletter/pages`.

- **Reuse utilities**
  - Use helpers in `com.newsletter.utils` (e.g. random email generators) to keep tests concise.

- **Follow existing patterns**
  - Prefer clear test names, `@DisplayName` annotations, and tags (e.g. `@Tag("signup")`, `@Tag("regression")`) for consistency.

---

## Contributing

- Keep tests deterministic and independent (no shared mutable state between tests).
- Prefer descriptive test names and clear assertions that explain **why** a scenario fails.
- Ensure new tests run successfully both **locally** and via the **CI workflow** before opening a pull request.

