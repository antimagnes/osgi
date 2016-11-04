package hu.frigo.gogo.shell.commands;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext bc) throws Exception {
        BundleContext systemBundleContext = bc.getBundle(0).getBundleContext();
        Hashtable<String, Object> props = new Hashtable<>();
        props.put("osgi.command.scope", "felix");
        props.put("osgi.command.function", new String[] {
                "servicelist" });
        bc.registerService(Service.class.getName(), new Service(systemBundleContext), props);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {

    }
}
