package ui.utils;

import database.query.PersonQuery;
import database.tools.DatabaseTables;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class UserIconCreator {

    private final static Color studentColor = Color.web("#b2dffb");
    private final static Color studentTextColor = Color.web("#10316b");
    private final static Color lecturerColor = Color.web("#6decb9");
    private final static Color lecturerTextColor = Color.web("#0c9463");

    public static Group createUserIcon(String personId, String username) {
        PersonQuery personQuery = new PersonQuery(DatabaseTables.PERSON);
        boolean isStudent = personQuery.isStudent(personId);
        Circle iconBackground = new Circle(AppProps.appHeight * 0.04, isStudent ? studentColor : lecturerColor);
        Text iconLetter = new Text(String.valueOf(Character.toUpperCase(username.charAt(0))));
        iconLetter.setFill(isStudent ? studentTextColor : lecturerTextColor);
        iconLetter.setFont(FontLoader.gudeaBold);
        iconLetter.setStyle("-fx-font-size: " + AppProps.appHeight * 0.04 + "px;");
        Group iconComponent = new Group(new StackPane(iconBackground, iconLetter));
        iconComponent.setOnMouseClicked(e -> {
            System.out.println("User Profile");
        });
        return iconComponent;
    }
}
