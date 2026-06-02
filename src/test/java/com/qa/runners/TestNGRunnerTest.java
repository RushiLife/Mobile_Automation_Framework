package com.qa.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"com.qa.stepdefinitions", "com.qa.hook"},
        tags = "@smoke",
        plugin = {
                "pretty",
                "html:target/cucumber-reports.html",
                "json:target/cucumber-reports.json"},
        monochrome = true,
        dryRun = false)
public class TestNGRunnerTest extends AbstractTestNGCucumberTests {

    /**
     * Feeds Cucumber scenarios to TestNG in parallel. Combined with
     * {@code data-provider-thread-count} in testng.xml, this is what actually
     * runs scenarios concurrently; the device pool then throttles how many
     * run at once.
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
