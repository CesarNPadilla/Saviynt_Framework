package com.companyname.project.bdd.runners;

import org.testng.annotations.DataProvider;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",          // Path to feature files
        glue = {
                "com.companyname.project.bdd.stepdefinitions", 
                "com.companyname.project.bdd.hooks"
        },
        plugin = {
                "pretty",                                  // Console output
                "html:target/cucumber-reports/cucumber.html",               // HTML report
                "json:target/cucumber-reports/cucumber.json",               // JSON report
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm", // Allure
                "summary"
        },
        monochrome = true,                                 // Clean console output
        tags = "@smoke"                                    // Run only scenarios tagged @smoke
)
public class TestRunner extends AbstractTestNGCucumberTests {
        @Override
        @DataProvider (parallel = true)
        public Object[][] scenarios() {
                return super.scenarios();
        }
}