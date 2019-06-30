package osgi;


import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import resource.MessageApi;

import java.util.HashMap;

public class OsgiUtil {

    public static HashMap<Class, Object> predefinedServices;

    @SuppressWarnings("unchecked")
    static public <T> T getService(Class<T> serviceClass) throws Exception {
        if (predefinedServices != null) {
            Object predefined = predefinedServices.get(serviceClass);
            if (predefined == null) {
                //service is  not configured in predefined
                throw MessageApi.getException("app00910",
                        "SERVICE_NAME", serviceClass.getName());
            }
            return (T) predefined;
        }
        Bundle bundle = FrameworkUtil.getBundle(serviceClass);
        if (bundle == null) {
            //bundle not found
            throw MessageApi.getException("app00911",
                    "BUNDLE_NAME", serviceClass.getName());
        }
        BundleContext bundleContext = bundle.getBundleContext();
        if (bundleContext == null) {
            //bundle context not found
            throw MessageApi.getException("app00912",
                    "BUNDLE_NAME", serviceClass.getName());
        }

        ServiceReference serviceReference = bundleContext.getServiceReference(serviceClass.getName());
        if (serviceReference == null) {
            //service reference not found
            throw MessageApi.getException("app00913",
                    "SERVICE_NAME", serviceClass.getName());
        }

        T service = (T) bundleContext.getService(serviceReference);
        if (service == null) {
            //service not found
            throw MessageApi.getException("app00914",
                    "SERVICE_NAME", serviceClass.getName());
        }

        return service;
    }
}
