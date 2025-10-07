package com.qa.hook;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.qa.base.AppLauncher;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hook {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    @BeforeAll
    public static void beforeAll() {
        // Extent setup
        ExtentSparkReporter reporter = new ExtentSparkReporter("target/extent-report.html");
        extent = new ExtentReports();
        extent.attachReporter(reporter);
    }

    @Before
    public void beforeScenario(Scenario scenario) throws Exception {
        // Initialize driver
        AppiumDriver appiumDriver = AppLauncher.initializeDriver("Android");
        driver.set(appiumDriver);

        // Create a new ExtentTest for each scenario
        ExtentTest extentTest = extent.createTest(scenario.getName());
        test.set(extentTest);

        test.get().info("App Launched for scenario: " + scenario.getName());
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) driver.get()).getScreenshotAs(OutputType.BYTES);

            // Attach screenshot to extent
            test.get().addScreenCaptureFromBase64String(((TakesScreenshot) driver.get()).getScreenshotAs(OutputType.BASE64));
        }
    }

    @After
    public void afterScenario(Scenario scenario) throws InterruptedException {
        if (test.get() == null) {
            return; // safeguard against NPE
        }

        if (scenario.isFailed()) {
            test.get().fail("Failed: " + scenario.getName());
        } else {
            test.get().pass("Passed: " + scenario.getName());
        }

        // Quit App
        AppLauncher.quitDriver();
    }

    @AfterAll
    public static void afterAll() {
        extent.flush();
    }

    public static AppiumDriver getDriver() {
        return driver.get();
    }
}
