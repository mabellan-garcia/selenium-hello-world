package com.extentreports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.io.IOException;

public class ExtentManager {
    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            extent = new ExtentReports();

            ExtentSparkReporter spark = new ExtentSparkReporter("target/TestReport.html");

            try {
                spark.loadXMLConfig("src/test/resources/extentreports/extent-config.xml");
            } catch (IOException e) {
                System.err.println("No se ha podido cargar la configuración CSS de ExtentReport");
                e.printStackTrace();
            }
            extent.attachReporter(spark);
        }
        return extent;
    }
}
