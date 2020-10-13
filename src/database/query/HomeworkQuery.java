package database.query;

import database.tools.DatabaseTables;

public class HomeworkQuery extends Query {

    public static HomeworkQuery instance = new HomeworkQuery(DatabaseTables.HOMEWORK);

    private HomeworkQuery(String tableName) {
        super(tableName);
    }
}
