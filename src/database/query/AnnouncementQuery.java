package database.query;

import database.tools.DatabaseTables;

public class AnnouncementQuery extends Query {

    public static AnnouncementQuery instance = new AnnouncementQuery(DatabaseTables.ANNOUNCEMENT);

    private AnnouncementQuery(String tableName) {
        super(tableName);
    }
}
