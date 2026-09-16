package utilities;

import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.Scenario;

public class CommonUtilities {

	private WebDriver driver;
	private WebDriverWait wait;

	public CommonUtilities(WebDriver driver) {
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	// Click
	public void click(WebElement element) {
		wait.until(ExpectedConditions.elementToBeClickable(element));
		element.click();
	}

	// SendKey
	public void type(WebElement element, String text) {
		wait.until(ExpectedConditions.visibilityOf(element));
		element.clear();
		element.sendKeys(text);
	}

	// Clear
	public void clear(WebElement element) {
		wait.until(ExpectedConditions.visibilityOf(element));
		element.clear();
	}

	// Screenshot Method
	public static void takeScreenshot(WebDriver driver, Scenario scenario, String status) {

		try {

			ChromeDriver chromeDriver = (ChromeDriver) driver;

			Map<String, Object> metrics = chromeDriver.executeCdpCommand("Page.getLayoutMetrics", new HashMap<>());
			Map<String, Object> contentSize = (Map<String, Object>) metrics.get("contentSize");

			double width = ((Number) contentSize.get("width")).doubleValue();
			double height = ((Number) contentSize.get("height")).doubleValue();

			Map<String, Object> clip = new HashMap<>();
			clip.put("x", 0);
			clip.put("y", 0);
			clip.put("width", width);
			clip.put("height", height);
			clip.put("scale", 1);

			Map<String, Object> params = new HashMap<>();
			params.put("format", "png");
			params.put("clip", clip);
			params.put("captureBeyondViewport", true);

			Map<String, Object> result = chromeDriver.executeCdpCommand("Page.captureScreenshot", params);

			byte[] screenshot = Base64.getDecoder().decode((String) result.get("data"));
			String screenshotName = status + " - " + scenario.getName();
			scenario.attach(screenshot, "image/png", screenshotName);
			System.out.println("Screenshot attached to Cucumber report: " + screenshotName);
		} catch (Exception e) {
			System.out.println("Screenshot failed: " + e.getMessage());
		}
	}

}
