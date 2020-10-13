package database.query;

import database.tools.DatabaseTables;
import database.tools.QueryExecutor;
import database.tools.ResultAnalyser;
import database.tools.StringManipulator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonQuery extends Query {

    public PersonQuery(String tableName) {
        super(tableName);
    }

    public String insert(String email, String id, String departmentId, String person_name, String surname, String password) {
        return String.format("INSERT INTO " + tableName  + " VALUES (%s, %s, '%s', '%s', '%s', '%s', '1992-02-02', 'NULL', " +
                        "'NULL', 'NULL')",
                id, departmentId, person_name, surname, email, password);
    }

    public boolean isStudent(String personId) {
        ResultSet isStudentResultSet = QueryExecutor.getInstance().executeSelect(
                "SELECT * FROM " + tableName + " PT INNER JOIN Student_T ST on " +
                        "PT.person_id = ST.student_id AND PT.person_id = " + personId
        );
        return !ResultAnalyser.isResultSetEmpty(isStudentResultSet);
    }
}
