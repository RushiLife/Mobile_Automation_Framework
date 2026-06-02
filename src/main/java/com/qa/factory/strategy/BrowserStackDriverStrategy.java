package com.qa.factory.strategy;

import com.qa.config.ConfigReader;
import com.qa.manager.Device;

import java.util.HashMap;
import java.util.Map;

/**
 * BrowserStack App Automate. Credentials resolve from {@code -Dcloud.username} /
 * {@code -Dcloud.accessKey}, then env {@code BROWSERSTACK_USERNAME} /
 * {@code BROWSERSTACK_ACCESS_KEY} — never committed to config. {@code cloud.app}
 * must point at an uploaded build ({@code bs://<app-id>}); the device catalog
 * name + version come from devices.cloud.json.
 */
public class BrowserStackDriverStrategy extends AbstractCloudDriverStrategy {

    @Override
    protected String vendorCapabilityKey() {
        return "bstack:options";
    }

    @Override
    protected Map<String, Object> vendorOptions(Device device) {
        Map<String, Object> bstack = new HashMap<>();
        bstack.put("userName", ConfigReader.getSecret("cloud.username", "BROWSERSTACK_USERNAME"));
        bstack.put("accessKey", ConfigReader.getSecret("cloud.accessKey", "BROWSERSTACK_ACCESS_KEY"));
        bstack.put("projectName", ConfigReader.get("cloud.project", "Mobile_Automation_Framework"));
        bstack.put("buildName", ConfigReader.get("cloud.build", "local-build"));
        bstack.put("sessionName", device.platform() + " - " + device.deviceName());
        return bstack;
    }
}
