package com.qa.manager;

import io.appium.java_client.AppiumDriver;

/**
 * Single source of truth for the driver bound to the current thread.
 * One thread == one scenario == one driver, which is what makes parallel
 * execution safe. (Replaces the two separate ThreadLocals that used to live
 * in AppLauncher and Hook.)
 */
public final class DriverManager {

    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void setDriver(AppiumDriver driver) {
        DRIVER.set(driver);
    }

    public static AppiumDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
