package com.qa.enums;

import java.util.Arrays;

/**
 * Where a run executes. Switch via {@code execution.target} in config.properties
 * or {@code -Dexecution.target=...}. Adding a provider = add a constant here,
 * a strategy class, and a case in {@code DriverStrategyFactory}.
 */
public enum ExecutionTarget {

    LOCAL,
    BROWSERSTACK;

    public static ExecutionTarget from(String value) {
        for (ExecutionTarget target : values()) {
            if (target.name().equalsIgnoreCase(value)) {
                return target;
            }
        }
        throw new IllegalArgumentException(
                "Unsupported execution.target: '" + value + "' (expected one of " + Arrays.toString(values()) + ")");
    }

    public boolean isLocal() {
        return this == LOCAL;
    }
}
