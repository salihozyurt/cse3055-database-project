package ui.fragments;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import ui.utils.AppColors;
import ui.utils.AppProps;
import ui.utils.FontLoader;

public class IconTextFragment {

    public Group createIconTextFragment(String text, String iconPath) {
        HBox fragmentContainer = new HBox(AppProps.appHeight * 0.02);
        fragmentContainer.setAlignment(Pos.CENTER);
        Circle activeLight = new Circle(AppProps.appHeight * 0.005, Color.TRANSPARENT);
        activeLight.setStyle("-fx-stroke: transparent; -fx-stroke-width: 20px;");
        activeLight.setEffect(new Bloom(0.1));
        ImageView iconView = new ImageView(new Image(getClass().getResource(iconPath).toExternalForm()));
        HBox activeContainer = new HBox(AppProps.appHeight * 0.008);
        activeContainer.setAlignment(Pos.CENTER);
        activeContainer.getChildren().addAll(activeLight, iconView);
        Text iconText = new Text(text);
        iconText.setOnMouseEntered(e -> {
            String darkIconPath = iconPath.substring(0, iconPath.indexOf('.')) + "_dark.png";
            iconText.setStyle("-fx-fill: black;");
            iconView.setImage(new Image(getClass().getResource(darkIconPath).toExternalForm()));
        });
        iconText.setOnMouseExited(e -> {
            iconText.setStyle("-fx-fill: " + AppColors.accentColorDark);
            iconView.setImage(new Image(getClass().getResource(iconPath).toExternalForm()));
        });
        iconText.getStyleClass().add("iconText");
        iconText.setFont(FontLoader.gudeaBold);
        iconText.setStyle("-fx-font-size: " + AppProps.appHeight * 0.015 + "px; -fx-fill: " + AppColors.accentColorDark);
        fragmentContainer.getChildren().addAll(activeContainer, iconText);
        return new Group(fragmentContainer);
    }
}
