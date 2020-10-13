package ui.views.components;

import database.query.CourseQuery;
import database.query.CourseRecordQuery;
import database.tools.DatabaseTables;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import ui.fragments.AppFragment;
import ui.utils.AppColors;
import ui.utils.AppProps;
import ui.utils.FontLoader;

public class LecturerClassroomComponent extends ComponentUtils implements CompoundComponent {

    public static LecturerClassroomComponent instance = new LecturerClassroomComponent();
    public static boolean canTakeCourse;

    private LecturerClassroomComponent() {}

    @Override
    public Group createComponent(String personId) {
        canTakeCourse = true;
        AppFragment.appCluster.setCenter(new Group());
        HBox mainLayout = new HBox(AppProps.appHeight * 0.1);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMinSize(AppProps.appWidth * 0.75, AppProps.appHeight * 1.0);
        mainLayout.setStyle("-fx-background-color: " + AppColors.accentColor);
        VBox coursesTakenContainer = new VBox(AppProps.appHeight * 0.02);
        coursesTakenContainer.setAlignment(Pos.CENTER);
        coursesTakenContainer.getChildren().add(createTitle("Course Taken"));
        String[] coursesTaken = CourseQuery.instance.getTakenCourses(personId);
        for (String courseTaken: coursesTaken) {
            canTakeCourse = false;
            coursesTakenContainer.getChildren().add(createCourseNode(personId, courseTaken, NodeType.REMOVE));
        }
        VBox coursesAvailableContainer = new VBox(AppProps.appHeight * 0.02);
        coursesAvailableContainer.setAlignment(Pos.CENTER);
        coursesAvailableContainer.getChildren().add(createTitle("Available Courses"));
        String[] availableCoursesForLecturer = CourseQuery.instance.getAvailableLecturerCourses(personId);
        for (String availableCourse: availableCoursesForLecturer) {
            coursesAvailableContainer.getChildren().add(createCourseNode(personId, availableCourse, NodeType.ADD));
        }
        mainLayout.getChildren().addAll(new Group(coursesTakenContainer), new Group(coursesAvailableContainer));
        return new Group(mainLayout);
    }

    private Group createCourseNode(String personId, String courseId, NodeType nodeType) {
        VBox courseNodeContainer = new VBox(AppProps.appHeight * 0.02);
        courseNodeContainer.getStyleClass().add("container");
        courseNodeContainer.setMinWidth(AppProps.appHeight * 0.3);
        ComponentUtils.applyOnContainerShadowEffect(courseNodeContainer, -1);
        courseNodeContainer.setAlignment(Pos.CENTER);
        courseNodeContainer.setPadding(new Insets(AppProps.appHeight * 0.02));
        Text courseName = new Text(
                CourseQuery.instance.getDataFrom("course_name", "course_id", courseId)
        );
        courseName.setFont(FontLoader.gudea);
        courseName.setStyle("-fx-fill: " + AppColors.primaryColor + ";-fx-font-size: " + AppProps.appHeight * 0.016 + "px");
        Text courseCode = new Text(
                CourseQuery.instance.getDataFrom("course_code", "course_id", courseId)
        );
        courseCode.setFont(FontLoader.gudeaBold);
        courseCode.setStyle("-fx-font-size: " + AppProps.appHeight * 0.02 + "px");
        Circle actionButton = new Circle(12, new ImagePattern(new Image(
                "/vendors/img/" + (nodeType == NodeType.ADD ? "add_icon.png": "remove_icon.png"))));
        actionButton.setOnMouseEntered(e -> AppProps.appScene.setCursor(Cursor.HAND));
        actionButton.setOnMouseExited(e -> AppProps.appScene.setCursor(Cursor.DEFAULT));
        actionButton.setOnMouseClicked(e -> {
            if (nodeType == NodeType.ADD) {
                CourseRecordQuery.instance.addCourseRecord(personId, courseId);
            } else {
                CourseRecordQuery.instance.removeCourseRecord(personId, courseId);
            }
            AppFragment.appCluster.setCenter(createComponent(personId));
        });
        courseNodeContainer.getChildren().addAll(courseName, courseCode);
        if ((nodeType == NodeType.ADD && canTakeCourse) || nodeType == NodeType.REMOVE)
            courseNodeContainer.getChildren().add(actionButton);
        return new Group(courseNodeContainer);
    }
}
