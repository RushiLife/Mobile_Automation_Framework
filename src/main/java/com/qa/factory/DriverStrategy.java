package com.qa.factory;

import com.qa.manager.Device;
import io.appium.java_client.AppiumDriver;

/** Creates a driver for one device against a particular execution target. */
public interface DriverStrategy {

    AppiumDriver createDriver(Device device) throws Exception;
}
