package ui.views.components;

import database.query.*;
import database.tools.DatabaseTables;
import database.tools.QueryExecutor;
import database.tools.ResultAnalyser;
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
import java.util.ArrayList;

public class StudentHomeworkComponent extends ComponentUtils implements CompoundComponent {

    public static StudentHomeworkComponent instance = new StudentHomeworkComponent();

    private StudentHomeworkComponent() {}

    @Override
    public Group createComponent(String personId) {
        AppFragment.appCluster.setCenter(new Group());
        HBox mainLayout = new HBox(AppProps.appHeight * 0.1);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMinSize(AppProps.appWidth * 0.75, AppProps.appHeight * 1.0);
        mainLayout.setStyle("-fx-background-color: " + AppColors.accentColor);
        VBox homeworkContainer = new VBox(AppProps.appHeight * 0.02);
        homeworkContainer.setAlignment(Pos.CENTER);
        homeworkContainer.getChildren().addAll(createTitle("Homeworks Given"));
        addGivenHomeworks(personId, homeworkContainer);
        mainLayout.getChildren().add(new Group(homeworkContainer));
        return new Group(mainLayout);
    }

    private void addGivenHomeworks(String personId, VBox container) {
        ArrayList<String> sentHomeworks = new ArrayList<>();
        ResultSet sentHomeworkSet = QueryExecutor.getInstance().executeSelect(
                "EXEC get_homeworks_done @PersonId = " + personId
        );
        while(true) {
            try {
                if (!sentHomeworkSet.next()) break;
                sentHomeworks.add(sentHomeworkSet.getString("assignment_id"));
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        ResultSet givenHomeworks = QueryExecutor.getInstance().executeSelect(
                "EXEC detect_student_homeworks @PersonId = " + personId
        );
        while (true) {
            try {
                if (!givenHomeworks.next()) break;
                String nodeId = givenHomeworks.getString("node_id");
                Text title = new Text(givenHomeworks.getString("title"));
                title.setFont(FontLoader.gudeaBold);
                title.setStyle("-fx-font-size: " + AppProps.appHeight * 0.03 + "px");
                Text description = new Text(givenHomeworks.getString("node_description"));
                description.setStyle("-fx-font-size: " + AppProps.appHeight * 0.018 + "px");
                description.setFont(FontLoader.gudea);
                Text courseCode = new Text(givenHomeworks.getString("course_code"));
                courseCode.setFont(FontLoader.gudea);
                courseCode.setStyle("-fx-font-size: " + AppProps.appHeight * 0.02 + "px");
                courseCode.setFill(Color.web(AppColors.secondaryColor));
                Text endDate = new Text(givenHomeworks.getString("end_date"));
                endDate.setFont(FontLoader.gudeaBold);
                endDate.setFill(Color.web(AppColors.redColor));
                VBox homeWorkNode = new VBox(AppProps.appHeight * 0.01);
                homeWorkNode.getStyleClass().add("container");
                applyOnContainerShadowEffect(homeWorkNode, -1);
                homeWorkNode.setAlignment(Pos.CENTER);
                homeWorkNode.setPadding(new Insets(AppProps.appHeight * 0.02));
                boolean isHomeworkSend = sentHomeworks.contains(givenHomeworks.getString("node_id"));
                Circle homeworkSendButton = new Circle(12, new ImagePattern(new Image(
                        isHomeworkSend ? "/vendors/img/check_icon.png" : "/vendors/img/send_icon.png"
                )));
                if (!isHomeworkSend) {
                    homeworkSendButton.setOnMouseEntered(e -> AppProps.appScene.setCursor(Cursor.HAND));
                    homeworkSendButton.setOnMouseExited(e -> AppProps.appScene.setCursor(Cursor.DEFAULT));
                    homeworkSendButton.setOnMouseClicked(e -> {
                        Stage inputPanel = new Stage();
                        VBox form = new VBox(AppProps.appHeight * 0.01);
                        Group fragment = InputFragment.createInputFragment("Document Name", "loginInput", true);
                        form.getChildren().add(fragment);
                        Button sendButton = createSendButton();
                        sendButton.setOnAction(e2 -> {
                            getWarningLabel(fragment).setTextFill(Color.web(AppColors.whiteColor));
                            if (checkEmptyFormInput(fragment)) {
                                getWarningLabel(fragment).setText("Document name is empty. Please fill it.");
                                getWarningLabel(fragment).setTextFill(Color.web(AppColors.secondaryColor));
                            } else {
                                DeliveryQuery.instance.sendDelivery(
                                        personId, nodeId, getNormalInputText(fragment)
                                );
                                inputPanel.close();
                                AppFragment.appCluster.setCenter(createComponent(personId));
                            }
                        });
                        VBox buttonWrapper = new VBox(sendButton);
                        buttonWrapper.setAlignment(Pos.CENTER);
                        buttonWrapper.setPadding(new Insets(AppProps.appHeight * 0.04, 0, 0, 0));
                        form.getChildren().add(buttonWrapper);
                        BorderPane layout = new BorderPane(new Group(form));
                        Scene inputScene = new Scene(layout, AppProps.appWidth * 0.4, AppProps.appHeight * 0.4);
                        inputScene.getStylesheets().add("/vendors/app.css");
                        inputPanel.setScene(inputScene);
                        inputPanel.show();
                    });
                }
                homeWorkNode.getChildren().addAll(title, description, endDate, courseCode, homeworkSendButton);
                container.getChildren().add(homeWorkNode);
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

    private boolean checkEmptyFormInput(Group form) {
        return getNormalInputText(form).isEmpty();
    }
}
