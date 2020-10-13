package database.query;

import database.tools.DatabaseTables;
import database.tools.QueryExecutor;

public class DocumentQuery extends Query {

    public static DocumentQuery instance = new DocumentQuery(DatabaseTables.DOCUMENT);

    private DocumentQuery(String tableName) {
        super(tableName);
    }
}
