package ui.utils;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;

public class AppProps {

    public static double appWidth, appHeight;
    public static Scene appScene;

    public static void setSize() {
        Rectangle2D screenSize = Screen.getPrimary().getBounds();
        appWidth = screenSize.getWidth() * 0.6;
        appHeight = screenSize.getHeight() * 0.8;
    }
}
