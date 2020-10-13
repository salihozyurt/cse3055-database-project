package ui.views.components;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ui.utils.AppProps;
import ui.utils.FontLoader;

public class InputPureComponent implements PureComponent {

    @Override
    public Group createComponent(String[] argumentSet) {
        VBox componentContainer = new VBox(AppProps.appHeight * 0.01);
        componentContainer.setAlignment(Pos.CENTER_LEFT);
        Text hintText = new Text(argumentSet[0]);
        hintText.setFont(FontLoader.openSans);
        TextField input = new TextField();
        componentContainer.setMinWidth(AppProps.appHeight * 0.3);
        input.getStyleClass().add("pureInput");
        componentContainer.getChildren().addAll(hintText, input);
        return new Group(componentContainer);
    }
}
