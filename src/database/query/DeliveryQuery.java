package database.query;

import database.tools.DatabaseTables;
import database.tools.QueryExecutor;

public class DeliveryQuery extends Query {

    public static DeliveryQuery instance = new DeliveryQuery(DatabaseTables.DELIVERY);

    private DeliveryQuery(String tableName) {
        super(tableName);
    }

    public void sendDelivery(String personId, String nodeId, String documentName) {
        QueryExecutor.getInstance().executeUpdate(
                "INSERT INTO " + tableName +
                        " VALUES(" + personId + ", " + nodeId + ", '" + documentName + "')"
        );
    }
}
