package ui.fragments;

import database.query.DepartmentQuery;
import database.query.LecturerQuery;
import database.query.PersonQuery;
import database.query.StudentQuery;
import database.tools.DatabaseTables;
import database.tools.QueryExecutor;
import database.tools.ResultAnalyser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.commons.validator.routines.EmailValidator;
import ui.utils.AppColors;
import ui.utils.FontLoader;
import ui.utils.AppProps;
import ui.views.LecturerView;
import ui.views.StudentView;
import ui.views.ViewPurger;

import java.sql.ResultSet;

public class AuthenticationFragment {

    private enum State {
        STUDENT(0), LECTURER(1);

        private int value;

        State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private double verticalContainerRadius = AppProps.appWidth * 0.01;
    private Font pacifico = Font.loadFont(
            getClass().getResource("/vendors/fonts/Pacifico-Regular.ttf").toExternalForm(), 64);
    private PersonQuery personQuery = new PersonQuery(DatabaseTables.PERSON);
    private StudentQuery newStudentQuery;
    private LecturerQuery newLecturerQuery;
    private StudentQuery staticStudentQuery = new StudentQuery(DatabaseTables.STUDENT);
    private LecturerQuery staticLecturerQuery = new LecturerQuery(DatabaseTables.LECTURER);
    private State currentState;

    private boolean checkUniquenessForSignUpInputs(Group[] inputFragments) {
        ResultSet emailResultSet =
                QueryExecutor.getInstance().executeSelect(personQuery.selectWith(
                        "email", "email", getNormalInputText(inputFragments[0])
                ));
        if (!ResultAnalyser.isResultSetEmpty(emailResultSet)) {
            getWarningLabel(inputFragments[5]).setText("Enter a unique email address.");
            getWarningLabel(inputFragments[5]).setTextFill(Color.web(AppColors.primaryColor));
            return false;
        }
        ResultSet idResultSet =
                QueryExecutor.getInstance().executeSelect(personQuery.selectWith(
                        "person_id", "person_id", getNormalInputText(inputFragments[1])
                ));
        if (!ResultAnalyser.isResultSetEmpty(idResultSet)) {
            getWarningLabel(inputFragments[5]).setText("Enter a unique ID.");
            getWarningLabel(inputFragments[5]).setTextFill(Color.web(AppColors.primaryColor));
            return false;
        }
        return true;
    }

    private boolean checkEmptySignUpInputs(Group[] inputFragments) {
        return getNormalInputText(inputFragments[0]).isEmpty() || getNormalInputText(inputFragments[1]).isEmpty() ||
                getNormalInputText(inputFragments[2]).isEmpty() || getNormalInputText(inputFragments[3]).isEmpty() ||
                getPasswordInputText(inputFragments[4]).isEmpty() || getPasswordInputText(inputFragments[5]).isEmpty();
    }

