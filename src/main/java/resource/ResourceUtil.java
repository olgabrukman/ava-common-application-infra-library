package resource;


import io.Encoding;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class ResourceUtil {

    /**
     * Loads text resource to String.
     *
     * @param loadingClass The class that the resource "belongs to".
     * @param resourceName The name of the resource file to get.
     * @return The resource in String representation.
     * @throws Exception
     */
    public static String loadTextResource(Class loadingClass, String resourceName) throws Exception {
        ClassLoader classloader = loadingClass.getClassLoader();
        InputStream input = classloader.getResourceAsStream(resourceName);
        if (input == null) {
            //resource  not found
            throw MessageApi.getException("app00915",
                    "RESOURCE_NAME", resourceName);
        }
        return IOUtils.toString(input, Encoding.ENCODING);
    }

    /**
     * Loads text resource to File.
     *
     * @param loadingClass The class that the resource "belongs to".
     * @param resourceName The name of the resource file to get.
     * @return The resource in File representation.
     * @throws Exception
     */
    public static File getResourceAsFile(Class loadingClass, String resourceName) throws Exception {
        ClassLoader classloader = loadingClass.getClassLoader();
        URL url = classloader.getResource(resourceName);
        if (url == null) {
            //resource  not found
            throw MessageApi.getException("app00915",
                    "RESOURCE_NAME", resourceName);
        }

        return new File(url.getPath());
    }

}
