/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package hu.frigo.gogo.shell.commands;

import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
class Service {

    private final BundleContext bundleContext;

    Service(BundleContext bc) {
        bundleContext = bc;
    }

    @Descriptor("list installed bundles matching a substring")
    public void servicelist(
            @Descriptor("Shows only service class names") @Parameter(names = {"-n", "--names"}, presentValue =
                    "true", absentValue = "false") boolean onlyNames,
            @Parameter(names = {"-o", "--objectclass"}, absentValue = "") String objectClass) {
        if (onlyNames) {
            listNames();
        } else {
            List<ServiceReference<?>> serviceRefs = new ArrayList<>();
            Bundle[] bundles = bundleContext.getBundles();
            for (Bundle bundle : bundles) {
                ServiceReference<?>[] services = bundle.getRegisteredServices();
                if (services != null) {
                    for (ServiceReference<?> ref : services) {
                        String[] objectClasses = (String[]) ref.getProperty(Constants.OBJECTCLASS);
                        if (objectClass.equals("") ||
                                ObjectClassMatcher.matchesAtLeastOneName(objectClasses, objectClass)) {
                            serviceRefs.add(ref);
                        }
                    }
                }
            }

            Collections.sort(serviceRefs, new ServiceClassComparator());

            serviceRefs.forEach(this::printServiceRef);
        }
    }

    private void printServiceRef(ServiceReference<?> serviceRef) {
        String[] objectClass = (String[]) serviceRef.getProperty(Constants.OBJECTCLASS);
        String serviceClasses = ShellUtil.getValueString(objectClass);
        System.out.println(serviceClasses);
        System.out.println(ShellUtil.getUnderlineString(serviceClasses));

        printProperties(serviceRef);

        String bundleName = ShellUtil.getBundleName(serviceRef.getBundle());
        System.out.println("Provided by : ");
        System.out.println(" " + bundleName);
        Bundle[] usingBundles = serviceRef.getUsingBundles();
        if (usingBundles != null) {
            System.out.println("Used by: ");
            for (Bundle bundle : usingBundles) {
                System.out.println(" " + ShellUtil.getBundleName(bundle));
            }
        }
        System.out.println();
    }

    private void printProperties(ServiceReference<?> serviceRef) {
        for (String key : serviceRef.getPropertyKeys()) {
            if (!Constants.OBJECTCLASS.equals(key)) {
                System.out.println(" " + key + " = " + ShellUtil.getValueString(serviceRef.getProperty(key)));
            }
        }
    }

    private void listNames() {
        Map<String, Integer> serviceNames = getServiceNamesMap(bundleContext);
        ArrayList<String> serviceNamesList = new ArrayList<>(serviceNames.keySet());
        Collections.sort(serviceNamesList);
        for (String name : serviceNamesList) {
            System.out.println(name + " (" + serviceNames.get(name) + ")");
        }
    }

    private static Map<String, Integer> getServiceNamesMap(BundleContext bundleContext) {
        Map<String, Integer> serviceNames = new HashMap<>();
        Bundle[] bundles = bundleContext.getBundles();
        for (Bundle bundle : bundles) {
            ServiceReference<?>[] services = bundle.getRegisteredServices();
            if (services != null) {
                for (ServiceReference<?> serviceReference : services) {
                    String[] names = (String[]) serviceReference.getProperty(Constants.OBJECTCLASS);
                    for (String name : names) {
                        int curCount = (serviceNames.containsKey(name)) ? serviceNames.get(name) : 0;
                        serviceNames.put(name, curCount + 1);
                    }
                }
            }
        }
        return serviceNames;
    }
}