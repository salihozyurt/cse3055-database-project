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

public class LectureAnnouncementComponent extends ComponentUtils implements CompoundComponent {

    public static LectureAnnouncementComponent instance = new LectureAnnouncementComponent();

    private LectureAnnouncementComponent() {}

    @Override
    public Group createComponent(String personId) {
        AppFragment.appCluster.setCenter(new Group());
        HBox mainLayout = new HBox(AppProps.appHeight * 0.1);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMinSize(AppProps.appWidth * 0.75, AppProps.appHeight * 1.0);
        mainLayout.setStyle("-fx-background-color: " + AppColors.accentColor);
        VBox announcementContainer = new VBox(AppProps.appHeight * 0.02);
        announcementContainer.setAlignment(Pos.CENTER);
        Circle announcementAddButton = new Circle(12, new ImagePattern(new Image(
                "/vendors/img/add_icon.png"
        )));
        announcementAddButton.setOnMouseEntered(e -> AppProps.appScene.setCursor(Cursor.HAND));
        announcementAddButton.setOnMouseExited(e -> AppProps.appScene.setCursor(Cursor.DEFAULT));
        announcementAddButton.setOnMouseClicked(e -> {
            Stage inputPanel = new Stage();
            VBox form = new VBox(AppProps.appHeight * 0.01);
            String[] inputPrompts = new String[]{"Title", "Description"};
            Group[] inputFragments = new Group[2];
            inputFragments[0] = InputFragment.createInputFragment(inputPrompts[0], "loginInput", false);
            inputFragments[1] = InputFragment.createInputFragment(inputPrompts[1], "loginInput", true);
            for (Group fragment : inputFragments) {
                form.getChildren().add(fragment);
            }
            Button sendButton = createSendButton();
            sendButton.setOnAction(e2 -> {
                getWarningLabel(inputFragments[1]).setTextFill(Color.web(AppColors.whiteColor));
                if (checkEmptyFormInputs(inputFragments)) {
                    getWarningLabel(inputFragments[1]).setText("Some inputs are empty. Please fill them.");
                    getWarningLabel(inputFragments[1]).setTextFill(Color.web(AppColors.secondaryColor));
                } else {
                    NodeQuery.instance.addAnnouncement(CourseRecordQuery.instance.getDataFrom(
                            "course_id", "person_id", personId
                            ), getNormalInputText(inputFragments[0]), getNormalInputText(inputFragments[1]));
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
        Pane buttonPadding = new StackPane(announcementAddButton);
        ComponentUtils.applyOnContainerShadowEffect(buttonPadding, 99);
        buttonPadding.setPadding(new Insets(AppProps.appHeight * 0.005));
        buttonPadding.getStyleClass().add("container");
        if (!LecturerClassroomComponent.canTakeCourse) {
            announcementContainer.getChildren().add(new Group(buttonPadding));
        }
        announcementContainer.getChildren().addAll(createTitle("Documents Given"));
        addGivenAnnouncements(personId, announcementContainer);
        mainLayout.getChildren().add(new Group(announcementContainer));
        return new Group(mainLayout);
    }

    private void addGivenAnnouncements(String personId, VBox container) {
        ResultSet givenAnnouncements = QueryExecutor.getInstance().executeSelect(
                "EXEC get_announcements @PersonId = " + personId
        );
        while (true) {
            try {
                if (!givenAnnouncements.next()) break;
                Text title = new Text(givenAnnouncements.getString("title"));
                title.setFont(FontLoader.gudeaBold);
                title.setStyle("-fx-font-size: " + AppProps.appHeight * 0.03 + "px");
                Text description = new Text(givenAnnouncements.getString("node_description"));
                description.setStyle("-fx-font-size: " + AppProps.appHeight * 0.018 + "px");
                description.setFont(FontLoader.gudea);
                VBox announcementNode = new VBox(AppProps.appHeight * 0.01);
                announcementNode.getStyleClass().add("container");
                applyOnContainerShadowEffect(announcementNode, -1);
                announcementNode.setAlignment(Pos.CENTER);
                announcementNode.setPadding(new Insets(AppProps.appHeight * 0.02));
                announcementNode.getChildren().addAll(title, description);
                container.getChildren().addAll(announcementNode);
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
        return getNormalInputText(form[0]).isEmpty()|| getNormalInputText(form[1]).isEmpty();
    }
}
