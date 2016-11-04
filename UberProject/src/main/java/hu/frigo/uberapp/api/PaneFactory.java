package hu.frigo.uberapp.api;

import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

public class PaneFactory {

    private static Pane thePane;

    public static Pane getInstance() {
        if (thePane == null) {
            thePane = new FlowPane(Orientation.VERTICAL);
        }
        return thePane;
    }
}
