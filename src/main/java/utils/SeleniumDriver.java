package utils;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Date;
import java.util.StringJoiner;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import lombok.Getter;

public class SeleniumDriver {
	
	private static ThreadLocal<SeleniumDriver> seleniumDriver = new ThreadLocal<SeleniumDriver>();
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();
	@Getter
    public static Configuration config = new Configuration();
	private static String browser;
	private static final Duration timeout = Duration.ofSeconds(Integer.parseInt(config.getProperty("timeout")));
	private static final Duration shortTimeout = Duration.ofSeconds(Integer.parseInt(config.getProperty("shortTimeout")));
	private static final Duration pageLoadTimeout = Duration.ofSeconds(Integer.parseInt(config.getProperty("pageLoadTimeout")));
	private static final ThreadLocal<WebDriverWait> wait = new ThreadLocal<>();
	private static final ThreadLocal<Actions> actions = new ThreadLocal<>();
	private static String defaultWaitTime;
	private static final boolean headless = Boolean.parseBoolean(System.getProperty("headless"));
	private static final String pathToDriver = System.getProperty("pathToDriver");
	
	
	private SeleniumDriver(WebDriverPreferences webDriverPreferences) {
		SeleniumDriver.defaultWaitTime = config.getProperty("defaultWaitTime");
		SeleniumDriver.browser = System.getProperty("browser", config.getProperty("browser"));
		
		setUpWebDriver(webDriverPreferences);

		wait.set(new WebDriverWait(driver.get(), SeleniumDriver.timeout));
		actions.set(new Actions(driver.get()));
		driver.get().manage().timeouts().implicitlyWait(SeleniumDriver.timeout);
		driver.get().manage().timeouts().pageLoadTimeout(SeleniumDriver.pageLoadTimeout);
		driver.get().getWindowHandle();
	}
	
	private static void setUpWebDriver(WebDriverPreferences webDriverPreferences) {
		if (SeleniumDriver.browser.equals("chrome")) {
			if (pathToDriver != null) {
				System.setProperty("webdriver.chrome.driver", pathToDriver);
			}

			ChromeOptions chromeOptions = getChromeOptions(webDriverPreferences);
			driver.set(new ChromeDriver(chromeOptions));

		} else if (SeleniumDriver.browser.equals("firefox")) {
			System.setProperty("webdriver.firefox.driver", pathToDriver);

			FirefoxOptions firefoxOptions = getFirefoxOptions(webDriverPreferences);
			driver.set(new FirefoxDriver(firefoxOptions));

		} else {
			if (pathToDriver != null) {
				System.setProperty("webdriver.edge.driver", pathToDriver);
			}

			EdgeOptions edgeOptions = getEdgeOptions(webDriverPreferences);
			driver.set(new EdgeDriver(edgeOptions));
		}
	}

	private static ChromeOptions getChromeOptions(WebDriverPreferences webDriverPreferences) {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--start-maximized");
		chromeOptions.addArguments("--remote-allow-origins=*");
		chromeOptions.addArguments("--no-sandbox");
		chromeOptions.addArguments("--disable-dev-shm-usage");
		//chromeOptions.addArguments("--incognito");

		if (SeleniumDriver.headless) {
			chromeOptions.addArguments("--window-size=1920x1080");
			chromeOptions.addArguments("--disable-gpu");
			chromeOptions.addArguments("--disable-extensions");
			chromeOptions.addArguments("--start-maximized");
			chromeOptions.addArguments("--headless");
			//chromeOptions.addArguments("--remote-debugging-port=9515");
		}

		if(webDriverPreferences != null && webDriverPreferences.getChromePreferences() != null) {

			webDriverPreferences.getChromePreferences().forEach(chromeOptions::addArguments);
		}


		return chromeOptions;
	}

	private static FirefoxOptions getFirefoxOptions(WebDriverPreferences webDriverPreferences) {
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.addArguments("--start-maximized");
		//firefoxOptions.addArguments("-private");
		if (SeleniumDriver.headless) {
			firefoxOptions.addArguments("--window-size=1920x1080");
			firefoxOptions.addArguments("--disable-gpu");
			firefoxOptions.addArguments("--disable-extensions");
			firefoxOptions.addArguments("--start-maximized");
			firefoxOptions.addArguments("--headless");
		}

		if(webDriverPreferences != null && webDriverPreferences.getFirefoxPreferences() != null) {
			FirefoxProfile firefoxProfile = new FirefoxProfile();

			webDriverPreferences.getFirefoxPreferences().forEach(firefoxProfile::setPreference);

			firefoxOptions.setProfile(firefoxProfile);
		}

		return firefoxOptions;
	}

	private static EdgeOptions getEdgeOptions(WebDriverPreferences webDriverPreferences) {
		EdgeOptions edgeOptions = new EdgeOptions();
		edgeOptions.addArguments("--start-maximized");
		edgeOptions.addArguments("--remote-allow-origins=*");
		//edgeOptions.addArguments("-inprivate");
		if (SeleniumDriver.headless) {
			edgeOptions.addArguments("--window-size=1920x1080");
			edgeOptions.addArguments("--disable-gpu");
			edgeOptions.addArguments("--disable-extensions");
			edgeOptions.addArguments("--start-maximized");
			edgeOptions.addArguments("--headless");
		}

		if(webDriverPreferences != null && webDriverPreferences.getEdgePreferences() != null) {
			edgeOptions.setCapability("ms:edgeChromium", true);

			StringJoiner stringJoiner = new StringJoiner(",");

			webDriverPreferences.getEdgePreferences().forEach((key, value) -> stringJoiner.add(
					MessageFormat.format("\"{0}\" : \"{1}\"", key, value)
			));

			edgeOptions.setCapability(
					"ms:edgeOptions",
					MessageFormat.format("'{' \"prefs\" : '{' {0} '}' '}'", stringJoiner)
			);
		}

		return edgeOptions;
	}

	public static void waitForPageToLoad(int miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public static File screenshot(String filename) {
		filename = "./target/screenshots/" + filename + "-" + new Date().getTime() + ".png";
		File outputFile = new File(filename);
		try {
			File screeenshotFile = ((TakesScreenshot) driver.get()).getScreenshotAs(OutputType.FILE);
			System.out.println("****** Placed screenshot as " + filename + " ******");
			FileUtils.copyFile(screeenshotFile, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputFile;
	}
	
	public static void setUpDriver() {
		if (seleniumDriver.get() != null) {
			System.out.println("****** El driver no se ha cerrado correctamente ******");
			driver.get().quit();
		}
		seleniumDriver.set(new SeleniumDriver(null));
	}
	
	public static void tearDown() {
		if (driver.get() != null) {
			try {
				driver.get().quit();
			} catch (NoSuchSessionException e) {
				System.out.println("****** El driver ya ha sido cerrado previamente ******");
			}
			
		} else {
			System.out.println("****** El driver no existe ******");
		}
		seleniumDriver.set(null);
	}
	
	public static WebDriver getDriver() {
		return driver.get();
	}

}
