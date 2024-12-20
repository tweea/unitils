package org.unitils.dbunit.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

/**
 * FileHandler: Util class for DbUnitModule..
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * @since 3.4
 */
public class FileHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);

    /**
     * Create temporary files to store the XML.
     */
    public File createTempFile(String dataSetResourceName) {
        LOGGER.debug("Creating temp file.");
        String tempDataSetResourceName = new String(dataSetResourceName);
        if (tempDataSetResourceName.endsWith("/")) {
            tempDataSetResourceName = tempDataSetResourceName.substring(0, tempDataSetResourceName.length() - 1);
        }
        int lastIndexOfSlash = tempDataSetResourceName.lastIndexOf("/");
        String nameFile = tempDataSetResourceName.substring(((lastIndexOfSlash < 0) ? 0 : lastIndexOfSlash + 1), tempDataSetResourceName.lastIndexOf("."));
        try {
            return File.createTempFile(nameFile + "-", ".xml");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Write {@link InputStream} to {@link File}
     */
    public void writeToFile(File file, InputStream in) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            String path = file.getAbsolutePath();
            LOGGER.debug("Writing preprocessed dataset to temp file: " + path);

            if (in.markSupported()) {
                in.mark(Integer.MAX_VALUE);
                IOUtils.copy(in, fos);
                in.reset();
            } else {
                IOUtils.copy(in, fos);
            }
            LOGGER.trace("inputstream visualised: \n" + IOUtils.toString(in));
            LOGGER.debug("End writing preprocessed dataset to file: " + path);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Delete all the files
     */
    public void deleteFiles(List<File> files) {
        for (File file : files) {
            LOGGER.debug("Deleting temp file: " + file.getAbsolutePath());
            if (file.exists()) {
                file.delete();
            } else {
                LOGGER.warn("File " + file.getName() + " not found.");
            }
        }
    }
}
