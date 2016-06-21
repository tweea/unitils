package org.unitils.selenium.downloader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.selenium.annotation.TestWebDriver;


/**
 * just an example how to use the RobotDownloader.
 *
 * @author Willemijn Wouters
 *
 * @since
 *
 */
//START SNIPPET: downloadFileExample
@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class DownloadFileExampleTest {
    @TestWebDriver
    private WebDriver driver;
    private RobotDownloader robotDownloaderIE;

    @Before
    public void setUp() {
        robotDownloaderIE = RobotDownloaderFactory.createDownloaderIE();
    }

    @Test
    public void testDownloadFile() throws Exception {

        driver.get("http://docs.spring.io/spring/docs/current/spring-framework-reference/pdf/");
        WebElement downloadLink = driver.findElement(By.linkText("spring-framework-ref..>"));
        robotDownloaderIE.clickAndSaveFileIE(downloadLink);
        robotDownloaderIE.checkIfDownloadedFileExists(downloadLink);

        robotDownloaderIE.deleteDownloadedFile(downloadLink);
    }
}
//END SNIPPET: downloadFileExample