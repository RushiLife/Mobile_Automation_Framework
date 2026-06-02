package com.qa.pages;


import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class LoginScreen
{

    private AppiumDriver driver;

    @AndroidFindBy (accessibility = "test-Username")
    @iOSXCUITFindBy(accessibility = "test-Username")
    private WebElement userName;

    public  LoginScreen(AppiumDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);

    }

    public void enter_username_for_login(String username)
    {
        userName.sendKeys(username);
    }

}
