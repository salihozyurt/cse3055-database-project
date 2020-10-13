package ui.views;

import database.query.PersonFullNameQuery;
import database.tools.DatabaseTables;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ui.fragments.AppFragment;
import ui.fragments.AuthenticationFragment;
import ui.fragments.IconTextFragment;
import ui.utils.*;
import ui.views.components.*;

public class LecturerView implements View {

    public static LecturerView instance = new LecturerView();
    private BorderPane appCluster;

    private LecturerView() {
        appCluster = AppFragment.appCluster;
    }

    @Override
    public void injectContentIntoCluster(String personId) {
        VBox userNode = new VBox(AppProps.appHeight * 0.02);
        Text usernameText = new Text(PersonFullNameQuery.instance.getFullName(personId));
        usernameText.setFont(FontLoader.openSansSemiBold);
        usernameText.setStyle("-fx-font-size: " + AppProps.appHeight * 0.02 +"px;");
        userNode.getStyleClass().add("userNode");
        userNode.setAlignment(Pos.CENTER);
        userNode.setMinWidth(AppProps.appWidth * 0.2);
        userNode.setMinHeight(AppProps.appHeight * 0.25);
        userNode.getChildren().addAll(UserIconCreator.createUserIcon(personId, usernameText.getText()),
                usernameText, BadgeFactory.createBadge("LECTURER"));
        VBox menuNode = new VBox(AppProps.appHeight * 0.03);
        menuNode.setAlignment(Pos.CENTER_LEFT);
        menuNode.setPadding(new Insets(0, 0, 0, AppProps.appWidth * 0.05));
        Text nodeTitle = new Text("MENU");
        nodeTitle.setFont(FontLoader.gudeaBold);
        nodeTitle.setStyle("-fx-font-size: " + AppProps.appHeight * 0.015 + "px; -fx-fill: " + AppColors.accentColorDark + ";");
        VBox nodeTitleWrapper = new VBox(nodeTitle);
        nodeTitleWrapper.setPadding(new Insets(0, 0, AppProps.appHeight * 0.01, AppProps.appHeight * 0.05));
        IconTextFragment iconTextGenerator = new IconTextFragment();
        Group profile = iconTextGenerator.createIconTextFragment("Profile", "/vendors/img/profile_icon.png");
        Group courses = iconTextGenerator.createIconTextFragment("Courses", "/vendors/img/courses_icon.png");
        Group homeworks = iconTextGenerator.createIconTextFragment("Homeworks", "/vendors/img/homeworks_icon.png");
        Group documents = iconTextGenerator.createIconTextFragment("Documents", "/vendors/img/documents_icon.png");
        Group announcements = iconTextGenerator.createIconTextFragment("Announcements", "/vendors/img/announcement_icon.png");
        ViewPurger.setColorForIconText(profile, AppColors.activeColor);
        appCluster.setCenter(LecturerProfileComponent.instance.createComponent(personId));
        profile.setOnMouseClicked(e -> {
            if (ViewPurger.isActive(profile)) return;
            appCluster.setCenter(LecturerProfileComponent.instance.createComponent(personId));
            ViewPurger.setColorForIconText(profile, AppColors.activeColor);
            ViewPurger.setColorForIconText(courses, "transparent");
            ViewPurger.setColorForIconText(homeworks, "transparent");
            ViewPurger.setColorForIconText(documents, "transparent");
            ViewPurger.setColorForIconText(announcements, "transparent");
        });
        courses.setOnMouseClicked(e -> {
            if (ViewPurger.isActive(courses)) return;
            appCluster.setCenter(LecturerClassroomComponent.instance.createComponent(personId));
            ViewPurger.setColorForIconText(profile, "transparent");
            ViewPurger.setColorForIconText(courses, AppColors.activeColor);
            ViewPurger.setColorForIconText(homeworks, "transparent");
            ViewPurger.setColorForIconText(documents, "transparent");
            ViewPurger.setColorForIconText(announcements, "transparent");
        });
        homeworks.setOnMouseClicked(e -> {
            if (ViewPurger.isActive(homeworks)) return;
            appCluster.setCenter(LecturerHomeworkComponent.instance.createComponent(personId));
            ViewPurger.setColorForIconText(profile, "transparent");
            ViewPurger.setColorForIconText(courses, "transparent");
            ViewPurger.setColorForIconText(homeworks, AppColors.activeColor);
            ViewPurger.setColorForIconText(documents, "transparent");
            ViewPurger.setColorForIconText(announcements, "transparent");
        });
        documents.setOnMouseClicked(e -> {
            if (ViewPurger.isActive(documents)) return;
            appCluster.setCenter(LecturerDocumentsComponent.instance.createComponent(personId));
            ViewPurger.setColorForIconText(profile, "transparent");
            ViewPurger.setColorForIconText(courses, "transparent");
            ViewPurger.setColorForIconText(homeworks, "transparent");
            ViewPurger.setColorForIconText(documents, AppColors.activeColor);
            ViewPurger.setColorForIconText(announcements, "transparent");
        });
        announcements.setOnMouseClicked(e -> {
            if (ViewPurger.isActive(announcements)) return;
            appCluster.setCenter(LectureAnnouncementComponent.instance.createComponent(personId));
            ViewPurger.setColorForIconText(profile, "transparent");
            ViewPurger.setColorForIconText(courses, "transparent");
            ViewPurger.setColorForIconText(homeworks, "transparent");
            ViewPurger.setColorForIconText(documents, "transparent");
            ViewPurger.setColorForIconText(announcements, AppColors.activeColor);
        });
        menuNode.getStyleClass().add("menuNode");
        menuNode.setMinWidth(AppProps.appWidth * 0.2);
        menuNode.setMinHeight(AppProps.appHeight * 0.5);
        menuNode.getChildren().addAll(nodeTitleWrapper, profile, courses, homeworks, documents, announcements);
        VBox settingsNode = new VBox();
        settingsNode.getStyleClass().add("settingsNode");
        settingsNode.setAlignment(Pos.CENTER);
        settingsNode.setMinWidth(AppProps.appWidth * 0.25);
        settingsNode.setMinHeight(AppProps.appHeight * 0.25);
        ToggleButton exitButton = new ToggleButton("Logout", new ImageView(
                new Image(getClass().getResource("/vendors/img/logout_icon.png").toExternalForm())));
        exitButton.setOnMouseClicked(e -> {
            ViewPurger.purge();
            appCluster.setCenter(new Group(new AuthenticationFragment().createLoginFragment()));
        });
        exitButton.setFont(FontLoader.gudeaBold);
        double horizontalInset = AppProps.appHeight * 0.04, verticalInset = AppProps.appHeight * 0.01;
        exitButton.setPadding(new Insets(verticalInset,  horizontalInset, verticalInset, horizontalInset));
        exitButton.setStyle("-fx-text-fill: white; -fx-background-color: " + AppColors.logoutColor);
        exitButton.getStyleClass().add("exitButton");
        settingsNode.getChildren().add(exitButton);
        Effect shadowEffect = new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.05),
                AppProps.appHeight * 0.002, AppProps.appHeight * 0.01, AppProps.appHeight * 0.002, 0);
        userNode.setEffect(shadowEffect);
        menuNode.setEffect(shadowEffect);
        settingsNode.setEffect(shadowEffect);
        appCluster.setLeft(new VBox(userNode, menuNode, settingsNode));
    }
}
