package database.query;

public class LecturerQuery extends Query {

    public LecturerQuery(String tableName) {
        super(tableName);
    }

    public String insertForeignKey(String foreignKey) {
        return "INSERT INTO " + tableName + " VALUES (" + foreignKey + ", 'NULL', 0);";
    }
}
