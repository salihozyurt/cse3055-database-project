package database.tools;

import java.sql.*;

public class QueryExecutor {

    private static QueryExecutor instance;
    private Connection mssqlConnection;
    private Statement queryStatement;

    private QueryExecutor(Connection mssqlConnection) {
        this.mssqlConnection = mssqlConnection;
    }

    public ResultSet executeSelect(String queryString) {
        try {
            if (queryStatement == null) {
                queryStatement = mssqlConnection.createStatement();
            }
            return queryStatement.executeQuery(queryString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void executeUpdate(String queryString) {
        if (queryStatement == null) {
            try {
                queryStatement = mssqlConnection.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            queryStatement.executeUpdate(queryString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static QueryExecutor getInstance() {
        if (instance == null) {
            instance = new QueryExecutor(Connector.getInstance().mssqlConnection);
        }
        return instance;
    }
}
