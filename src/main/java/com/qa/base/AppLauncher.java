package com.qa.base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.io.File;
import java.net.URI;
import java.time.Duration;

public class AppLauncher {

    private static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    // Appium 2.x/3.x default base path is "/" — the legacy "/wd/hub" suffix was removed
    private static final String APPIUM_SERVER_URL = "http://127.0.0.1:4723/";

    private static String getAndroidAppPath() {
        return System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
                + File.separator + "resources" + File.separator + "app" + File.separator + "Android.apk";
    }

    public static AppiumDriver initializeDriver(String platform) throws Exception {

        if (platform.equalsIgnoreCase("Android")) {
            // Android capabilities — UiAutomator2Options applies the W3C "appium:" prefix automatically
            UiAutomator2Options options = new UiAutomator2Options();
            options.setPlatformName("Android");
            options.setDeviceName("emulator-5554");
            options.setAutomationName("UiAutomator2");
            options.setPlatformVersion("14");
            options.setAppPackage("com.swaglabsmobileapp");
            options.setAppActivity("com.swaglabsmobileapp.SplashActivity");
            options.setNoReset(false);
            options.setAutoGrantPermissions(true);
            options.setAvd("Pixel_8");
            options.setAvdLaunchTimeout(Duration.ofMillis(250000));
            //options.setApp(getAndroidAppPath());

            driver.set(new AndroidDriver(URI.create(APPIUM_SERVER_URL).toURL(), options));

        } else if (platform.equalsIgnoreCase("iOS")) {
            // iOS capabilities — XCUITestOptions applies the W3C "appium:" prefix automatically
            XCUITestOptions options = new XCUITestOptions();
            options.setPlatformName("iOS");
            options.setAutomationName("XCUITest");
            options.setDeviceName("iPhone 16 Pro");
            options.setPlatformVersion("18.4"); // Update as needed
            options.setBundleId("com.saucelabs.SwagLabsMobileApp");
            options.setUdid("9F9D8AA3-89E1-43FF-B732-1257DCA213A4"); // Update to the correct UDID of your device
            options.setNewCommandTimeout(Duration.ofSeconds(300));
            options.setNoReset(true);
            //options.setApp(System.getProperty("user.dir") + "/apps/iOS.app");

            driver.set(new IOSDriver(URI.create(APPIUM_SERVER_URL).toURL(), options));

        } else {
            throw new Exception("Unsupported platform: " + platform);
        }

        return driver.get();
    }

    public static AppiumDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() throws InterruptedException {
        if (driver.get() != null) {
            Thread.sleep(5000);
            driver.get().quit();
            driver.remove();
        }
    }
}
