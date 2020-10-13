package database.tools;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultAnalyser {

    public static boolean isResultSetEmpty(ResultSet resultSet) {
        try {
            if (!resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isResultNull(ResultSet resultSet, String testAttribute) {
        try {
            resultSet.next();
            if (resultSet.getString(testAttribute).equals("NULL")) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
