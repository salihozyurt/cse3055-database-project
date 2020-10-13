package ui.utils;

import javafx.geometry.Insets;
import javafx.scene.control.Button;

public class BadgeFactory {

    private static final String BADGE_COLOR = "#B78AE4";

    public static Button createBadge(String badgeText) {
        Button badge = new Button(badgeText);
        badge.getStyleClass().add("badge");
        badge.setFont(FontLoader.gudea);
        double verticalInset = AppProps.appHeight * 0.008, horizontalInset = AppProps.appHeight * 0.02;
        badge.setPadding(new Insets(verticalInset, horizontalInset, verticalInset, horizontalInset));
        badge.setStyle("-fx-background-color: " +  BADGE_COLOR + "; -fx-text-fill: white; -fx-font-size: " +
                AppProps.appHeight * 0.012 + "px;");
        return badge;
    }
}
