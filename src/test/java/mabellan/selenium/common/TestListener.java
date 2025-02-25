package mabellan.selenium.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.extentreports.ExtentManager;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import utils.SeleniumDriver;

public class TestListener implements ITestListener {

	private static final ExtentReports extent = ExtentManager.getInstance();
	private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	private static final String testReportPath = "target/TestReport.html";
	private static final String extentReportsResourcesPath = "../src/test/resources/extentreports";
	private static final String screenshotsPath = "screenshots";

	@Override
	public void onTestStart(ITestResult result) {
		//Espera aleatoria de entre 0 y 15 para que no empiecen todos los test a la vez aunque se lancen al mismo tiempo
		SeleniumDriver.waitForPageToLoad((int) (Math.random() * 1000 * 15 + 1));

		String methodName = result.getMethod().getMethodName();
		String className = result.getTestClass().getName();
		ExtentTest extentTest = extent.createTest(className.substring(className.lastIndexOf(".") + 1) + " | " + methodName);
		test.set(extentTest);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		test.get().pass("Test Passed")
				.assignCategory("Pass");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		File outputFile = SeleniumDriver.screenshot(result.getName());
		test.get().fail(result.getThrowable())
				.addScreenCaptureFromPath(screenshotsPath + File.separator + outputFile.getName())
				.assignCategory("Fail");

		String idCarpeta = (String) result.getTestContext().getAttribute("idCarpeta");
		System.out.println("****** Error " + result.getName() + " has failed with idCarpeta " + idCarpeta + " ******");
		Reporter.log("<a href='"+ outputFile.getAbsolutePath() + "'> <img src='"+ outputFile.getAbsolutePath() + "' height='100' width='100'/> </a>");


	}

	@Override
	public void onTestSkipped(ITestResult result) {
		File outputFile = SeleniumDriver.screenshot(result.getName());

		if (result.wasRetried()) {
			test.get().skip(result.getThrowable())
					.addScreenCaptureFromPath(screenshotsPath + File.separator + outputFile.getName())
					.assignCategory("Retried");
		} else {
			test.get().skip(result.getThrowable())
					.addScreenCaptureFromPath(screenshotsPath + File.separator + outputFile.getName())
					.assignCategory("Skip");
		}
	}

	@Override
	public void onFinish(ITestContext context) {
		extent.flush();

		/*try {
			final Path reportPath = Paths.get(testReportPath);
			String content = new String(Files.readAllBytes(reportPath))
					.replace(
							"https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@b00a2d0486596e73dd7326beacf352c639623a0e/commons/img/logo.png",
							extentReportsResourcesPath + "/img/logo.png"
					).replace(
							"https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@ce8b10435bcbae260c334c0d0c6b61d2c19b6168/spark/css/spark-style.css",
							extentReportsResourcesPath + "/css/spark-style.css"
					).replace(
							"https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css",
							extentReportsResourcesPath + "/css/font-awesome-4.7.0/css/font-awesome.min.css"
					).replace(
							"https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@7cc78ce/spark/js/jsontree.js",
							extentReportsResourcesPath + "/js/jsontree.js"
					).replace(
							"https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@c05cd28cde1617b9d0c05a831daff6cb97fd9fd5/spark/js/spark-script.js",
							extentReportsResourcesPath + "/js/spark-script.js"
					).replace(
							" style=\"background-image: url('../src/test/resources/extentreports/img/logo.png')\"",
							""
					);

			Files.write(reportPath, content.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

}
