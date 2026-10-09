package edu.college.gymportal;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Browser-level smoke flow; enable with -DrunSelenium=true and SELENIUM_REMOTE_URL. */
@EnabledIfSystemProperty(named = "runSelenium", matches = "true")
class MemberPortalSeleniumTest {
    private WebDriver browser;

    @BeforeEach void openBrowser() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        String remote = System.getenv("SELENIUM_REMOTE_URL");
        browser = remote == null || remote.isBlank()
                ? new ChromeDriver(options)
                : new RemoteWebDriver(java.net.URI.create(remote).toURL(), options);
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
    }

    @AfterEach void closeBrowser() { if (browser != null) browser.quit(); }

    @Test void loginCreateSearchUpdateAndChangeStatus() {
        String baseUrl = System.getenv().getOrDefault("PORTAL_BASE_URL", "http://localhost:8080");
        WebDriverWait wait = new WebDriverWait(browser, Duration.ofSeconds(15));
        browser.get(baseUrl + "/login");
        browser.findElement(By.id("username")).sendKeys("admin");
        browser.findElement(By.id("password")).sendKeys(System.getenv("GYM_ADMIN_PASSWORD"));
        browser.findElement(By.cssSelector("button[type=submit]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-member")));

        browser.findElement(By.id("add-member")).click();
        String unique = "Selenium " + System.currentTimeMillis();
        String email = "selenium." + System.currentTimeMillis() + "@example.test";
        browser.findElement(By.id("name")).sendKeys(unique);
        browser.findElement(By.id("email")).sendKeys(email);
        browser.findElement(By.id("phone")).sendKeys("9876543210");
        setDate("startDate", "2026-10-08");
        setDate("endDate", "2027-10-08");
        browser.findElement(By.cssSelector("button[type=submit]")).click();
        try {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), unique));
        } catch (org.openqa.selenium.TimeoutException timeout) {
            String fields = browser.findElements(By.cssSelector(".member-form input, .member-form select")).stream()
                    .map(field -> field.getAttribute("name") + "=" + field.getAttribute("value"))
                    .collect(java.util.stream.Collectors.joining(", "));
            throw new AssertionError("Member registration did not finish. Current URL: " + browser.getCurrentUrl()
                    + "; fields: " + fields + "; visible page: " + browser.findElement(By.tagName("body")).getText(), timeout);
        }

        browser.findElement(By.id("member-search")).sendKeys(email);
        browser.findElement(By.cssSelector(".search-form button")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tr[data-member-id]")));
        browser.findElement(By.cssSelector("tr[data-member-id] .actions a")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("startDate")));
        org.junit.jupiter.api.Assertions.assertEquals("2026-10-08", browser.findElement(By.id("startDate")).getAttribute("value"),
                "Existing start date should load into the edit form. Current URL: " + browser.getCurrentUrl());
        org.junit.jupiter.api.Assertions.assertEquals("2027-10-08", browser.findElement(By.id("endDate")).getAttribute("value"),
                "Existing end date should load into the edit form.");
        browser.findElement(By.id("phone")).clear();
        browser.findElement(By.id("phone")).sendKeys("9876543211");
        browser.findElement(By.cssSelector("button[type=submit]")).click();
        try {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "9876543211"));
        } catch (org.openqa.selenium.TimeoutException timeout) {
            String fields = browser.findElements(By.cssSelector(".member-form input, .member-form select")).stream()
                    .map(field -> field.getAttribute("name") + "=" + field.getAttribute("value") + ":valid=" +
                            field.getAttribute("validity"))
                    .collect(java.util.stream.Collectors.joining(", "));
            throw new AssertionError("Member update did not finish. Current URL: " + browser.getCurrentUrl()
                    + "; fields: " + fields + "; visible page: " + browser.findElement(By.tagName("body")).getText(), timeout);
        }
        browser.findElement(By.cssSelector(".actions select")).sendKeys("INACTIVE");
        browser.findElement(By.cssSelector(".actions form button")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "INACTIVE"));
        assertTrue(browser.getPageSource().contains(unique));
    }

    private void setDate(String id, String value) {
        ((JavascriptExecutor) browser).executeScript(
                "const input = document.getElementById(arguments[0]); input.value = arguments[1]; " +
                "input.dispatchEvent(new Event('input', {bubbles: true})); " +
                "input.dispatchEvent(new Event('change', {bubbles: true}));", id, value);
    }
}
