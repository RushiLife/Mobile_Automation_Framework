package com.qa.base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.URI;
import java.net.URL;

public class AppLauncher {

    private static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    private static String getAndroidAppPath() {
        return System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
                + File.separator + "resources" + File.separator + "app" + File.separator + "Android.apk";
    }

        public static AppiumDriver initializeDriver(String platform) throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();

        if (platform.equalsIgnoreCase("Android")) {
            // Android capabilities
            caps.setCapability("platformName", "Android");
            caps.setCapability("deviceName", "emulator-5554");
            caps.setCapability("automationName", "UiAutomator2");
            caps.setCapability("platformVersion", "14");
            caps.setCapability("appPackage", "com.swaglabsmobileapp");
            caps.setCapability("appActivity", "com.swaglabsmobileapp.SplashActivity");
            //caps.setCapability("app", getAndroidAppPath());
            caps.setCapability("noReset", false);
            caps.setCapability("autoGrantPermissions", true);
            caps.setCapability("avd", "Pixel_8");
            caps.setCapability("avdLaunchTimeout", 250000);
            //caps.setCapability("app", System.getProperty("user.dir") + "/app/Android.apk");

            driver.set(new AndroidDriver(URI.create("http://127.0.0.1:4723/wd/hub").toURL(), caps));

        } else if (platform.equalsIgnoreCase("iOS")) {
            // iOS capabilities

            caps.setCapability("platformName", "iOS");
            caps.setCapability("automationName", "XCUITest");
            caps.setCapability("deviceName", "iPhone 16 Pro");
            caps.setCapability("platformVersion", "18.4"); // Update as needed
            caps.setCapability("bundleId", "com.saucelabs.SwagLabsMobileApp");
            caps.setCapability("noReset", "true");
            caps.setCapability("undid", "9F9D8AA3-89E1-43FF-B732-1257DCA213A4"); // Update to the correct UDID of your device
            caps.setCapability("newCommandTimeout", 300);
            //caps.setCapability("app", System.getProperty("user.dir") + "/apps/iOS.app");

            driver.set(new IOSDriver(URI.create("http://127.0.0.1:4723").toURL(), caps));

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
