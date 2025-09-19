package com.companyname.project.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LogViewerPage {
    private WebDriver driver;

    // Locators
    private By searchButton = By.xpath("//button[@type='submit']");

    // Constructor
    public LogViewerPage(WebDriver driver) {
        this.driver = driver;
    }

    // Click search button
    public void clicksearchButton() {
        driver.findElement(searchButton).click();
    }
}