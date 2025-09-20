package com.companyname.project.core;

import java.time.Duration;
import java.util.Objects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void initDriver() {
        if (Objects.isNull(driver.get())) {
            String browser = System.getProperty("browser", "chrome");
            boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
            int implicitWait = Integer.parseInt(System.getProperty("implicitWait", "10"));
            int pageLoadTimeout = Integer.parseInt(System.getProperty("pageLoadTimeout", "30"));

            // Force headless if running inside GitLab CI
            boolean isCI = System.getenv("CI") != null;
            if (isCI) {
                headless = true;
            }

            switch (browser.toLowerCase()) {
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions ffOptions = new FirefoxOptions();
                    if (headless) {
                        ffOptions.addArguments("--headless");
                    }
                    driver.set(new FirefoxDriver(ffOptions));
                    break;

                case "chrome":
                default:
                    WebDriverManager.chromedriver()
                        //.browserVersion("139.0.7258.128") // explicitly match your Chrome version
                        .setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-logging"});
                    if (headless) {
                        chromeOptions.addArguments("--headless=new");
                        chromeOptions.addArguments("--no-sandbox");
                        chromeOptions.addArguments("--disable-dev-shm-usage");
                        chromeOptions.addArguments("--disable-gpu");
                    }
                    driver.set(new ChromeDriver(chromeOptions));
                    break;
            }

            // Timeouts
            driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
            driver.get().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
            driver.get().manage().window().maximize();
        }
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (Objects.nonNull(driver.get())) {
            driver.get().quit();
            driver.remove();
        }
    }
}