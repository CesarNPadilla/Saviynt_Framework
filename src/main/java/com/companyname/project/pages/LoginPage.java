package com.companyname.project.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.companyname.project.core.EnvironmentLoader;

public class LoginPage {
    private WebDriver driver;

    // Locators
    private By usernameField = By.id("username");   // Adjust IDs according to AUT
    private By passwordField = By.xpath("//input[@placeholder='Password']");
    private By loginButton   = By.xpath("//button[normalize-space()='Login']");
    private By homePage     = By.xpath("//div[@class='homepage-container']");
    private By welcomeMsg    = By.xpath("//div[@class='welcome-message']");

    // Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // Navigate to Login Page
    public void open() {
        // Prefer JVM property if set, otherwise fallback to JSON
        String url = EnvironmentLoader.get("url");
        driver.get(url);
    }

    // Enter credentials
    public void enterCredentials(String username, String password) {
        WebElement userInput = driver.findElement(usernameField);
        WebElement passInput = driver.findElement(passwordField);

        userInput.clear();
        userInput.sendKeys(username);

        passInput.clear();
        passInput.sendKeys(password);
    }

    // Click login button
    public void submit() {
        driver.findElement(loginButton).click();
    }

    // Validate dashboard is visible
    public boolean isHomePageVisible() {
        return driver.findElements(homePage).size() > 0;
    }

    // Validate welcome message
    public boolean isWelcomeMessageDisplayed() {
        return driver.findElements(welcomeMsg).size() > 0;
    }
}