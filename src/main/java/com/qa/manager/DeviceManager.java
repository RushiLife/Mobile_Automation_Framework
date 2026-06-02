package com.qa.manager;

import com.qa.config.ConfigReader;
import com.qa.enums.ExecutionTarget;
import com.qa.enums.Platform;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Thread-safe pool of available devices, loaded from {@code config/devices.json}.
 * Each scenario thread {@link #acquire() acquires} a free device and
 * {@link #release(Device) releases} it on teardown, so two parallel threads can
 * never drive the same device. The pool size is therefore the real throttle on
 * concurrency — add a device entry to scale parallelism up.
 */
public final class DeviceManager {

    private static final BlockingQueue<Device> AVAILABLE = new LinkedBlockingQueue<>();
    private static final long ACQUIRE_TIMEOUT_MINUTES = 5;

    private DeviceManager() {
    }

    /** Loads every device matching {@code platform} from devices.json into the pool. */
    public static void initPool(Platform platform) {
        AVAILABLE.clear();
        ExecutionTarget target = ExecutionTarget.from(ConfigReader.get("execution.target"));
        String resource = target.isLocal() ? "config/devices.json" : "config/devices.cloud.json";
        try (InputStream in = DeviceManager.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException(resource + " not found on the classpath");
            }
            JSONArray devices = new JSONObject(new JSONTokener(in)).getJSONArray("devices");
            for (int i = 0; i < devices.length(); i++) {
                JSONObject entry = devices.getJSONObject(i);
                if (Platform.from(entry.getString("platform")) != platform) {
                    continue;
                }
                AVAILABLE.add(new Device(
                        platform,
                        entry.getString("deviceName"),
                        entry.getString("platformVersion"),
                        entry.optString("avd", null),
                        entry.optString("udid", null),
                        entry.optInt("systemPort", 0),
                        entry.optInt("wdaLocalPort", 0)));
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load " + resource, e);
        }
        if (AVAILABLE.isEmpty()) {
            throw new IllegalStateException("No devices configured for platform: " + platform);
        }
    }

    /** Blocks until a device is free, then hands it to the calling thread. */
    public static Device acquire() {
        try {
            Device device = AVAILABLE.poll(ACQUIRE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            if (device == null) {
                throw new IllegalStateException("Timed out waiting for a free device from the pool");
            }
            return device;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for a device", e);
        }
    }

    public static void release(Device device) {
        if (device != null) {
            AVAILABLE.offer(device);
        }
    }
}
