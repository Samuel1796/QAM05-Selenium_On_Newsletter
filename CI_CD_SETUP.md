# CI/CD Pipeline Setup Guide

## Overview
This project uses GitHub Actions to automatically run Selenium tests on every push and send notifications via Slack.

## Features
- ✅ Automatic test execution on push to master branch
- ✅ Multi-Java version testing (Java 11, 17)
- ✅ Detailed test reports with HTML output
- ✅ Screenshot capture on test failure
- ✅ Slack notifications for pass/fail status
- ✅ Email notifications via SMTP for pass/fail status
- ✅ Artifact retention for debugging

## Prerequisites
1. GitHub repository with GitHub Actions enabled
2. Slack workspace with admin access to create webhooks
3. Maven and Java installed locally for testing

## GitHub Actions Setup

### 1. Workflow Configuration
The CI pipeline is defined in `.github/workflows/ci.yml` and includes:

- **Trigger Events:**
  - Push to `master` branch
  - Manual trigger via `workflow_dispatch`
  - Repository dispatch events

- **Jobs:**
  - `selenium-tests`: Runs tests on Ubuntu latest with Java 11 and 17

- **Key Steps:**
  1. Checkout code
  2. Set up Java environment
  3. Install Maven dependencies
  4. Run Selenium tests with detailed logging
  5. Upload test reports and screenshots
  6. Generate test summary
  7. Send Slack and email notifications

### 2. Test Execution
Tests are executed using Maven with the Surefire plugin:
```bash
mvn clean install -DskipTests
mvn test
```

**Detailed Logging:** Tests run with `-B` (batch mode) and `-V` (verbose) flags for comprehensive GitHub Actions logs.

### 3. Test Reports
- **Format:** XML and plain text
- **Location:** `target/surefire-reports/`
- **Artifact Retention:** 30 days
- **Accessible in:** GitHub Actions > Artifacts

## Slack Integration Setup

