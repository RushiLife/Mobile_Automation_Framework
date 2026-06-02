package com.qa.factory;

import com.qa.config.ConfigReader;
import com.qa.enums.ExecutionTarget;
import com.qa.manager.Device;
import io.appium.java_client.AppiumDriver;

/**
 * Entry point for driver creation. Resolves the configured execution target
 * (e.g. {@code local} | {@code browserstack}) and delegates to its
 * {@link DriverStrategy}. Callers (Hook) are unaware of where tests run.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static AppiumDriver createDriver(Device device) throws Exception {
        ExecutionTarget target = ExecutionTarget.from(ConfigReader.get("execution.target"));
        return DriverStrategyFactory.forTarget(target).createDriver(device);
    }
}
