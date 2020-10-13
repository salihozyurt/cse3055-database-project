package ui.views.components;

import database.query.*;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ui.fragments.AppFragment;
import ui.fragments.InputFragment;
import ui.utils.AppColors;
import ui.utils.AppProps;
import ui.utils.FontLoader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LecturerDocumentsComponent extends ComponentUtils implements CompoundComponent {

    public static LecturerDocumentsComponent instance = new LecturerDocumentsComponent();

    private LecturerDocumentsComponent() {}

    @Override
    public Group createComponent(String personId) {
        AppFragment.appCluster.setCenter(new Group());
        HBox mainLayout = new HBox(AppProps.appHeight * 0.1);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMinSize(AppProps.appWidth * 0.75, AppProps.appHeight * 1.0);
        mainLayout.setStyle("-fx-background-color: " + AppColors.accentColor);
        VBox documentContainer = new VBox(AppProps.appHeight * 0.02);
        documentContainer.setAlignment(Pos.CENTER);
        Circle documentAddButton = new Circle(12, new ImagePattern(new Image(
                "/vendors/img/add_icon.png"
        )));
        documentAddButton.setOnMouseEntered(e -> AppProps.appScene.setCursor(Cursor.HAND));
        documentAddButton.setOnMouseExited(e -> AppProps.appScene.setCursor(Cursor.DEFAULT));
        documentAddButton.setOnMouseClicked(e -> {
            Stage inputPanel = new Stage();
            VBox form = new VBox(AppProps.appHeight * 0.01);
            String[] inputPrompts = new String[]{"Title", "Description", "Document Name"};
            Group[] inputFragments = new Group[3];
            for (int i = 0; i < inputPrompts.length; ++i) {
                if (i == inputPrompts.length - 1) {
                    inputFragments[i] = InputFragment.createInputFragment(inputPrompts[i], "loginInput", true);
                    break;
                }
                inputFragments[i] = InputFragment.createInputFragment(inputPrompts[i], "loginInput", false);
            }
            for (Group fragment : inputFragments) {
                form.getChildren().add(fragment);
            }
            Button sendButton = createSendButton();
            sendButton.setOnAction(e2 -> {
                getWarningLabel(inputFragments[2]).setTextFill(Color.web(AppColors.whiteColor));
                if (checkEmptyFormInputs(inputFragments)) {
                    getWarningLabel(inputFragments[2]).setText("Some inputs are empty. Please fill them.");
                    getWarningLabel(inputFragments[2]).setTextFill(Color.web(AppColors.secondaryColor));
                } else {
                    NodeQuery.instance.addDocument(CourseRecordQuery.instance.getDataFrom(
                            "course_id", "person_id", personId
                            ), getNormalInputText(inputFragments[0]), getNormalInputText(inputFragments[1]),
                            getNormalInputText(inputFragments[2]));
                    inputPanel.close();
                    AppFragment.appCluster.setCenter(createComponent(personId));
                }
            });
            VBox buttonWrapper = new VBox(sendButton);
            buttonWrapper.setAlignment(Pos.CENTER);
            buttonWrapper.setPadding(new Insets(AppProps.appHeight * 0.04, 0, 0, 0));
            form.getChildren().add(buttonWrapper);
            BorderPane layout = new BorderPane(new Group(form));
            Scene inputScene = new Scene(layout,AppProps.appWidth * 0.4, AppProps.appHeight * 0.4);
            inputScene.getStylesheets().add("/vendors/app.css");
            inputPanel.setScene(inputScene);
            inputPanel.show();
        });
        Pane buttonPadding = new StackPane(documentAddButton);
        ComponentUtils.applyOnContainerShadowEffect(buttonPadding, 99);
        buttonPadding.setPadding(new Insets(AppProps.appHeight * 0.005));
        buttonPadding.getStyleClass().add("container");
        if (!LecturerClassroomComponent.canTakeCourse) {
            documentContainer.getChildren().add(new Group(buttonPadding));
        }
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
