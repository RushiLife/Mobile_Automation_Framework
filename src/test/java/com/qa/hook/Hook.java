package com.qa.hook;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.qa.config.ConfigReader;
import com.qa.enums.Platform;
import com.qa.factory.DriverFactory;
import com.qa.manager.Device;
import com.qa.manager.DeviceManager;
import com.qa.manager.DriverManager;
import io.cucumber.java.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hook {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static final ThreadLocal<Device> currentDevice = new ThreadLocal<>();

    @BeforeAll
    public static void beforeAll() {
        ExtentSparkReporter reporter = new ExtentSparkReporter("target/extent-report.html");
        extent = new ExtentReports();
        extent.attachReporter(reporter);

        // Build the device pool once for the target platform (override with -Dplatform=iOS)
        DeviceManager.initPool(Platform.from(ConfigReader.get("platform")));
    }

    @Before
    public void beforeScenario(Scenario scenario) throws Exception {
        // One free device per thread → one driver per thread (parallel-safe)
        Device device = DeviceManager.acquire();
        currentDevice.set(device);
        DriverManager.setDriver(DriverFactory.createDriver(device));

        ExtentTest extentTest = extent.createTest(scenario.getName());
        extentTest.assignDevice(device.platform() + " | " + device.deviceName());
        test.set(extentTest);
        test.get().info("App launched on [" + device.platform() + " | " + device.deviceName()
                + "] for scenario: " + scenario.getName());
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        if (scenario.isFailed() && DriverManager.getDriver() != null) {
            String base64 = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BASE64);
            test.get().addScreenCaptureFromBase64String(base64);
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        try {
            if (test.get() != null) {
                if (scenario.isFailed()) {
                    test.get().fail("Failed: " + scenario.getName());
                } else {
                    test.get().pass("Passed: " + scenario.getName());
                }
            }
        } finally {
            // Always release the device back to the pool, even if quit() throws
            DriverManager.quitDriver();
            DeviceManager.release(currentDevice.get());
            currentDevice.remove();
            test.remove();
        }
    }

    @AfterAll
    public static void afterAll() {
        if (extent != null) {
            extent.flush();
        }
    }
}
