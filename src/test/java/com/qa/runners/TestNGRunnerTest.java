package com.qa.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;


@CucumberOptions(features = { "src/test/resources/features" },

        glue = {"com.qa.stepdefinitions", "com.qa.hook"},

        tags = "@smoke",

        //"rerun:target/cucumber-reports/rerun.txt",
        plugin = { "pretty",
                "html:target/cucumber-reports.html",
                "json:target/cucumber-reports.json" },

        monochrome = true,
        dryRun = false)

    public class TestNGRunnerTest extends AbstractTestNGCucumberTests
    {

    }
