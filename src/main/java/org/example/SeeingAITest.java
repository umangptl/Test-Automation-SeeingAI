package org.example;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SeeingAITest {
    private static ExtentReports extent;
    private static ExtentTest test;
    public static AppiumDriver driver;
    public static int totalTests = 25;
    public static int passCount = 0;

    @BeforeMethod
    public void setUp() {
        ExtentSparkReporter spark = new ExtentSparkReporter("Spark.html");
        extent = new ExtentReports();

        extent.attachReporter(spark);
    }

    @AfterMethod
    public void tearDown() {
        if (extent != null) {
            extent.flush();
        }
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterClass
    public static void finalCheck() {
        File reportFile = new File("Spark.html");
        System.out.println("Final report generated: " + reportFile.exists());
    }

    public static void main(String[] args) {
        SeeingAITest testClass = new SeeingAITest();
        testClass.setUp(); // Call setUp to initialize extent
        test = extent.createTest("SeeingAI Test", "Verify SeeingAI functionality");

        appiumTest();
        double passRate = (double) passCount / totalTests * 100;
        System.out.println("Test Pass: " + passCount + "/" + totalTests );
        System.out.println("Pass %: " + passRate + "%");
        testClass.tearDown();
    }


    public static void appiumTest() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "android");
        caps.setCapability("platformVersion", "13");
        caps.setCapability("deviceName", "pixel-8");
        caps.setCapability("app", "seeingai.apk");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("appPackage", "com.microsoft.seeingai");
        caps.setCapability("appActivity", "crc64a8457ff90b487ee0.SplashActivity");

        List<String> expectedResults = readExpectedResultsFromFile("expected.txt");

        try {
            URL url = new URL("http://127.0.0.1:4723/wd/hub/");
            driver = new AndroidDriver(url, caps);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

            skipButton(wait);
            acceptTerms(wait);
            getStarted(wait);
            handlePermissionDialog(wait);
            closeOverlay(wait);
            navigateToHome();
            selectPhoto(wait);

            for (int i = 0; i < totalTests; i++) {
                appium(wait, expectedResults.get(i));
            }

        } catch (MalformedURLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    private static void appium(WebDriverWait wait, String expectedResult) throws InterruptedException {
        sharePhoto(wait);
        compareResults(wait, expectedResult);
        goBack(wait);
        swipeToNextImage();
    }

    private static List<String> readExpectedResultsFromFile(String filePath) {
        List<String> expectedResults = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                expectedResults.add(line.trim());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return expectedResults;
    }

    private static void compareResults(WebDriverWait wait, String expectedResult) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.microsoft.seeingai:id/result_cell_text")));
        String actualResult = element.getText().toLowerCase();
        System.out.println("Expected Result: " + expectedResult);
        System.out.println("Actual Result: " + actualResult);
        boolean pass = true;
        String[] expectedWords = expectedResult.toLowerCase().split(" ");
        for (String word : expectedWords) {
            if (!actualResult.contains(word)) {
                pass = false;
                break;
            }
        }
        if (pass) {
            passCount++;
            test.log(Status.PASS, "Expected Result: " + expectedResult + ", Actual Result: " + actualResult);
            System.out.println("Pass");
        } else {
            test.log(Status.FAIL, "Expected Result: " + expectedResult + ", Actual Result: " + actualResult);
            System.out.println("Fail");
        }
    }

    private static void skipButton(WebDriverWait wait) {
        WebElement skipButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.microsoft.seeingai:id/pagedSkipButton")));
        skipButton.click();
        test.log(Status.INFO, "Skip Tutorial");
    }

    private static void acceptTerms(WebDriverWait wait) {
        WebElement termsCheckBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.microsoft.seeingai:id/terms_check_box")));
        termsCheckBox.click();
        test.log(Status.INFO, "Accept Terms and Conditions");
    }

    private static void getStarted(WebDriverWait wait) {
        WebElement getStartedButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.microsoft.seeingai:id/terms_getstarted_button")));
        getStartedButton.click();
        test.log(Status.INFO, "Get Started");
    }

    private static void handlePermissionDialog(WebDriverWait wait) {
        WebElement allowButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button")));
        allowButton.click();
        test.log(Status.INFO, "Allow permission for photos");
    }

    private static void closeOverlay(WebDriverWait wait) {
        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.microsoft.seeingai:id/close_icon_bottom_sheet")));
        closeButton.click();
        test.log(Status.INFO, "Close Overlay");
    }

    private static void navigateToHome() {
        ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.HOME));
        test.log(Status.INFO, "Navigate to Home Screen");
        System.out.println("At Home");
    }

    private static void selectPhoto(WebDriverWait wait) {
        WebElement fileApp = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//android.widget.TextView[@content-desc=\"Photos\"]")));
        fileApp.click();
        test.log(Status.INFO, "In Photos App");
        System.out.println("In Photos App");

        WebElement morePhotos = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//android.support.v7.widget.RecyclerView[@resource-id=\"com.google.android.apps.photos:id/recycler_view\"]/android.widget.FrameLayout/android.widget.LinearLayout")));
        morePhotos.click();

        WebElement photo1 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//android.view.ViewGroup[@content-desc=\"Photo taken on May 4, 2024 6:52:01 PM\"])[1]")));
        photo1.click();
    }

    private static void sharePhoto(WebDriverWait wait) {
        WebElement shareButton2 = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.google.android.apps.photos:id/share")));
        shareButton2.click();
        WebElement appToShare = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//android.widget.TextView[@resource-id=\"com.google.android.apps.photos:id/text\" and @text=\"SeeingAI\"]")));
        appToShare.click();
        System.out.println("Sharing Image to SeeingAI");
    }

    private static void goBack(WebDriverWait wait) {
        WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//android.widget.ImageButton[@content-desc=\"Navigate up\"]")));
        backButton.click();
    }

    private static void swipeToNextImage() throws InterruptedException {
        Thread.sleep(1000);
        TouchAction ta = new TouchAction((PerformsTouchActions) driver);
        ta.press(PointOption.point(900, 1200)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000))).moveTo(PointOption.point(100, 1200)).release().perform();
        Thread.sleep(1000);
        test.log(Status.INFO, "Swipe to Next Image");
        System.out.println("swiped to next Image-----------------");
    }
}
