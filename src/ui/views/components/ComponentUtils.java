package ui.views.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ui.utils.AppColors;
import ui.utils.AppProps;
import ui.utils.FontLoader;

public class ComponentUtils {

    protected VBox createInfoBox(String title, String info) {
        double verticalInset = AppProps.appHeight * 0.02, horizontalInset = AppProps.appHeight * 0.05;
        VBox nameBox = new VBox(AppProps.appHeight * 0.05);
        applyOnContainerShadowEffect(nameBox, -1);
        nameBox.getStyleClass().add("infoBox");
        nameBox.setAlignment(Pos.CENTER);
        nameBox.setPadding(new Insets(verticalInset, horizontalInset, verticalInset, horizontalInset));
        Text nameTitle = new Text(title);
        nameTitle.setFont(FontLoader.gudea);
        nameTitle.setFill(Color.web(AppColors.accentColorDark));
        Text nameContent = new Text(info);
        nameContent.setFont(FontLoader.gudeaBold);
        nameContent.setStyle("-fx-font-size: " + AppProps.appWidth * 0.02);
        nameBox.getChildren().addAll(nameTitle, nameContent);
        return nameBox;
    }

    public static void applyOnContainerShadowEffect(Pane container, double radius) {
        if (radius == -1) {
            radius = AppProps.appHeight * 0.01;
        }
        String radiusStyle = "-fx-background-radius: " + radius;
        container.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.0), 16, -8, 0, 0);" + radiusStyle);
        container.setOnMouseEntered(e -> {
            container.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 16, -8, 0, 0);" + radiusStyle);
        });
        container.setOnMouseExited(e -> {
            container.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.0), 16, -8, 0, 0);" + radiusStyle);
        });
    }

    protected boolean areInputsFilled(VBox inputForm) {
        for (int i = 0; i < inputForm.getChildren().size() - 2; ++i) {
            if (getInputText(inputForm, i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    protected String getInputText(VBox inputForm, int index) {
        return ((TextField) ((VBox) ((Group) inputForm.getChildren().get(index)).
                getChildren().get(0)).getChildren().get(1)).getText();
    }

    public Group createTitle(String title) {
        Text titleText = new Text(title);
        String radiusStyle = "-fx-background-radius: " + AppProps.appHeight * 0.01;
        titleText.setFont(FontLoader.gudeaBold);
        titleText.setStyle("-fx-font-size: " + AppProps.appHeight * 0.03 + "px");
        VBox wrapper = new VBox(titleText);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle(radiusStyle);
        wrapper.getStyleClass().add("container");
        wrapper.setMinWidth(AppProps.appHeight * 0.2);
        wrapper.setPadding(new Insets(AppProps.appHeight * 0.02));
        return new Group(wrapper);
    }

    public Button createSendButton() {
        Button sendButton = new Button("Send");
        sendButton.setFont(FontLoader.openSansSemiBold);
        sendButton.getStyleClass().add("sendButton");
        sendButton.setStyle("-fx-font-size: " + AppProps.appHeight * 0.015 + "px");
        double horizontalInset = AppProps.appHeight * 0.04, verticalInset = AppProps.appHeight * 0.01;
        sendButton.setPadding(new Insets(verticalInset, horizontalInset, verticalInset, horizontalInset));
        return sendButton;
    }
}
