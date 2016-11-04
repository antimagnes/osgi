package hu.frigo.uberapp;

import hu.frigo.api.ServiceInterface;
import hu.frigo.uberapp.api.PaneFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.Logger;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.util.tracker.ServiceTracker;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class MainWindow extends Application {

//    private HostActivator hostActivator = null;
    private Felix felix = null;
    Logger logger = new Logger();

    public void start(Stage stage) throws Exception {
        Pane mainPane = PaneFactory.getInstance();
        Group theMainGroup = new Group(mainPane);
        Scene theScene = new Scene(theMainGroup, 400, 300);
        stage.setScene(theScene);
        Button majom = new Button("Majom");
        mainPane.getChildren().add(majom);
        majom.setOnAction((ActionEvent ae) -> {
            initFelix();
            logger.log(Logger.LOG_INFO, "felix.getVersion() = " + felix.getVersion());
            for (Bundle b : felix.getBundleContext().getBundles()) {
                logger.log(Logger.LOG_INFO, "b.getSymbolicName() = " + b.getSymbolicName());
                logger.log(Logger.LOG_INFO, "b.getLocation() = " + b.getLocation());
                logger.log(Logger.LOG_INFO, "b.getVersion() = " + b.getVersion());
            }
        });
        Button services = new Button("Services");
        mainPane.getChildren().add(services);
        services.setOnAction(ae -> {
            for (ServiceReference sr : felix.getRegisteredServices()) {
                System.out.println("sr = " + sr);
            }
        });
//        Button services = new Button("Services");
//        mainPane.getChildren().add(services);
//        services.setOnAction(ae -> {
//            for (ServiceReference sr : felix.getRegisteredServices()) {
//                System.out.println("sr = " + sr);
//            }
//        });
        Button servicesearch = new Button("Servicesearch");
        mainPane.getChildren().add(servicesearch);
        servicesearch.setOnAction(ae -> {
            ServiceTracker m_tracker =
                    new ServiceTracker(felix.getBundleContext(), ServiceInterface.class.getName(), null);
            m_tracker.open();
            for (Object o : m_tracker.getServices()) {
                System.out.println("o.getClass() = " + o.getClass().getName());
                try {
                    System.out.println("The cucc " + ((ServiceInterface)o).getTheMagicNumber());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            m_tracker.close();
        });
        stage.setOnCloseRequest(e -> {
            try {
                felix.stop();
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                Platform.exit();
            }
        });
        stage.show();
    }

    private Felix initFelix() {
//        hostActivator = new HostActivator();
        Map<String, String> felixConfigMap = new HashMap<>();
        logger.setLogLevel(Logger.LOG_DEBUG);
        logger.log(org.apache.felix.resolver.Logger.LOG_ERROR, "Init Felix");
//        felixConfigMap.put("felix.log.logger", logger);
        felixConfigMap.put("felix.log.level", Integer.toString(Logger.LOG_DEBUG));
        felixConfigMap.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
        FrameworkFactory ff = ServiceLoader.load(FrameworkFactory.class).iterator().next();
        felixConfigMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
                "hu.frigo.api; version=1.0.0");
        felix = (Felix) ff.newFramework(felixConfigMap);
        try {
            felix.init();
            BundleContext bundleContext = felix.getBundleContext();
            bundleContext.addBundleListener(bundleEvent -> logger.log(Logger.LOG_INFO,
                    bundleEvent.toString() + " - " + bundleEvent.getType()));
            bundleContext.addFrameworkListener(frameworkEvent -> {logger.log(Logger.LOG_INFO,
                    frameworkEvent.toString() + " - " + frameworkEvent.getType());});
            bundleContext.addServiceListener(serviceEvent -> {logger.log(Logger.LOG_INFO,
                    serviceEvent.toString() + " - " + serviceEvent.getType());});
//            hostActivator.start(bundleContext);
// ------------------ AUTO VERSION --------------------------
            Map apconfig = new HashMap();
            apconfig.put(AutoProcessor.AUTO_DEPLOY_ACTION_PROPERTY, "install, start");
            AutoProcessor.process(apconfig, felix.getBundleContext());
// ------------------ AUTO VERSION --------------------------
// ------------------ MANUAL VERSION --------------------------
//            String rs = "file:///home/frigo/omc/osgi/ideaproject/UberProject/bundle/org.apache.felix.gogo" +
//                    ".runtime-1.0.0.jar";
//            logger.log(org.apache.felix.resolver.Logger.LOG_INFO, "install " + rs);
//            Bundle runtime = bundleContext.installBundle(rs);
//            rs = "file:///home/frigo/omc/osgi/ideaproject/UberProject/bundle/org.apache.felix.gogo.shell-1.0.0.jar";
//            logger.log(org.apache.felix.resolver.Logger.LOG_INFO, "install " + rs);
//            Bundle shell = bundleContext.installBundle(rs);
//            logger.log(org.apache.felix.resolver.Logger.LOG_INFO, "start runtime");
//            runtime.start();
//            logger.log(org.apache.felix.resolver.Logger.LOG_INFO, "start shell");
//            shell.start();
// ------------------ MANUAL VERSION --------------------------
            felix.start();
//            felix.waitForStop(0L);
        } catch (BundleException e) {
            e.printStackTrace();
        }
//      catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return felix;
    }
}
