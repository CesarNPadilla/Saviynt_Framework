// Hooks.java
package com.companyname.project.bdd.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import com.companyname.project.core.DriverFactory;
import com.companyname.project.core.BaseTest;
import com.companyname.project.core.EnvironmentLoader;
import com.companyname.project.utils.AllureEnvironmentWriter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import io.qameta.allure.Allure;

public class hooks {

    @Before(order = 0)
    public void setUp() {
        // Single initialization point
        DriverFactory.initDriver();
        BaseTest.initializeWait(); // Only sets up WebDriverWait

        // Environment setup
        String browser = System.getProperty("browser", "chrome");
        String env = System.getProperty("env", "dev");
        EnvironmentLoader.load(env, "testdata.json");
        AllureEnvironmentWriter.writeEnvironmentInfo(browser, env);
    }

    @After(order = Integer.MAX_VALUE)
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            captureScreenshot(scenario);
        }
        
        // Clean up in reverse order
        BaseTest.tearDownWait();
        DriverFactory.quitDriver();
    }

    private void captureScreenshot(Scenario scenario) {
        try {
            byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver())
                .getScreenshotAs(OutputType.BYTES);
            
            Allure.getLifecycle().addAttachment("Screenshot on Failure", "image/png", "png", screenshot);
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }
}