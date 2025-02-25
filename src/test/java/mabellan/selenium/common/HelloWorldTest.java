package mabellan.selenium.common;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import utils.SeleniumDriver;

@Listeners(TestListener.class)
public class HelloWorldTest {
	
	@BeforeMethod
	public static void setUp() {
		SeleniumDriver.setUpDriver();
	}
	
	@Test(groups = {"hello-world"}, description = "Hello World")
	public void helloWorld(ITestContext context) {
		WebDriver driver = SeleniumDriver.getDriver();
		driver.get("https://www.google.es");
		SeleniumDriver.waitForPageToLoad(5000);
		SeleniumDriver.screenshot("google-es");
	}
	
	@AfterMethod(alwaysRun = true)
	public static void tearDown() {
		SeleniumDriver.tearDown();
	}

}
