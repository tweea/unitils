package org.unitils.dbmaintainer.locator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.unitils.core.UnitilsException;

import static org.apache.commons.lang3.StringUtils.replace;

/**
 * Abstract class to locate resources on the classpath.
 * Will also look in jars that are in the classpath.
 *
 * @author tdr
 * @since 1.0.2
 */
public abstract class ClassPathResourceLocator {
    /* The logger instance for this class */
    private static Logger logger = LoggerFactory.getLogger(ClassPathResourceLocator.class);

    protected List<URL> resourceList;

    /**
     * Load resources from the classpath.
     * if <code>isConcreteResource</code> set to true
     * we search for a concrete resource.
     * then no deeper directory are attempted to search.
     *
     * @return List<URL>
     */
    public List<URL> loadResources(String path, Boolean isConcreteResource) {
        resourceList = new ArrayList<>();

        try {
            // will also check in external referenced jars.
            Enumeration<URL> resources = getClass().getClassLoader().getResources(path);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();

                resourceList.add(url);
                logger.debug(" Resource '" + url.toString() + "' added to resourcelist ");

                if (!isConcreteResource) {
                    List<URL> subResources = searchResources(url);
                    resourceList.addAll(subResources);
                }
            }
        } catch (IOException e) {
            throw new UnitilsException("Unable to scan for resources in path " + path + "", e);
        }

        return resourceList;
    }

    /**
     * Will use the Spring {@link PathMatchingResourcePatternResolver} to find a resource that corresponds to the <code>url</code>.
     *
     * @return List<URL>
     */
    protected List<URL> searchResources(URL url)
        throws IOException {
        PathMatchingResourcePatternResolver p = new PathMatchingResourcePatternResolver();
        Resource[] scriptResources = p.getResources(url.toString() + "**");
        List<URL> listScriptResources = new ArrayList<>();

        for (int i = 0; i < scriptResources.length; i++) {
            URL urlResource = fixJarUrl(scriptResources[i].getURL());

            listScriptResources.add(urlResource);
            logger.debug("Resource '" + urlResource.toString() + "' added to resourcelist ");
            try {
                urlResource.openStream();
            } catch (Exception e) {
                logger.error(" Resource '" + urlResource.toString() + "' is not found.", e);
            }
        }

        return listScriptResources;
    }

    public URL fixJarUrl(URL url)
        throws MalformedURLException {
        if (url.getProtocol().equals("jar")) {
            String fixedStr = url.toExternalForm();
            fixedStr = replace(fixedStr, "#", "%23");
            fixedStr = replace(fixedStr, "@", "%40");

            return new URL(fixedStr);
        }
        return url;
    }
}
