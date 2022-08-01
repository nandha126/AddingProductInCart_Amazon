package com.addingproductincart.amazon;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AddProduct {

	private static final String URL = "https://www.amazon.in/";
	WebDriver driver;
	String actualURL;
	String expectedURL;

	@BeforeTest
	public void setup() {
		setupDriver();
		initilizeApp();
		Reporter.log("SetupDriver and InitilizeApplication was Successfully.");
	}

	@AfterTest
	public void tearDown() {
		driver.close();
		driver.quit();
		Reporter.log("Browser Closed Done.");
	}

	@Test
	public void searchProduct() {
		WebElement userProduct = driver.findElement(By.id("twotabsearchtextbox"));
		userProduct.clear();
		userProduct.sendKeys("boAt Rockerz wireless headset bluetooth");
		WebElement searchBtn = driver.findElement(By.id("nav-search-submit-button"));
		LibraryUtils.waitForElementToBeVisible(driver, searchBtn, 5);
		searchBtn.click();
		Reporter.log("Search user product Successfully.");
	}

	@Test(dependsOnMethods = { "searchProduct" })
	public void selectProduct() throws InterruptedException {
		WebElement chooseElement = driver.findElement(By.cssSelector(".widgetId\\=search-results_6 .s-line-clamp-2 > a"));
		LibraryUtils.waitForElementToBeVisible(driver, chooseElement, 10);
		chooseElement.click();
		Reporter.log("Chosen Product Successfully.");
		// driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.TAB);
		Set<String> windowHandles = driver.getWindowHandles();
		List<String> list = new ArrayList<String>(windowHandles);
		driver.switchTo().window(list.get(1));
	}

	@Test(dependsOnMethods = { "selectProduct" })
	public void addToCart() throws AWTException {
		WebElement alink = driver.findElement(By.cssSelector("#productTitle"));
		LibraryUtils.waitForElementToBeVisible(driver, alink, 7);
		expectedURL = alink.getText();
		//System.out.println(expectedURL);
		WebElement addProducttoCart = driver.findElement(By.xpath("//input[@id='add-to-cart-button']"));
		addProducttoCart.click();
		
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_ESCAPE);
		r.keyRelease(KeyEvent.VK_ESCAPE);
		Reporter.log("Product successfully added in Cart.");
	}

	@Test(dependsOnMethods = { "addToCart" })
	public void viewAddedProductinCart() {
		WebElement viewAddedProduct = driver.findElement(By.xpath("//*[@id='sw-gtc']"));
		viewAddedProduct.click();
		driver.findElement(By.cssSelector(".sc-info-block .a-truncate-cut")).click();
	
		Set<String> windowHandles = driver.getWindowHandles();
		List<String> list = new ArrayList<String>(windowHandles);
		driver.switchTo().window(list.get(2));
		
		
		WebElement elink = driver.findElement(By.cssSelector("#productTitle"));
		LibraryUtils.waitForElementToBeVisible(driver, elink, 10);
		actualURL = elink.getText();
		//System.out.println(actualURL);
		Reporter.log("Successfully viewing product in cart.");
	}

	@Test(dependsOnMethods = { "viewAddedProductinCart" })
	public void verifytheProduct() {
		boolean isEqual = actualURL.equals(expectedURL);
		Assert.assertTrue(isEqual);
		Reporter.log("Searched and added Product are same.");
	}

	private void initilizeApp() {
		driver.manage().window().maximize();
		driver.get(URL);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	}

	private void setupDriver() {
		System.setProperty("webdriver.chrome.driver", "./lib/chromedriver.exe");
		this.driver = new ChromeDriver();
	}
}
