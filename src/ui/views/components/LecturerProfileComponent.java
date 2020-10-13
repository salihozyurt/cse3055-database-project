package ui.views.components;

import database.query.*;
import database.tools.DatabaseTables;
import database.tools.QueryExecutor;
import database.tools.ResultAnalyser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ui.fragments.AppFragment;
import ui.utils.AppColors;
import ui.utils.AppProps;
import ui.utils.FontLoader;

public class LecturerProfileComponent extends ComponentUtils implements CompoundComponent {

    public static LecturerProfileComponent instance = new LecturerProfileComponent();

    private LecturerProfileComponent() {

    }

    @Override
    public Group createComponent(String personId) {
        PersonQuery personQuery = new PersonQuery(DatabaseTables.PERSON);
        LecturerQuery lecturerQuery = new LecturerQuery(DatabaseTables.LECTURER);
        AppFragment.appCluster.setCenter(new Group());
        if (ResultAnalyser.isResultNull(QueryExecutor.getInstance().executeSelect(
                personQuery.getQueryFrom("city", "person_id", personId)
        ), "city")) {
            return createInfoForm(personId);
        }
        HBox mainLayout = new HBox(AppProps.appWidth * 0.2);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMinSize(AppProps.appWidth * 0.75, AppProps.appHeight * 1.0);
        mainLayout.setStyle("-fx-background-color: " + AppColors.accentColor);
        GridPane boxContainer = new GridPane();
        boxContainer.setHgap(AppProps.appHeight * 0.06);
        boxContainer.setVgap(AppProps.appHeight * 0.06);
        boxContainer.add(createInfoBox("Name", PersonFullNameQuery.instance.getFullName(personId)),
                0, 0);
        boxContainer.add(createInfoBox("ID Number", personId), 0, 1);
        boxContainer.add(createInfoBox("Email", personQuery.getDataFrom("email", "person_id", personId)),
                0, 2);
        boxContainer.add(createInfoBox("Department", new DepartmentQuery(DatabaseTables.DEPARTMENT).getDataFrom(
                "department_code", "department_id", personQuery.getDataFrom(
                        "department_id", "person_id", personId
                )
                )),
                0, 3);
        boxContainer.add(createInfoBox("Birth Date", personQuery.getDataFrom("birth_date", "person_id", personId)),
                1, 0);
        boxContainer.add(createInfoBox("Street", personQuery.getDataFrom("street", "person_id", personId)),
                1, 1);
        boxContainer.add(createInfoBox("City", personQuery.getDataFrom("city", "person_id", personId)),
                1, 2);
        boxContainer.add(createInfoBox("State", personQuery.getDataFrom( "person_state", "person_id", personId)),
                1, 3);
        boxContainer.add(createInfoBox("Rank", lecturerQuery.getDataFrom("lecturer_rank", "lecturer_id", personId)),
                2, 0);
        boxContainer.add(createInfoBox("Total Education Year", lecturerQuery.getDataFrom("total_education_year", "lecturer_id", personId)),
                2, 1);
        mainLayout.getChildren().add(new Group(boxContainer));
        return new Group(mainLayout);
    }

    private Group createInfoForm(String personId) {
        PersonQuery personQuery = new PersonQuery(DatabaseTables.PERSON);
        LecturerQuery lecturerQuery = new LecturerQuery(DatabaseTables.LECTURER);
        VBox form = new VBox(AppProps.appHeight * 0.02);
        form.setAlignment(Pos.CENTER);
        InputPureComponent inputPureComponent = new InputPureComponent();
        form.getChildren().add(inputPureComponent.createComponent(new String[]{"Birth Date"}));
        form.getChildren().add(inputPureComponent.createComponent(new String[]{"Street"}));
        form.getChildren().add(inputPureComponent.createComponent(new String[]{"City"}));
        form.getChildren().add(inputPureComponent.createComponent(new String[]{"State"}));
        form.getChildren().add(inputPureComponent.createComponent(new String[]{"Rank"}));
        form.getChildren().add(inputPureComponent.createComponent(new String[]{"Total Education Year"}));
        Text warningText = new Text("Please fill the empty areas.");
        warningText.setOpacity(0f);
        form.getChildren().add(warningText);
        Button sendButton = createSendButton();
        sendButton.setOnAction(e -> {
            if (areInputsFilled(form)) {
                QueryExecutor.getInstance().executeUpdate(personQuery.getUpdateQuery(
                        "birth_date", getInputText(form, 0), "person_id", personId));
                QueryExecutor.getInstance().executeUpdate(personQuery.getUpdateQuery(
                        "street", getInputText(form, 1), "person_id", personId));
                QueryExecutor.getInstance().executeUpdate(personQuery.getUpdateQuery(
                        "city", getInputText(form, 2), "person_id", personId));
                QueryExecutor.getInstance().executeUpdate(personQuery.getUpdateQuery(
                        "person_state", getInputText(form, 3), "person_id", personId));
                QueryExecutor.getInstance().executeUpdate(lecturerQuery.getUpdateQuery(
                        "lecturer_rank", getInputText(form, 4), "lecturer_id", personId
                ));
                QueryExecutor.getInstance().executeUpdate(lecturerQuery.getUpdateQuery(
                        "total_education_year", getInputText(form, 5), "lecturer_id", personId
                ));
                AppFragment.appCluster.setCenter(createComponent(personId));
            }
            warningText.setOpacity(1f);
        });
        VBox buttonWrapper = new VBox(sendButton);
        buttonWrapper.setAlignment(Pos.CENTER);
        buttonWrapper.setPadding(new Insets(AppProps.appHeight * 0.04, 0, 0, 0));
        form.getChildren().add(buttonWrapper);
        return new Group(form);
    }
}
