package hu.frigo.uberapp;

import hu.frigo.uberapp.osgi.HostActivator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.Logger;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.FrameworkFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class MainWindow extends Application {

    private HostActivator hostActivator = null;
    private Felix felix = null;
    Logger logger = new Logger();

    public void start(Stage stage) throws Exception {
        Pane mainPane = new FlowPane(Orientation.VERTICAL);
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
        stage.setOnCloseRequest(e -> {
            try {
                felix.stop();
            } catch (BundleException e1) {
                e1.printStackTrace();
            } finally {
                Platform.exit();
            }
        });
        stage.show();
    }

    private Felix initFelix() {
        hostActivator = new HostActivator();
        Map felixConfigMap = new HashMap<>();
        logger.setLogLevel(Logger.LOG_DEBUG);
        logger.log(org.apache.felix.resolver.Logger.LOG_ERROR, "Onyaddal");
        felixConfigMap.put("felix.log.logger", logger);
        felixConfigMap.put("felix.log.level", Integer.toString(Logger.LOG_DEBUG));
        FrameworkFactory ff = ServiceLoader.load(FrameworkFactory.class).iterator().next();
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
            hostActivator.start(bundleContext);
            Map apconfig = new HashMap();
            apconfig.put(AutoProcessor.AUTO_DEPLOY_ACTION_PROPERTY, "install, start");
            AutoProcessor.process(apconfig, felix.getBundleContext());
            felix.start();
        } catch (BundleException e) {
            e.printStackTrace();
        }
        return felix;
    }
}
