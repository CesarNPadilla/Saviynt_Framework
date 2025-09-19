Feature: Login Functionality
  As a registered user
  I want to login to the application
  So that I can access my dashboard

  @smoke @healthcheck
  Scenario: Successful login with valid credentials
    Given I navigate to the login page
    When I enter valid username and password
    And I click on the login button
    Then I should be redirected to the Home page
    And I should see a welcome message
    When I select the "Admin" menu item
    And I navigate to "Identity Repository"
    And I navigate to "Users"
    Then I should see the "Users" page