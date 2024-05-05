package org.example;

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SeeingAITest {
    public static AppiumDriver driver;
    public static int totalTests = 25;
    public static int passCount = 0;

    public static void main(String[] args) {
        appiumTest();
        double passRate = (double) passCount / totalTests * 100;
        System.out.println("Test Pass: " + passCount + "/" + totalTests );
        System.out.println("Pass %: " + passRate + "%");
    }

    public static void appiumTest() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "android");
        caps.setCapability("platformVersion", "13");
        caps.setCapability("deviceName", "pixel-8");
        caps.setCapability("app", "package/.apk");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("appPackage", "com.microsoft.seeingai");
        caps.setCapability("appActivity", ".SplashActivity");

        List<String> expectedResults = readExpectedResultsFromFile();

        try {
            URL url = new URL("http://127.0.0.1:4723/wd/hub/");
            driver = new AndroidDriver(url, caps);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            skipButton(wait);
            acceptTerms(wait);
            getStarted(wait);
            handlePermissionDialog(wait);
            closeOverlay(wait);
            navigateToHome();
            selectPhoto(wait);

            for (int i = 0; i < totalTests; i++) {
                sharePhoto(wait);
                String expectedResult = expectedResults.get(i);
                getResults(wait, expectedResult);
                compareResults(expectedResult);
                goBack(wait);
                swipeToNextImage();
            }

        } catch (MalformedURLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> readExpectedResultsFromFile() {
        List<String> expectedResults = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("expected.txt"));
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

    private static void getResults(WebDriverWait wait, String expectedResult) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.microsoft.seeingai:id/result_cell_text")));
        String actualResult = element.getText();
        System.out.println("Expected Result: " + expectedResult);
        System.out.println("Actual Result: " + actualResult);
    }

    private static void compareResults(String expectedResult) {
        WebElement element = driver.findElement(By.id("com.microsoft.seeingai:id/result_cell_text"));
        String actualResult = element.getText().toLowerCase();
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
            System.out.println("Pass");
        } else {
            System.out.println("Fail");
        }
    }

    private static void skipButton(WebDriverWait wait) {
        WebElement skipButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.microsoft.seeingai:id/pagedSkipButton")));
        skipButton.click();
    }

    private static void acceptTerms(WebDriverWait wait) {
        WebElement termsCheckBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.microsoft.seeingai:id/terms_check_box")));
        termsCheckBox.click();
    }

    private static void getStarted(WebDriverWait wait) {
        WebElement getStartedButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.microsoft.seeingai:id/terms_getstarted_button")));
        getStartedButton.click();
    }

    private static void handlePermissionDialog(WebDriverWait wait) {
        WebElement allowButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button")));
        allowButton.click();
    }

    private static void closeOverlay(WebDriverWait wait) {
        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.microsoft.seeingai:id/close_icon_bottom_sheet")));
        closeButton.click();
    }

    private static void navigateToHome() {
        ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.HOME));
        System.out.println("At Home");
    }

    private static void selectPhoto(WebDriverWait wait) {
        WebElement fileApp = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//android.widget.TextView[@content-desc=\"Photos\"]")));
        fileApp.click();
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
        System.out.println("swiped to next Image-----------------");
    }
}
