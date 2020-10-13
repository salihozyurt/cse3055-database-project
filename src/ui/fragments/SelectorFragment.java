package ui.fragments;

import database.query.DepartmentQuery;
import database.tools.DatabaseTables;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ui.utils.AppProps;
import ui.utils.FontLoader;

public class SelectorFragment {

    private static ComboBox<String> departmentComboBox;

    public static Group createSelector(String[] departments) {
        departmentComboBox = new ComboBox<>(
                FXCollections.observableArrayList(departments)
        );
        departmentComboBox.getStyleClass().add("departmentComboBox");
        departmentComboBox.setValue(departments[0]);
        Text infoText = new Text("Department");
        infoText.setFont(FontLoader.openSans);
        VBox selectorWrapper = new VBox();
        selectorWrapper.setStyle("-fx-background-color: white; -fx-background-radius: " + AppProps.appHeight * 0.005 +
                ";-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.6), 16, -8, 0, 8)");
        selectorWrapper.setAlignment(Pos.CENTER);
        VBox styleLayout = new VBox(infoText, departmentComboBox);
        styleLayout.setSpacing(AppProps.appHeight * 0.01);
        styleLayout.setAlignment(Pos.CENTER_LEFT);
        selectorWrapper.getChildren().addAll(styleLayout);
        selectorWrapper.setPadding(new Insets(AppProps.appWidth * 0.02));
        return new Group(selectorWrapper);
    }

    public static String getDepartmentValue() {
        return new DepartmentQuery(DatabaseTables.DEPARTMENT).getDataFrom("department_id", "department_name",
                departmentComboBox.getValue());
    }
}
