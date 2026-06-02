package com.qa.manager;

import com.qa.enums.Platform;

/**
 * One physical/virtual device entry from {@code config/devices.json}.
 *
 * @param avd          Android emulator (AVD) name, or {@code null} for iOS / real devices
 * @param udid         device UDID, or {@code null} when an AVD/deviceName is enough
 * @param systemPort   unique UiAutomator2 server port (Android) — required for parallel runs
 * @param wdaLocalPort unique WebDriverAgent port (iOS) — required for parallel runs
 */
public record Device(
        Platform platform,
        String deviceName,
        String platformVersion,
        String avd,
        String udid,
        int systemPort,
        int wdaLocalPort) {
}
