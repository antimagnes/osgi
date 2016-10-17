package hu.frigo.servicelistener;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

public class FrigoActivator implements BundleActivator, ServiceListener {
    public void start(BundleContext bundleContext) throws Exception {
        System.out.println("Starting to listen to services");
        bundleContext.addServiceListener(this);
    }

    public void stop(BundleContext bundleContext) throws Exception {
        System.out.println("Stopping listening to services");
        bundleContext.removeServiceListener(this);

    }

    public void serviceChanged(ServiceEvent serviceEvent) {
        System.out.println("serviceEvent = " + serviceEvent);
    }
}
