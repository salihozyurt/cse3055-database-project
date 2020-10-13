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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ui.fragments.AppFragment;
import ui.fragments.InputFragment;
import ui.utils.AppColors;
import ui.utils.AppProps;
import ui.utils.FontLoader;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LecturerHomeworkComponent extends ComponentUtils implements CompoundComponent {

    public static LecturerHomeworkComponent instance = new LecturerHomeworkComponent();

    private LecturerHomeworkComponent() {}

    @Override
    public Group createComponent(String personId) {
        AppFragment.appCluster.setCenter(new Group());
        HBox mainLayout = new HBox(AppProps.appHeight * 0.1);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMinSize(AppProps.appWidth * 0.75, AppProps.appHeight * 1.0);
        mainLayout.setStyle("-fx-background-color: " + AppColors.accentColor);
        VBox homeworkContainer = new VBox(AppProps.appHeight * 0.02);
        homeworkContainer.setAlignment(Pos.CENTER);
        Circle homeworkAddButton = new Circle(12, new ImagePattern(new Image(
                "/vendors/img/add_icon.png"
        )));
        homeworkAddButton.setOnMouseEntered(e -> AppProps.appScene.setCursor(Cursor.HAND));
        homeworkAddButton.setOnMouseExited(e -> AppProps.appScene.setCursor(Cursor.DEFAULT));
        homeworkAddButton.setOnMouseClicked(e -> {
            Stage inputPanel = new Stage();
            VBox form = new VBox(AppProps.appHeight * 0.01);
            String[] inputPrompts = new String[]{"Title", "Description", "End Date"};
            Group[] inputFragments = new Group[3];
            for (int i = 0; i < inputPrompts.length; ++i) {
                if (i == inputPrompts.length - 1) {
                    inputFragments[i] = InputFragment.createInputFragment(inputPrompts[i], "loginInput", true);
                    break;
                }
                inputFragments[i] = InputFragment.createInputFragment(inputPrompts[i], "loginInput", false);
            }
            for (Group fragment: inputFragments) {
                form.getChildren().add(fragment);
            }
            Button sendButton = createSendButton();
            sendButton.setOnAction(e2 -> {
                getWarningLabel(inputFragments[2]).setTextFill(Color.web(AppColors.whiteColor));
                if (checkEmptyFormInputs(inputFragments)) {
                    getWarningLabel(inputFragments[2]).setText("Some inputs are empty. Please fill them.");
                    getWarningLabel(inputFragments[2]).setTextFill(Color.web(AppColors.secondaryColor));
                } else {
                    NodeQuery.instance.addHomework(CourseRecordQuery.instance.getDataFrom(
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
        Pane buttonPadding = new StackPane(homeworkAddButton);
        ComponentUtils.applyOnContainerShadowEffect(buttonPadding, 99);
        buttonPadding.setPadding(new Insets(AppProps.appHeight * 0.005));
        buttonPadding.getStyleClass().add("container");
        if (!LecturerClassroomComponent.canTakeCourse) {
            homeworkContainer.getChildren().add(new Group(buttonPadding));
        }
        homeworkContainer.getChildren().addAll(createTitle("Homeworks Given"));
        addGivenHomeworks(personId, homeworkContainer);
        mainLayout.getChildren().add(new Group(homeworkContainer));
        return new Group(mainLayout);
    }

    private void addGivenHomeworks(String personId, VBox container) {
        ResultSet givenHomeworks = QueryExecutor.getInstance().executeSelect(
                "EXEC get_current_homeworks @LecturerId = " + personId
        );
        while (true) {
            try {
                if (!givenHomeworks.next()) break;
                Text title = new Text(givenHomeworks.getString("title"));
                title.setFont(FontLoader.gudeaBold);
                title.setStyle("-fx-font-size: " + AppProps.appHeight * 0.03 + "px");
                Text description = new Text(givenHomeworks.getString("node_description"));
                description.setStyle("-fx-font-size: " + AppProps.appHeight * 0.018 + "px");
                description.setFont(FontLoader.gudea);
                Text endDate = new Text(givenHomeworks.getString("end_date"));
                endDate.setFont(FontLoader.gudea);
                endDate.setFill(Color.web(AppColors.redColor));
                VBox homeWorkNode = new VBox(AppProps.appHeight * 0.01);
                homeWorkNode.getStyleClass().add("container");
                applyOnContainerShadowEffect(homeWorkNode, -1);
                homeWorkNode.setAlignment(Pos.CENTER);
                homeWorkNode.setPadding(new Insets(AppProps.appHeight * 0.02));
                homeWorkNode.getChildren().addAll(title, description, endDate);
                String assignmentId = givenHomeworks.getString("assignment_id");
                Rectangle listSentHomeworksButton = new Rectangle(32, 32, new ImagePattern(new Image(
                        "/vendors/img/record_icon.png"
                )));
                listSentHomeworksButton.setOnMouseEntered(e -> AppProps.appScene.setCursor(Cursor.HAND));
                listSentHomeworksButton.setOnMouseExited(e -> AppProps.appScene.setCursor(Cursor.DEFAULT));
                listSentHomeworksButton.setOnMouseClicked(e -> {
                    ResultSet finishedHomeworkSet = QueryExecutor.getInstance().executeSelect(
                            "EXEC get_delivered_homeworks @AssignmentId = " + assignmentId
                    );
                    ArrayList<String> studentsWhoTheirHomeworkIsFinished = new ArrayList<>();
                    while (true) {
                        try {
                            if (!finishedHomeworkSet.next()) break;
                            studentsWhoTheirHomeworkIsFinished.add(finishedHomeworkSet.getString("student_id"));
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (studentsWhoTheirHomeworkIsFinished.isEmpty()) return;
                    Stage homeworkStage = new Stage();
                    homeworkStage.getIcons().add(new Image("/vendors/img/record_icon.png"));
                    VBox homeworkContainer = new VBox(AppProps.appHeight * 0.01);
                    homeworkContainer.setAlignment(Pos.CENTER);
                    for(String studentId: studentsWhoTheirHomeworkIsFinished) {
                        VBox homeworkUnit = new VBox(AppProps.appHeight * 0.005);
                        homeworkUnit.setPadding(new Insets(AppProps.appHeight * 0.01));
                        homeworkUnit.setAlignment(Pos.CENTER);
                        Text fullName = new Text(PersonFullNameQuery.instance.getFullName(studentId));
                        fullName.setStyle("-fx-font-size: " + AppProps.appHeight * 0.016 + "px; -fx-font-family: 'Ubuntu Mono'");
                        ResultSet documentResult = QueryExecutor.getInstance().executeSelect(
                                "EXEC get_document @StudentId = " + studentId + ", @AssignmentId = " +
                                        assignmentId
                        );
                        Text documentName = new Text();
                        try {
                            documentResult.next();
                            documentName = new Text(documentResult.getString("document"));
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        documentName.setStyle("-fx-font-size: " + AppProps.appHeight * 0.016 + "px; -fx-font-family: 'Ubuntu Mono'");
                        documentName.setFill(Color.web(AppColors.greenColor));
                        homeworkUnit.getChildren().addAll(fullName, documentName);
                        homeworkContainer.getChildren().add(homeworkUnit);
                    }
                    BorderPane layout = new BorderPane(new Group(homeworkContainer));
                    Scene scene = new Scene(layout, AppProps.appWidth * 0.4,
                            studentsWhoTheirHomeworkIsFinished.size() * AppProps.appHeight * 0.2);
                    homeworkStage.setScene(scene);
                    homeworkStage.setTitle("Homeworks");
                    homeworkStage.show();
                });
                container.getChildren().addAll(homeWorkNode, listSentHomeworksButton);
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
