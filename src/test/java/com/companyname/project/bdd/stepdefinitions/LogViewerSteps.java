package com.companyname.project.bdd.stepdefinitions;

import com.companyname.project.core.DriverFactory;
import com.companyname.project.pages.LogViewerPage;

import io.cucumber.java.en.When;

public class LogViewerSteps {
    private LogViewerPage logViewerPage = new LogViewerPage(DriverFactory.getDriver());

    @When("I click on log search button")
    public void i_click_on_the_log_search_button() {
        logViewerPage.clicksearchButton();
    }
}