package com.qa.stepdefinitions;

import com.qa.base.AppLauncher;
import com.qa.pages.LoginScreen;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class LoginStepDefinition {

    LoginScreen loginScreen = new LoginScreen(AppLauncher.getDriver());

    @Given("Launch Mobile App")
    public void launchMobileApp() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("Launch Mobile App");
    }

    @Then("Enter Username {string}")
    public void enter_username(String username) throws InterruptedException {
    System.out.println("Enter Username " + username);
    loginScreen.enter_username_for_login(username);
    }
    @Then("Enter Password {string}")
    public void enter_password(String string) {
    System.out.println("Enter Password " + string);
    }
    @Then("Click on login button")
    public void click_on_login_button() {
    System.out.println("Click on login button");
    }


}