### Step 1: Create a Slack App
1. Go to [Slack API Dashboard](https://api.slack.com/apps)
2. Click "Create New App"
3. Choose "From scratch"
4. App name: `myGitHubActions`
5. Select your workspace
6. Click "Create App"

### Step 2: Enable Incoming Webhooks
1. In the app sidebar, select "Incoming Webhooks"
2. Toggle "Activate Incoming Webhooks" to ON
3. Click "Add New Webhook to Workspace"
4. Select the channel where notifications should be sent (e.g., `#dev-notifications` or `#builds`)
5. Click "Allow"
6. Copy the generated Webhook URL

### Step 3: Add Webhook URL to GitHub
1. Go to your GitHub repository
2. Settings → Secrets and variables → Actions
3. Click "New repository secret"
4. Name: `SLACK_WEBHOOK_URL`
5. Value: Paste the Webhook URL from Step 2
6. Click "Add secret"

## Email Integration Setup (SMTP)

The workflow sends email notifications to the configured recipient after every run (success or failure), using SMTP.

### Step 1: Gmail App Password (for yoshninjas.1@gmail.com)
1. Enable 2-Step Verification on the Gmail account
2. Go to [Google Account → Security → App passwords](https://myaccount.google.com/apppasswords)
3. Create an app password for "Mail" on "Other" device
4. Copy the 16-character password

### Step 2: Add SMTP Secrets to GitHub
1. Go to your GitHub repository
2. Settings → Secrets and variables → Actions
3. Add these repository secrets:

| Secret Name    | Value                    | Description                          |
|----------------|--------------------------|--------------------------------------|
| `SMTP_HOST`    | `smtp.gmail.com`         | SMTP server address                  |
| `SMTP_PORT`    | `587`                    | SMTP port (587 for STARTTLS)         |
| `SMTP_USER`    | `yoshninjas.1@gmail.com` | Sender email address                 |
| `SMTP_PASSWORD`| *(App password from Step 1)* | Gmail app password (not regular password) |
| `EMAIL_TO`     | `sbakye1796@gmail.com`   | Recipient email address              |

### Step 3: Verify Email Setup
1. Push a commit or trigger the workflow manually
2. Check the recipient inbox (and spam folder) for the notification

### Step 4: Verify Setup
1. Push a commit to the `master` branch
2. Go to GitHub Actions and wait for the workflow to complete
3. Check your Slack channel for the notification

## Notification Format

### Success Notification
```
✅ Selenium Tests Passed
Repo: owner/repo-name
Branch: master
Java: 11 (or 17)
Commit: [link to commit]
```

### Failure Notification
```
❌ Selenium Tests Failed
Repo: owner/repo-name
Branch: master
Java: 11 (or 17)
Commit: [link to commit]
Workflow Run: [link to GitHub Actions logs]

Some tests failed. Reports and screenshots (if any) are attached.
```

## Accessing Test Reports

### In GitHub Actions
1. Go to Actions tab in GitHub
2. Select the completed workflow run
3. Scroll to "Artifacts" section
4. Download `selenium-reports-java-X` (where X is the Java version)
5. Extract and open in browser

### In Workflow Summary
- Test execution summary is appended to the workflow run summary
- Includes report location and available resources

## Maven Configuration

### pom.xml Settings
The project includes Maven plugins for test execution:

- **Maven Surefire Plugin** (v3.1.2): Runs TestNG tests
  - Generates XML and plain text reports
  - Configured for test discovery

- **Maven Compiler Plugin** (v3.11.0): Compiles Java code
  - Source: Java 11
  - Target: Java 11

## Logging Details

### Test Execution Logs
- Verbose output in GitHub Actions logs
- All test method names and results visible
- Stack traces for failures
- Summary at the end of test run

### Report Files
- `testng-results.xml`: Machine-readable test results
- `index.html`: Browser-viewable HTML report (if generated)
- `TEST-*.txt`: Plain text test reports

## Troubleshooting

### Slack Webhook Invalid
**Error:** "Webhook URL is invalid"
- **Solution:** Verify the webhook URL in `Settings → Secrets and variables`
- Check that the URL hasn't expired
- Regenerate the webhook in Slack if needed

### Tests Not Running
**Error:** "No tests found" or build fails
- **Solution:** Ensure TestNG tests are in `src/test/java`
- Check Maven configuration in `pom.xml`
- Run locally: `mvn test`

### Reports Not Generated
**Error:** Artifacts section empty
- **Solution:** Ensure tests actually run and create reports
- Check `target/surefire-reports/` directory exists
- Verify Surefire plugin configuration in pom.xml

### Build Fails on Java Version
**Error:** "Source option X is not supported"
- **Solution:** Java version mismatch - use Java 11+
- The matrix in ci.yml tests Java 11 and 17
- Ensure `maven.compiler.source` is set to 11

## Customization

### Change Notification Channel
1. In `.github/workflows/ci.yml`, update the webhook URL
2. Or create multiple webhooks for different channels
3. Add conditional steps for different channels

### Add More Java Versions
Edit `.github/workflows/ci.yml`:
```yaml
strategy:
  matrix:
    java-version: ['11', '17', '21']  # Add '21' here
```

### Change Trigger Events
Edit the `on:` section in `.github/workflows/ci.yml`:
```yaml
on:
  push:
    branches: [master, develop]  # Add branches here
  schedule:
    - cron: '0 0 * * *'  # Add nightly runs
```

## Monitoring

### GitHub Actions Dashboard
- Navigate to your repo's Actions tab
- View real-time workflow execution
- Check logs for detailed test output
- Download artifacts directly

### Slack Channel
- Notifications appear in real-time
- Click links to jump to:
  - Commit details
  - Workflow run logs
  - Test artifact downloads

## Best Practices

1. **Keep Webhooks Secure:** Never commit webhook URLs to the repository
2. **Review Test Reports:** Check reports after each failure
3. **Monitor Trends:** Track pass/fail rates over time
4. **Clean Artifacts:** GitHub automatically removes artifacts after 30 days
5. **Test Locally First:** Run `mvn clean test` locally before pushing

## Quick Reference

| Command | Purpose |
|---------|---------|
| `mvn clean install -DskipTests` | Install dependencies without running tests |
| `mvn test` | Run all tests locally |
| `mvn test -Dtest=SignUpTest` | Run specific test class |
| `mvn clean` | Remove generated files |

## Support

For issues with:
- **GitHub Actions:** Check the Actions log in GitHub
- **Slack Integration:** Verify webhook URL in repo secrets
- **Email Integration:** Verify SMTP secrets (SMTP_HOST, SMTP_PORT, SMTP_USER, SMTP_PASSWORD, EMAIL_TO)
- **Test Failures:** Download test reports from artifacts
- **Maven:** Check `pom.xml` configuration
