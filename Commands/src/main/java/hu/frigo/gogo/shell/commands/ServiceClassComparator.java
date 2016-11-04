package hu.frigo.gogo.shell.commands;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.Comparator;

public final class ServiceClassComparator implements Comparator<ServiceReference<?>> {
        @Override
        public int compare(ServiceReference<?> o1, ServiceReference<?> o2) {
            String[] classes1 = (String[])o1.getProperty(Constants.OBJECTCLASS);
            String[] classes2 = (String[])o2.getProperty(Constants.OBJECTCLASS);
            return classes1[0].compareTo(classes2[0]);
        }
    }