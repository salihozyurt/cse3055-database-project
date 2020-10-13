package database.query;

import database.tools.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DepartmentQuery extends Query {

    public DepartmentQuery(String tableName) {
        super(tableName);
    }

    public String[] getDepartments() {
        ResultSet departmentSet = QueryExecutor.getInstance().executeSelect(selectAll("department_name"));
        ArrayList<String> departments = new ArrayList<>();
        while (true) {
            try {
                if (!departmentSet.next()) break;
                departments.add(departmentSet.getString("department_name"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return departments.toArray(new String[0]);
    }

}
