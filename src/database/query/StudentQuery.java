package database.query;

public class StudentQuery extends Query {

    public StudentQuery(String tableName) {
        super(tableName);
    }

    public String insertForeignKey(String foreignKey) {
        return "INSERT INTO " + tableName + " VALUES (" + foreignKey + ", 0, 0.0, 0, 0)";
    }
}
