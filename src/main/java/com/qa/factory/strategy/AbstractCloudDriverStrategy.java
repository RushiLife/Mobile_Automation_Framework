package com.qa.factory.strategy;

import com.qa.config.ConfigReader;
import com.qa.factory.DriverStrategy;
import com.qa.manager.Device;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.MutableCapabilities;

import java.net.URI;
import java.net.URL;
import java.util.Map;

/**
 * Shared flow for cloud device farms: build the platform base capabilities
 * (deviceName / platformVersion taken from the provider's device catalog, plus
 * the uploaded app reference), attach the provider's vendor capability block,
 * then connect to the provider hub. A concrete provider only supplies its
 * vendor key and vendor options — the wiring here is provider-agnostic.
 */
public abstract class AbstractCloudDriverStrategy implements DriverStrategy {

    /** Top-level vendor capability name, e.g. {@code "bstack:options"}. */
    protected abstract String vendorCapabilityKey();

    /** Provider-specific block: credentials + build/session metadata. */
    protected abstract Map<String, Object> vendorOptions(Device device);

    /** Provider hub endpoint (defaults to {@code cloud.hub.url}). */
    protected URL hubUrl() throws Exception {
        return URI.create(ConfigReader.get("cloud.hub.url")).toURL();
    }

    @Override
    public AppiumDriver createDriver(Device device) throws Exception {
        URL hub = hubUrl();
        return switch (device.platform()) {
            case ANDROID -> new AndroidDriver(hub, decorate(androidBase(device), device));
            case IOS -> new IOSDriver(hub, decorate(iosBase(device), device));
        };
    }

    private UiAutomator2Options androidBase(Device device) {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setAutomationName(ConfigReader.get("android.automationName"));
        options.setDeviceName(device.deviceName());
        options.setPlatformVersion(device.platformVersion());
        return options;
    }

    private XCUITestOptions iosBase(Device device) {
        XCUITestOptions options = new XCUITestOptions();
        options.setPlatformName("iOS");
        options.setAutomationName(ConfigReader.get("ios.automationName"));
        options.setDeviceName(device.deviceName());
        options.setPlatformVersion(device.platformVersion());
        return options;
    }

    /** Attaches the uploaded-app reference (if any) and the vendor capability block. */
    private <T extends MutableCapabilities> T decorate(T options, Device device) {
        String app = ConfigReader.get("cloud.app", "");
        if (!app.isBlank()) {
            options.setCapability("appium:app", app);
        }
        options.setCapability(vendorCapabilityKey(), vendorOptions(device));
        return options;
    }
}
