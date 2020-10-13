package database.query;

import database.tools.DatabaseTables;
import database.tools.QueryExecutor;

public class CourseRecordQuery extends Query {

    public static CourseRecordQuery instance = new CourseRecordQuery(DatabaseTables.COURSE_RECORD);

    private CourseRecordQuery(String tableName) {
        super(tableName);
    }

    public void addCourseRecord(String personId, String courseId) {
        QueryExecutor.getInstance().executeUpdate(
                "INSERT INTO " + tableName + " VALUES (" + personId + ", " + courseId + ")"
        );
    }

    public void removeCourseRecord(String personId, String courseId) {
        QueryExecutor.getInstance().executeUpdate(
                "DELETE FROM " + tableName + " WHERE person_id = " + personId + " AND course_id = " + courseId
        );
    }
}