    public VBox createSignUpFragment() {
        newStudentQuery = staticStudentQuery;
        newLecturerQuery = null;
        currentState = State.STUDENT;
        AppFragment.appCluster.setStyle("-fx-background-color: " + AppColors.primaryColor + ";");
        VBox verticalContainer = new VBox(AppProps.appWidth * 0.02);
        double horizontalPaddingOffset = AppProps.appWidth * 0.04, verticalPaddingOffset = AppProps.appWidth * 0.01;
        verticalContainer.setPadding(new Insets(verticalPaddingOffset, horizontalPaddingOffset,
                verticalPaddingOffset * 2, horizontalPaddingOffset));
        verticalContainer.setAlignment(Pos.CENTER);
        Label formTitle = new Label("Sign Up");
        formTitle.setStyle("-fx-text-fill: " + AppColors.primaryColor + ";");
        formTitle.setPadding(new Insets(0, 0 , verticalPaddingOffset, 0));
        formTitle.setFont(pacifico);
        BorderPane titleBar = new BorderPane(formTitle);
        VBox userChangeIconContainer = new VBox();
        userChangeIconContainer.setAlignment(Pos.CENTER);
        Circle changeUserCircle = new Circle(12, new ImagePattern(
                new Image(getClass().getResource("/vendors/img/change_user_icon.png").toExternalForm())
        ));
        changeUserCircle.getStyleClass().add("changeUserCircle");
        userChangeIconContainer.getChildren().add(changeUserCircle);
        titleBar.setLeft(userChangeIconContainer);
        verticalContainer.getChildren().add(titleBar);
        Group[] inputFragments = new Group[6];
        changeUserCircle.setOnMouseClicked(e -> {
            if (currentState == State.STUDENT) {
                newStudentQuery = null;
                newLecturerQuery = new LecturerQuery(DatabaseTables.LECTURER);
                currentState = State.LECTURER;
                getIdInputInfoText(inputFragments[1]).setText("Lecturer ID");
            } else {
                newStudentQuery = new StudentQuery(DatabaseTables.STUDENT);
                newLecturerQuery = null;
                currentState = State.STUDENT;
                getIdInputInfoText(inputFragments[1]).setText("Student ID");
            }
        });
        String[] prompts = new String[]{"Email", "Student ID", "Name", "Surname", "Password", "Password Again"};
        for (int i = 0; i < inputFragments.length - 2; ++i) {
            inputFragments[i] = InputFragment.createInputFragment(prompts[i], "signUpInput", false);
        }
        inputFragments[4] = InputFragment.createPasswordInputFragment(prompts[4], "signUpInput", false);
        inputFragments[5] = InputFragment.createPasswordInputFragment(prompts[5], "signUpInput", true);
        for (Group input: inputFragments) {
            verticalContainer.getChildren().add(input);
        }
        verticalContainer.setStyle("-fx-background-color: white; -fx-background-radius: " + verticalContainerRadius +
                ";-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.6), 16, -8, 0, 8)");
        Button signUpButton = new Button("REGISTER");
        signUpButton.setFont(FontLoader.gudea);
        signUpButton.setMinSize(AppProps.appWidth * 0.15, AppProps.appHeight * 0.04);
        signUpButton.getStyleClass().add("signUpButton");
        signUpButton.setOnAction(e -> {
            getWarningLabel(inputFragments[5]).setTextFill(Color.web(AppColors.whiteColor));
            if (checkEmptySignUpInputs(inputFragments)) {
                getWarningLabel(inputFragments[5]).setText("Some inputs are empty. Please fill them.");
                getWarningLabel(inputFragments[5]).setTextFill(Color.web(AppColors.primaryColor));
                return;
            }
            EmailValidator emailValidator = EmailValidator.getInstance(true);
            boolean isValid = emailValidator.isValid(getNormalInputText(inputFragments[0]));
            if (isValid) {
                if (checkUniquenessForSignUpInputs(inputFragments)) {
                    if (getPasswordInputText(inputFragments[4]).equals(getPasswordInputText(inputFragments[5]))) {
                        QueryExecutor.getInstance().executeUpdate(personQuery.insert(
                           getNormalInputText(inputFragments[0]), getNormalInputText(inputFragments[1]),
                                SelectorFragment.getDepartmentValue(),
                           getNormalInputText(inputFragments[2]), getNormalInputText(inputFragments[3]),
                                getPasswordInputText(inputFragments[4])));
                        QueryExecutor.getInstance().executeUpdate(
                                newStudentQuery != null ? newStudentQuery.insertForeignKey(
                                        getNormalInputText(inputFragments[1])
                                ) : newLecturerQuery.insertForeignKey(getNormalInputText(inputFragments[1]))
                        );
                        AppFragment.appCluster.setCenter(new Group(createLoginFragment()));
                    } else {
                        getWarningLabel(inputFragments[5]).setText("Passwords don't match.");
                        getWarningLabel(inputFragments[5]).setTextFill(Color.web(AppColors.primaryColor));
                    }
                }
            } else {
                getWarningLabel(inputFragments[5]).setText("Email is not valid.");
                getWarningLabel(inputFragments[5]).setTextFill(Color.web(AppColors.primaryColor));
            }
        });
        StackPane buttonWrapper = new StackPane(signUpButton);
        buttonWrapper.setPadding(new Insets(verticalPaddingOffset * 2, 0, verticalPaddingOffset * 2, 0));
        Label switchLabel = new Label("Are you already registered?");
        switchLabel.setTextFill(Color.web(AppColors.primaryColor));
        switchLabel.getStyleClass().add("switchLabel");
        switchLabel.setOnMouseClicked(e -> {
            ViewPurger.purge();
            AppFragment.appCluster.setCenter(new Group(createLoginFragment()));
        });
        verticalContainer.getChildren().addAll(buttonWrapper, switchLabel);
        return verticalContainer;
    }

    private String getNormalInputText(Group container) {
        return ((TextField) ((VBox) container.getChildren().get(0)).getChildren().get(1)).getText();
    }

    private String getPasswordInputText(Group container) {
        return ((PasswordField) ((VBox) container.getChildren().get(0)).getChildren().get(1)).getText();
    }

    private Label getWarningLabel(Group container) {
        return (Label) ((VBox) container.getChildren().get(0)).getChildren().get(2);
    }

    private Text getIdInputInfoText(Group container) {
        return ((Text) ((VBox) container.getChildren().get(0)).getChildren().get(0));
    }

    private boolean checkEmptyLoginInputs(Group[] inputFragments) {
        return getNormalInputText(inputFragments[0]).isEmpty() || getPasswordInputText(inputFragments[1]).isEmpty();
    }

