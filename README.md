## Newsletter Selenium Test Suite

This repository contains an automated UI test suite for a newsletter sign-up flow, implemented with **Java**, **JUnit 5**, and **Selenium WebDriver**.  
The tests exercise the newsletter form end-to-end (valid/invalid email flows, validation messages, success page behaviour, persistence across refresh, etc.).

The project is wired into **GitHub Actions** so that the test suite runs automatically on every change to `master` and on pull requests, with rich reporting via artifacts, Slack notifications and email.

---

## Project Structure (High Level)

- **`src/test/java/com/newsletter/tests`**: JUnit 5 test classes (for example, `SignUpTest`) describing test cases for the newsletter form.
- **`src/test/java/com/newsletter/pages`**: Page Object Model (POM) classes wrapping page interactions (e.g. `SignUpPage`, `SuccessPage`).
- **`src/test/java/com/newsletter/utils`**: Utilities such as random email generators and shared helpers.
- **`.github/workflows/ci.yml`**: GitHub Actions workflow that builds the project, runs the Selenium tests, and sends notifications.

> Note: File and package names may vary slightly, but the overall responsibilities follow this pattern.

---

## Running Tests Locally

1. **Prerequisites**
   - Java 17 (or the version configured in the workflow matrix).
   - Maven installed and available on your `PATH`.
   - A compatible browser and WebDriver (for example, Chrome + ChromeDriver).

2. **Commands**
   - Install dependencies and build without tests:

     ```bash
     mvn clean install -DskipTests
     ```

   - Run the full test suite:

     ```bash
     mvn test
     ```

3. **Reports**
   - JUnit/TestNG XML reports are generated under `target/surefire-reports/`.
   - If your tests capture screenshots, they are typically stored under `target/**.png` or `screenshots/**.png`.

---

## Continuous Integration: `ci.yml` Workflow

The workflow file lives at **`.github/workflows/ci.yml`** and is named **“Selenium CI Pipeline”**.  
It defines **when** the pipeline runs, **what environment** it uses, and **how** the tests and notifications are executed.

### Triggers (`on`)

The `on` section controls when the workflow is started:

- **`push`**  
  - Runs the pipeline on every push to the `master` branch.

- **`pull_request`**  
  - Runs the pipeline on pull requests targeting `master`.  
  - This protects the main branch by running tests before merging.

- **`repository_dispatch`**  
  - Optional, for external systems to trigger a run via GitHub’s API.
  - Uses event type `newsletter-update` as a custom hook.

- **`workflow_dispatch`**  
  - Enables a manual “Run workflow” button in the GitHub Actions UI.
  - Useful for ad‑hoc test runs (e.g. to re-run tests without a new commit).

### Jobs and Runner (`jobs.selenium-tests`)

There is a single job called **`selenium-tests`**:

- **`runs-on: ubuntu-latest`**
  - Uses the latest GitHub-hosted Ubuntu runner as the execution environment.

