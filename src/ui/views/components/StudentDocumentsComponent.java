package ui.views.components;

import database.query.CourseRecordQuery;
import database.query.NodeQuery;
import database.tools.QueryExecutor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ui.fragments.AppFragment;
import ui.fragments.InputFragment;
import ui.utils.AppColors;
import ui.utils.AppProps;
import ui.utils.FontLoader;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDocumentsComponent extends ComponentUtils implements CompoundComponent {

    public static StudentDocumentsComponent instance = new StudentDocumentsComponent();

    private StudentDocumentsComponent() {}

    @Override
    public Group createComponent(String personId) {
        AppFragment.appCluster.setCenter(new Group());
        HBox mainLayout = new HBox(AppProps.appHeight * 0.1);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMinSize(AppProps.appWidth * 0.75, AppProps.appHeight * 1.0);
        mainLayout.setStyle("-fx-background-color: " + AppColors.accentColor);
        VBox documentContainer = new VBox(AppProps.appHeight * 0.02);
        documentContainer.setAlignment(Pos.CENTER);
        documentContainer.getChildren().addAll(createTitle("Documents Given"));
        addGivenDocuments(personId, documentContainer);
        mainLayout.getChildren().add(new Group(documentContainer));
        return new Group(mainLayout);
    }

    private void addGivenDocuments(String personId, VBox container) {
        ResultSet givenDocuments = QueryExecutor.getInstance().executeSelect(
                "EXEC get_lecture_notes @PersonId = " + personId
        );
        while (true) {
            try {
                if (!givenDocuments.next()) break;
                Text title = new Text(givenDocuments.getString("title"));
                title.setFont(FontLoader.gudeaBold);
                title.setStyle("-fx-font-size: " + AppProps.appHeight * 0.03 + "px");
                Text description = new Text(givenDocuments.getString("node_description"));
                description.setStyle("-fx-font-size: " + AppProps.appHeight * 0.018 + "px");
                description.setFont(FontLoader.gudea);
                Text documentName = new Text(givenDocuments.getString("document"));
                documentName.setFont(FontLoader.gudea);
                documentName.setFill(Color.web(AppColors.redColor));
                VBox documentNode = new VBox(AppProps.appHeight * 0.01);
                documentNode.getStyleClass().add("container");
                applyOnContainerShadowEffect(documentNode, -1);
                documentNode.setAlignment(Pos.CENTER);
                documentNode.setPadding(new Insets(AppProps.appHeight * 0.02));
                documentNode.getChildren().addAll(title, description, documentName);
                container.getChildren().addAll(documentNode);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Label getWarningLabel(Group container) {
        return (Label) ((VBox) container.getChildren().get(0)).getChildren().get(2);
    }

    private String getNormalInputText(Group container) {
        return ((TextField) ((VBox) container.getChildren().get(0)).getChildren().get(1)).getText();
    }

    private boolean checkEmptyFormInputs(Group[] form) {
        return getNormalInputText(form[0]).isEmpty()|| getNormalInputText(form[1]).isEmpty()
                || getNormalInputText(form[2]).isEmpty();
    }
}