    private boolean checkMatchForLoginInputs(Group[] inputFragments) {
        ResultSet emailResultSet =
                QueryExecutor.getInstance().executeSelect(personQuery.selectWith(
                        "email", "email", getNormalInputText(inputFragments[0])
                ));

        if (ResultAnalyser.isResultSetEmpty(emailResultSet)) {
            getWarningLabel(inputFragments[0]).setText("Given email is not recorded in the system.");
            getWarningLabel(inputFragments[0]).setTextFill(Color.web(AppColors.secondaryColor));
            return false;
        }
        ResultSet passwordResultSet =
                QueryExecutor.getInstance().executeSelect(personQuery.selectWith(
                        "person_password", "person_password", getPasswordInputText(inputFragments[1])
                ));
        if (ResultAnalyser.isResultSetEmpty(passwordResultSet)) {
            getWarningLabel(inputFragments[1]).setText("Password is not matched with email.");
            getWarningLabel(inputFragments[1]).setTextFill(Color.web(AppColors.secondaryColor));
            return false;
        }
        return true;
    }

    public VBox createLoginFragment() {
        AppFragment.appCluster.setStyle("-fx-background-color: " + AppColors.secondaryColor + ";");
        VBox verticalContainer = new VBox(AppProps.appWidth * 0.02);
        double horizontalPaddingOffset = AppProps.appWidth * 0.04, verticalPaddingOffset = AppProps.appWidth * 0.01;
        verticalContainer.setPadding(new Insets(verticalPaddingOffset, horizontalPaddingOffset,
                verticalPaddingOffset * 2, horizontalPaddingOffset));
        verticalContainer.setAlignment(Pos.CENTER);
        Label formTitle = new Label("Login");
        formTitle.setStyle("-fx-text-fill: " + AppColors.secondaryColor + ";");
        formTitle.setPadding(new Insets(0, 0 , verticalPaddingOffset, 0));
        formTitle.setFont(pacifico);
        verticalContainer.getChildren().add(formTitle);
        Group[] inputFragments = new Group[5];
        String[] prompts = new String[]{"Email", "Password"};
        inputFragments[0] = InputFragment.createInputFragment(prompts[0], "loginInput", true);
        inputFragments[1] = InputFragment.createPasswordInputFragment(prompts[1], "loginInput", true);
        verticalContainer.getChildren().add(inputFragments[0]);
        verticalContainer.getChildren().add(inputFragments[1]);
        verticalContainer.setStyle("-fx-background-color: white; -fx-background-radius: " + verticalContainerRadius +
                ";-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.6), 16, -8, 0, 8)");
        Button loginButton = new Button("LOGIN");
        loginButton.setFont(FontLoader.gudea);
        loginButton.setMinSize(AppProps.appWidth * 0.15, AppProps.appHeight * 0.04);
        loginButton.getStyleClass().add("loginButton");
        loginButton.setOnAction(e -> {
            getWarningLabel(inputFragments[0]).setTextFill(Color.web(AppColors.whiteColor));
            getWarningLabel(inputFragments[1]).setTextFill(Color.web(AppColors.whiteColor));
            if (checkEmptyLoginInputs(inputFragments)) {
                getWarningLabel(inputFragments[1]).setTextFill(Color.web(AppColors.secondaryColor));
                getWarningLabel(inputFragments[1]).setText("Some inputs are empty. Please fill them.");
                return;
            }
            if (checkMatchForLoginInputs(inputFragments)) {
                AppFragment.appCluster.setCenter(new Group());
                AppFragment.appCluster.setStyle("-fx-background-color: " + AppColors.whiteColor + ";");
                if (personQuery.isStudent(
                        personQuery.getDataFrom("person_id", "email", getNormalInputText(inputFragments[0]))
                )) {
                    StudentView.instance.injectContentIntoCluster(personQuery.getDataFrom(
                            "person_id", "email", getNormalInputText(inputFragments[0])
                    ));
                } else {
                    LecturerView.instance.injectContentIntoCluster(personQuery.getDataFrom(
                            "person_id", "email", getNormalInputText(inputFragments[0])
                    ));
                }
            }
        });
        StackPane buttonWrapper = new StackPane(loginButton);
        buttonWrapper.setPadding(new Insets(verticalPaddingOffset * 2, 0, verticalPaddingOffset * 2, 0));
        Label switchLabel = new Label("Have you not signed up yet?");
        switchLabel.setTextFill(Color.web(AppColors.secondaryColor));
        switchLabel.getStyleClass().add("switchLabel");
        switchLabel.setOnMouseClicked(e -> {
            ViewPurger.purge();
            HBox loginMenuWrapper = new HBox(AppProps.appHeight * 0.02);
            loginMenuWrapper.setAlignment(Pos.CENTER);
            Group departmentSelector = SelectorFragment.createSelector(
                    new DepartmentQuery(DatabaseTables.DEPARTMENT).getDepartments());
            loginMenuWrapper.getChildren().addAll(new Group(createSignUpFragment()), departmentSelector);
            AppFragment.appCluster.setCenter(loginMenuWrapper);
        });
        verticalContainer.getChildren().addAll(buttonWrapper, switchLabel);
        return verticalContainer;
    }
}
