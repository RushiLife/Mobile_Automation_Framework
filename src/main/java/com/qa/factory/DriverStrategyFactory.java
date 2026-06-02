package com.qa.factory;

import com.qa.enums.ExecutionTarget;
import com.qa.factory.strategy.BrowserStackDriverStrategy;
import com.qa.factory.strategy.LocalDriverStrategy;

/** Resolves the {@link DriverStrategy} for the configured {@link ExecutionTarget}. */
public final class DriverStrategyFactory {

    private DriverStrategyFactory() {
    }

    public static DriverStrategy forTarget(ExecutionTarget target) {
        return switch (target) {
            case LOCAL -> new LocalDriverStrategy();
            case BROWSERSTACK -> new BrowserStackDriverStrategy();
        };
    }
}
