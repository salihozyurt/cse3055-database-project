package ui.fragments;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ui.utils.AppColors;
import ui.utils.FontLoader;
import ui.utils.AppProps;

public class InputFragment {

    private static Label createWarningLabel(String warningMessage) {
        Label warningLabel = new Label(warningMessage);
        warningLabel.setTextFill(Color.web(AppColors.whiteColor));
        warningLabel.setFont(FontLoader.openSans);
        return warningLabel;
    }

    public static Group createInputFragment(String promptText, String cssClassName, boolean isErrorMessageExistent) {
        VBox container = new VBox(AppProps.appWidth * 0.005);
        Text prompt = new Text(promptText);
        prompt.setFont(FontLoader.openSans);
        TextField input = new TextField();
        input.getStyleClass().add(cssClassName);
        input.setMinHeight(AppProps.appHeight * 0.04);
        input.setFont(FontLoader.openSans);
        input.setMinWidth(AppProps.appWidth * 0.3);
        container.getChildren().addAll(prompt, input);
        if (isErrorMessageExistent) {
            container.getChildren().add(createWarningLabel("Email is not valid."));
        }
        return new Group(container);
    }

    static Group createPasswordInputFragment(String promptText, String cssClassName, boolean isErrorMessageExistent) {
        VBox container = new VBox(AppProps.appWidth * 0.005);
        Text prompt = new Text(promptText);
        prompt.setFont(FontLoader.openSans);
        PasswordField input = new PasswordField();
        input.getStyleClass().add(cssClassName);
        input.setMinHeight(AppProps.appHeight * 0.04);
        input.setMinWidth(AppProps.appWidth * 0.3);
        container.getChildren().addAll(prompt, input);
        if (isErrorMessageExistent) {
            container.getChildren().add(createWarningLabel("Password is not valid"));
        }
        return new Group(container);
    }
}
