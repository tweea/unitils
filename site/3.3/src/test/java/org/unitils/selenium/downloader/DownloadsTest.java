package org.unitils.selenium.downloader;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Example: how can you get the file from an URL in an easy way.
 *
 * @author Willemijn Wouters
 *
 * @since
 *
 */
public class DownloadsTest {

    //START SNIPPET: downloadUrlFileUtilsExample
    @Test
    public void testDownloadUrl() throws Exception {
        URL url = new URL("http://www.urlFileToDownload.be/fileToDownload.pdf");

        File tempFile = File.createTempFile("expectedTempFile", ".pdf");
        FileUtils.copyURLToFile(url, tempFile);
        Assert.assertTrue(tempFile.exists());
    }
    //END SNIPPET: downloadUrlFileUtilsExample
}
