package database.query;

import database.tools.DatabaseTables;
import database.tools.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NodeQuery extends Query {

    public static NodeQuery instance = new NodeQuery(DatabaseTables.NODE);

    private NodeQuery(String tableName) {
        super(tableName);
    }

    public void addHomework(String courseId, String title, String nodeDescription, String endDate) {
        QueryExecutor.getInstance().executeUpdate(
                "INSERT INTO " + tableName + " VALUES (" + courseId + ", GETDATE(), '" + title + "', " +
                        "'" + nodeDescription + "')"
        );
        ResultSet currentNodeSet = QueryExecutor.getInstance().executeSelect(
                "EXEC get_last_node_record"
        );
        try {
            currentNodeSet.next();
            String assignment_id = currentNodeSet.getString("node_id");
            QueryExecutor.getInstance().executeUpdate(
                    "INSERT INTO " + HomeworkQuery.instance.tableName + " VALUES " +
                            "(" + assignment_id + ", '" + endDate + "')"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDocument(String courseId, String title, String nodeDescription, String documentName) {
        QueryExecutor.getInstance().executeUpdate(
                "INSERT INTO " + tableName + " VALUES (" + courseId + ", GETDATE(), '" + title + "', " +
                        "'" + nodeDescription + "')"
        );
        ResultSet currentNodeSet = QueryExecutor.getInstance().executeSelect(
                "EXEC get_last_node_record"
        );
        try {
            currentNodeSet.next();
            String lecture_note_id = currentNodeSet.getString("node_id");
            QueryExecutor.getInstance().executeUpdate(
                    "INSERT INTO " + DocumentQuery.instance.tableName + " VALUES " +
                            "(" + lecture_note_id + ", '" + documentName + "')"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAnnouncement(String courseId, String title, String nodeDescription) {
        QueryExecutor.getInstance().executeUpdate(
                "INSERT INTO " + tableName + " VALUES (" + courseId + ", GETDATE(), '" + title + "', " +
                        "'" + nodeDescription + "')"
        );
        ResultSet currentNodeSet = QueryExecutor.getInstance().executeSelect(
                "EXEC get_last_node_record"
        );
        try {
            currentNodeSet.next();
            String announcement_id = currentNodeSet.getString("node_id");
            QueryExecutor.getInstance().executeUpdate(
                    "INSERT INTO " + AnnouncementQuery.instance.tableName + " VALUES " +
                            "(" + announcement_id + ")"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
