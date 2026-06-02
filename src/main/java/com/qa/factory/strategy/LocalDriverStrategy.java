package com.qa.factory.strategy;

import com.qa.config.ConfigReader;
import com.qa.factory.DriverStrategy;
import com.qa.manager.Device;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.net.URI;
import java.time.Duration;

/**
 * Runs against a local Appium server with local emulators/simulators. The
 * per-device unique ports (systemPort/wdaLocalPort) are what keep concurrent
 * local sessions from colliding.
 */
public class LocalDriverStrategy implements DriverStrategy {

    @Override
    public AppiumDriver createDriver(Device device) throws Exception {
        URI server = URI.create(ConfigReader.get("appium.server.url"));
        return switch (device.platform()) {
            case ANDROID -> new AndroidDriver(server.toURL(), androidOptions(device));
            case IOS -> new IOSDriver(server.toURL(), iosOptions(device));
        };
    }

    private UiAutomator2Options androidOptions(Device device) {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setAutomationName(ConfigReader.get("android.automationName"));
        options.setDeviceName(device.deviceName());
        options.setPlatformVersion(device.platformVersion());
        options.setAppPackage(ConfigReader.get("android.appPackage"));
        options.setAppActivity(ConfigReader.get("android.appActivity"));
        options.setNoReset(false);
        options.setAutoGrantPermissions(true);
        if (device.avd() != null) {
            options.setAvd(device.avd());
            options.setAvdLaunchTimeout(Duration.ofMillis(250000));
        }
        // Unique per parallel Android session — prevents UiAutomator2 server-port collisions
        if (device.systemPort() > 0) {
            options.setSystemPort(device.systemPort());
        }
        return options;
    }

    private XCUITestOptions iosOptions(Device device) {
        XCUITestOptions options = new XCUITestOptions();
        options.setPlatformName("iOS");
        options.setAutomationName(ConfigReader.get("ios.automationName"));
        options.setDeviceName(device.deviceName());
        options.setPlatformVersion(device.platformVersion());
        options.setBundleId(ConfigReader.get("ios.bundleId"));
        options.setNoReset(true);
        options.setNewCommandTimeout(Duration.ofSeconds(300));
        if (device.udid() != null) {
            options.setUdid(device.udid());
        }
        // Unique per parallel iOS session — prevents WebDriverAgent port collisions
        if (device.wdaLocalPort() > 0) {
            options.setWdaLocalPort(device.wdaLocalPort());
        }
        return options;
    }
}
