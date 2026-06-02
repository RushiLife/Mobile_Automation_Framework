package com.qa.stepdefinitions;

import com.qa.manager.DriverManager;
import com.qa.pages.LoginScreen;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class LoginStepDefinition {

    private LoginScreen loginScreen;

    /**
     * Lazily builds the page object the first time it is needed — i.e. after the
     * @Before hook has created the driver for this thread. Building it as a field
     * initializer (the old approach) captured a null driver and caused the NPE
     * recorded in "Issue and Solutions.txt".
     */
    private LoginScreen loginScreen() {
        if (loginScreen == null) {
            loginScreen = new LoginScreen(DriverManager.getDriver());
        }
        return loginScreen;
    }

    @Given("Launch Mobile App")
    public void launchMobileApp() {
        System.out.println("Launch Mobile App");
    }

    @Then("Enter Username {string}")
    public void enter_username(String username) {
        System.out.println("Enter Username " + username);
        loginScreen().enter_username_for_login(username);
    }

    @Then("Enter Password {string}")
    public void enter_password(String password) {
        System.out.println("Enter Password " + password);
    }

    @Then("Click on login button")
    public void click_on_login_button() {
        System.out.println("Click on login button");
    }
}
