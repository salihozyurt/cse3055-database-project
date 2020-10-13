package database.query;

import database.tools.DatabaseTables;

public class PersonFullNameQuery extends Query {

    public static PersonFullNameQuery instance = new PersonFullNameQuery(DatabaseTables.FULL_NAME);

    private PersonFullNameQuery(String viewName) {
        super(viewName);
    }

    public String getFullName(String personId) {
        return getDataFrom("FullName", "person_id", personId);
    }
}
