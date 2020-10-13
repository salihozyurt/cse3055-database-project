package ui.views.components;

import database.tools.QueryExecutor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ui.fragments.AppFragment;
import ui.utils.AppColors;
import ui.utils.AppProps;
import ui.utils.FontLoader;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentAnnouncementComponent extends ComponentUtils implements CompoundComponent {

    public static StudentAnnouncementComponent instance = new StudentAnnouncementComponent();

    private StudentAnnouncementComponent() {}

    @Override
    public Group createComponent(String personId) {
        AppFragment.appCluster.setCenter(new Group());
        HBox mainLayout = new HBox(AppProps.appHeight * 0.1);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMinSize(AppProps.appWidth * 0.75, AppProps.appHeight * 1.0);
        mainLayout.setStyle("-fx-background-color: " + AppColors.accentColor);
        VBox announcementContainer = new VBox(AppProps.appHeight * 0.02);
        announcementContainer.setAlignment(Pos.CENTER);
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
