package database.query;

import database.tools.QueryExecutor;
import database.tools.StringManipulator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Query {

    protected String tableName;

    public Query(String tableName) {
        this.tableName = tableName;
    }

    public  String selectAll(String key) {
        return "SELECT " + key + " FROM " + tableName;
    }

    public String selectWith(String dataKey, String sourceKey, String sourceValue) {
        if (StringManipulator.isNotNumeric(sourceValue)) sourceValue = "'" + sourceValue + "'";
        return "SELECT " + dataKey + " FROM " + tableName + " WHERE " + sourceKey + " = " + sourceValue;
    }

    public String getDataFrom(String dataKey, String sourceKey, String sourceValue) {
        ResultSet infoResultSet = QueryExecutor.getInstance().executeSelect(
                selectWith(dataKey, sourceKey, sourceValue)
        );
        try {
            infoResultSet.next();
            return infoResultSet.getString(dataKey);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getQueryFrom(String dataKey, String sourceKey, String sourceValue) {
        if (StringManipulator.isNotNumeric(sourceValue)) sourceValue = "'" + sourceValue + "'";
        return "SELECT " + dataKey + " FROM " + tableName + " WHERE " + sourceKey + " = " + sourceValue;
    }

    public String getUpdateQuery(String dataKey, String dataValue, String sourceKey, String sourceValue) {
        if (StringManipulator.isNotNumeric(dataValue)) dataValue = "'" + dataValue + "'";
        if (StringManipulator.isNotNumeric(sourceValue)) sourceValue = "'" + sourceValue + "'";
        return "UPDATE " + tableName + " SET " + dataKey + " = " + dataValue + " WHERE " +
                sourceKey + " = " + sourceValue;
    }
}
