Feature: Health Check
    As a registered user
    I want to do A Health Check post patching
    So that I can confirm main componentents are working as expected
    -Validate application Accessibility
    -Login and navigation verification
    -Logs verification
    -Control Panel jobs check
    -Analytics check
    -Data analyzer verification

    Background: Validate application Accessibility, Login and navigation verification
        Given I navigate to the login page
        And I enter valid username and password
        And I click on the login button
        Then I should be redirected to the Home page
        And I should see a welcome message

    @healthcheck @debug
    Scenario: Logs verification
        And I select the "Admin" menu item
        And I navigate to "Admin Function"
        And I navigate to "Log Viewer"
        Then I should see the "Log Viewer" page
        And I click on log search button
        Then I should see the "Search & download application logs" page

    @healthcheck
    Scenario: Control Panel jobs check
        And I select the "Admin" menu item
        And I navigate to "Job Control Panel"
        Then I should see the "Job Control Panel" page

    @healthcheck
    Scenario: Analytics check
        And I select the "Intelligence" menu item
        And I navigate to "Analytics History"
        And I navigate to "Analytics History V2"
        Then I should see the "Analytics History" page

    @healthcheck
    Scenario: Data analyzer verification
        And I select the "Admin" menu item
        And I navigate to "Admin Function"
        And I navigate to "Data Analyzer"
        Then I should see the "Data Analyzer" page