- **`env` block (environment variables)**
  - **`SLACK_WEBHOOK_URL`**  
    Incoming webhook URL used to send Slack notifications.
  - **`SMTP_HOST`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASSWORD`**  
    SMTP configuration for email notifications (e.g. Gmail: `smtp.gmail.com:587`).
  - **`EMAIL_TO`**  
    Comma-separated list of recipient email addresses.
  - **`CI` / `HEADLESS`**  
    Flags that instruct the tests to run in CI/headless mode (e.g. headless Chrome).

  All sensitive values come from **GitHub Actions secrets** (see “Required Secrets” below).

- **`strategy` and `matrix`**
  - Currently defines a single axis:
    - `java-version: [ "17" ]`
  - `fail-fast: false` means:
    - If you later extend the matrix (e.g. multiple Java versions), a failure in one entry will not automatically cancel the others.
    - This ensures complete reporting across all configured variants.

### Steps Overview

Each step runs in order inside the `selenium-tests` job. The comments in `ci.yml` and this section correspond one-to-one.

1. **Checkout Code**
   - Uses `actions/checkout@v4`.
   - Clones the repository source code into the runner so Maven and tests can run.

2. **Set up Java**
   - Uses `actions/setup-java@v4` with:
     - `distribution: temurin`
     - `java-version` from the matrix (currently `17`)
     - `cache: maven` to speed up builds by caching dependencies.

3. **Install Dependencies**
   - Runs:

     ```bash
     mvn clean install -DskipTests
     ```

   - Compiles the project and downloads all dependencies **without** executing tests yet.

4. **Run Selenium Tests**
   - Runs the full Maven test phase:

     ```bash
     mvn test -B -V
     ```

   - Captures the Maven exit code into `TEST_EXIT_CODE` and exports it into `GITHUB_ENV`.
   - Uses `continue-on-error: true` so that later reporting and notification steps still run even when tests fail.

5. **Upload Test Reports**
   - Uses `actions/upload-artifact@v4` to upload:
     - `target/surefire-reports` as `selenium-reports-java-<version>`.
   - Artifacts are kept for **30 days** by default.

6. **Upload Screenshots**
   - Also uses `actions/upload-artifact@v4` to upload screenshot files:
     - `target/**/*.png`
     - `screenshots/**/*.png`
   - Helpful for debugging UI failures.

7. **Parse Test Results**
   - Installs `xmlstarlet` and `jq` on the runner.
   - Reads `target/surefire-reports/TEST-*.xml` and extracts:
     - Total number of test cases.
     - Passed, failed and skipped counts.
     - A compact list of failing tests (class and method names, plus messages).
   - Writes summary values into environment variables:
     - `TOTAL_TESTS`, `PASSED_TESTS`, `FAILED_TESTS`, `SKIPPED_TESTS`
   - Determines:
     - Whether the pipeline should be considered **failed** (`TEST_FAILED=true/false`).
     - The **reason** (`FAIL_REASON=test_failures` or `build_error`).

8. **Format Notification Payload**

   - Converts raw test results into human-friendly strings:
     - Replaces `PASS` / `FAIL` prefixes with ✅ / ❌.
     - Builds a `SUMMARY_LINE` like:
       - `X passed · Y failed · Z skipped · N total`
     - Creates a `STATUS_LINE` that distinguishes:
       - Build errors vs. actual test failures.

9. **Generate Test Summary (GitHub UI)**

   - Writes a Markdown summary to `GITHUB_STEP_SUMMARY` so that:
     - The workflow run page shows:
       - Java version
       - Overall status
       - Exit code
       - Test counts table
       - (Optionally) a list of failing tests.
     - Links to relevant artifacts, such as `target/surefire-reports/`.

10. **Slack Notification – Success**

    - Runs only if:
      - `TEST_FAILED == 'false'`, **and**
      - `SLACK_WEBHOOK_URL` is non-empty.
    - Uses `curl` + `jq` to send a rich Slack message containing:
      - Repository/branch/Java version/short commit SHA.
      - Summary line.
      - Top test results.
      - Link to the GitHub Actions run.

11. **Slack Notification – Failure**

    - Similar to the success step, but:
      - Triggered when `TEST_FAILED == 'true'`.
      - Emphasises:
        - Exit code.
        - Failure reason (build error vs test failures).
        - List of failing tests (where available).

12. **Email Notification – Success**

    - Uses `dawidd6/action-send-mail@v4`.
    - Runs if:
      - `TEST_FAILED == 'false'`, **and**
      - `SMTP_PASSWORD` is provided.
    - Sends a HTML email that includes:
      - Repository, branch, Java version, commit hash.
      - Test counts (passed/failed/skipped/total).
      - A preformatted block of test results.
      - A link back to the GitHub Actions run.

13. **Email Notification – Failure**

    - Same mail action, but:
      - Triggered when `TEST_FAILED == 'true'`.
      - Email body highlights:
        - Exit code.
        - Failure reason.
        - Counts and list of failing tests.

14. **Log Test Failure (Job Continues)**

    - Always runs when `TEST_FAILED == 'true'`.
    - Echoes key variables (exit code, counts, summary, etc.) into the job logs.
    - Ensures that failure information is accessible even if Slack/email are misconfigured.

---

## Required GitHub Secrets / Configuration

To have all notifications fully working, configure the following secrets in your repository or organisation:

- **Slack**
  - `SLACK_WEBHOOK_URL`

- **Email / SMTP**
  - `SMTP_HOST`
  - `SMTP_PORT`
  - `SMTP_USER`
  - `SMTP_PASSWORD`
  - `EMAIL_TO`

If these secrets are not set, the corresponding Slack/email steps are simply skipped while the rest of the CI workflow still runs and uploads artifacts.

---

## How to Extend or Modify the CI Workflow

- **Add more Java versions**
  - Update the matrix in `.github/workflows/ci.yml`:

    ```yaml
    strategy:
      fail-fast: false
      matrix:
        java-version: [ "17", "21" ]
    ```

- **Toggle notifications**
  - Remove or comment out the Slack/email steps if you only want GitHub-based reporting.

- **Adjust retention**
  - Change `retention-days` in the upload-artifact steps to suit your storage and compliance needs.

With these structures and comments in place, the CI pipeline should be easy to understand, operate, and extend for future test scenarios.

