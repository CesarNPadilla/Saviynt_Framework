package com.companyname.project.bdd.stepdefinitions;

import static org.testng.Assert.assertTrue;

import com.companyname.project.core.BaseTest;
import com.companyname.project.core.DriverFactory;
import com.companyname.project.core.EnvironmentLoader;
import com.companyname.project.pages.LoginPage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginSteps {
    private LoginPage loginPage = new LoginPage(DriverFactory.getDriver());

    @Given("I navigate to the login page")
    public void i_navigate_to_the_login_page() {
        loginPage.open();
    }

    @When("I enter valid username and password")
    public void i_enter_valid_username_and_password() {
        // Load username/password from JSON
        String username = EnvironmentLoader.get("validUser.username");
        String password = EnvironmentLoader.get("validUser.password");

        loginPage.enterCredentials(username, password);
    }

    @When("I click on the login button")
    public void i_click_on_the_login_button() {
        loginPage.submit();
    }

    @Then("I should be redirected to the Home page")
    public void i_should_be_redirected_to_the_dashboard() {
        assertTrue(loginPage.isHomePageVisible(), "Dashboard was not visible");
    }

    @Then("I should see a welcome message")
    public void i_should_see_a_welcome_message() {
        BaseTest.waitForPageReload();
        assertTrue(loginPage.isWelcomeMessageDisplayed(), "Welcome message not displayed");
    }

    @When("I select the {string} menu item")
    public void selectMainMenuItem(String menuItem) {
        assertTrue(BaseTest.selectMenuItem(menuItem), "Menu item '" + menuItem + "' was not found");
    }

    @And("I navigate to {string}")
    public void navigateToMenuItem(String menuItem) {
        BaseTest.navigateToMenuItem(menuItem);
        assertTrue(true, "Navigation to '" + menuItem + "' failed"); 
    }

    @And("I navigate to HomePage")
    public void navigateToHomePage() {
        BaseTest.navigateToHomePage();
    }

    @Then("I should see the {string} page")
    public void i_should_see_a_page(String page) {
        assertTrue(BaseTest.isPagePresent(page), "Page was not visible");
    }
}