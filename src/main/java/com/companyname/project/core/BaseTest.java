package com.companyname.project.core;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BaseTest {
    protected static final ThreadLocal<WebDriverWait> wait = new ThreadLocal<>();
    private static final By MENU_CONTAINER = By.xpath("//div[@class='MuiScopedCssBaseline-root']");

    public static void initializeWait() {
        // Only initialize WebDriverWait using existing driver
        wait.set(new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(10)));
    }

    public static void tearDownWait() {
        if (wait.get() != null) {
            wait.remove();
        }
    }
    protected static void tearDownTest() {
        DriverFactory.quitDriver();
        wait.remove();
    }

    // Highlight an element using DriverFactory
    public static void highlightElement(WebElement element) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.border='3px solid red'", 
                element
            );
        } catch (Exception e) {
            System.err.println("Couldn't highlight element: " + e.getMessage());
        }
    }   

    // Adds a highlight animation to the element for visual feedback
    public static void highlightElementWithAnimation(WebElement element) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String originalStyle = element.getAttribute("style");
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "border: 3px solid orange; transition: border 0.3s;");
            Thread.sleep(300);
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, originalStyle);
        } catch (Exception e) {
            System.err.println("Couldn't animate highlight: " + e.getMessage());
        }
    }

    // Embedded MenuSelector functionality
    public static boolean selectMenuItem(String menuText) {
        WebDriver driver = DriverFactory.getDriver();
        String initialPageState = getCurrentPageStateId(driver); // Store initial state
        // 1. Find and click menu item
        WebElement menu = wait.get().until(ExpectedConditions.visibilityOfElementLocated(MENU_CONTAINER));
        WebElement menuItem = menu.findElement(By.xpath(".//span[normalize-space()='" + menuText + "']"));
        highlightElement(menuItem);
        wait.get().until(ExpectedConditions.elementToBeClickable(menuItem)).click();
       
        // Only wait if page actually reloads
        if (!getCurrentPageStateId(driver).equals(initialPageState)) {
            waitForPageReload();
        }

        return true;
    }

    public static void  navigateToMenuItem(String menuText) {
        WebDriver driver = DriverFactory.getDriver();
        String initialPageState = getCurrentPageStateId(driver); // Store initial state

        WebElement menu = wait.get().until(ExpectedConditions.visibilityOfElementLocated(MENU_CONTAINER));
        WebElement menuItem = menu.findElement(By.xpath(".//span[normalize-space()='" + menuText + "']"));
        highlightElement(menuItem);
        wait.get().until(ExpectedConditions.elementToBeClickable(menuItem)).click();

        // Only wait if page actually reloads
        if (!getCurrentPageStateId(driver).equals(initialPageState)) {
            waitForPageReload();
        }
    }

        public static boolean navigateToHomePage() {
        WebDriver driver = DriverFactory.getDriver();

        String url = EnvironmentLoader.get("url");
        driver.get(url);
        
        //String initialPageState = getCurrentPageStateId(driver); // Store initial state

        //WebElement mainLogo = driver.findElement(By.xpath("//img[@alt='App Logo']"));
        //mainLogo.click();

        // Only wait if page actually reloads
        //if (!getCurrentPageStateId(driver).equals(initialPageState)) {
        //    waitForPageReload();
        //}
        return true;
    }
    
    public static List<WebElement> getAllMenuItems() {
        WebElement menu = wait.get().until(ExpectedConditions.visibilityOfElementLocated(MENU_CONTAINER));
        return menu.findElements(By.xpath(".//span[contains(@class, 'MuiListItemText-primary')]"));
    }

    public static boolean isMenuItemPresent(String menuText) {
        try {
            WebElement menu = wait.get().until(ExpectedConditions.visibilityOfElementLocated(MENU_CONTAINER));
            menu.findElement(By.xpath(".//span[normalize-space()='" + menuText + "']"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPagePresent(String pageName) {
        try {
            WebDriverWait dynamicWait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(10));
            
            // Try multiple common element patterns
            List<By> possibleLocators = Arrays.asList(
                 By.xpath("//*[contains(@class,'page-title') and normalize-space()='" + pageName + "']")
                //By.xpath("//*[@data-testid='page-header' and normalize-space()='" + pageName + "']") //Placeholder for data-testid
            );
            
            // Check each possible locator
            for (By locator : possibleLocators) {
                try {
                    WebElement element = wait.get().until(ExpectedConditions.visibilityOfElementLocated(locator));
                    dynamicWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                    highlightElementWithAnimation(element);  // Enhanced visual feedback

                    //System.out.println("Element location: " + element.getLocation());
                    //System.out.println("Element visible: " + element.isDisplayed());
                    //System.out.println("Locator Found: " + possibleLocators);
            
                } catch (TimeoutException e) {
                    continue;
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Page validation error: " + e.getMessage());
            return false;
        }
    }

    public static void waitForPageReload() {
        WebDriver driver = DriverFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        
        // Get initial page state
        String initialPageId = getCurrentPageStateId(driver);
        
        // Wait until page state changes
        wait.until(d -> {
            String currentPageId = getCurrentPageStateId(d);
            return !currentPageId.equals(initialPageId);
        });
        
        // Then wait for document ready state
        wait.until(d -> {
            return ((JavascriptExecutor)d).executeScript("return document.readyState").equals("complete");
        });
    }

    private static String getCurrentPageStateId(WebDriver driver) {
    try {
        return (String) ((JavascriptExecutor)DriverFactory.getDriver()).executeScript(
            "try {" +
            "  return (document.readyState === 'complete') ? " +
            "    (window.performance.timing.navigationStart + '_' + " +
            "    (document.documentElement?.outerHTML.length || document.body.innerHTML.length)) " +
            "  : 'page_loading';" +
            "} catch(e) { return 'error_state'; }"
        );
        } catch (JavascriptException e) {
            return "error_state";
        }
    }
}