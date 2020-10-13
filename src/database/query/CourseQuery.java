package database.query;

import database.tools.DatabaseTables;
import database.tools.QueryExecutor;
import database.tools.ResultAnalyser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CourseQuery extends Query {

    public static CourseQuery instance = new CourseQuery(DatabaseTables.COURSE);

    private CourseQuery(String tableName) {
        super(tableName);
    }

    public String[] getAvailableLecturerCourses(String personId) {
        ArrayList<String> notAvailableCourses = new ArrayList<>();
        ArrayList<String> courseList = new ArrayList<>();
        ResultSet courseSet = QueryExecutor.getInstance().executeSelect(
                "EXEC detect_department_courses @PersonId = " + personId
        );
        while (true) {
            try {
                if (!courseSet.next()) break;
                courseList.add(courseSet.getString("course_id"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ResultSet takenCoursesSet = QueryExecutor.getInstance().executeSelect(
                "EXEC get_courses_lecturers_taken @PersonId = " + personId);
        while (true) {
            try {
                if (!takenCoursesSet.next()) break;
                notAvailableCourses.add(takenCoursesSet.getString("course_id"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        courseList.removeAll(notAvailableCourses);
        return courseList.toArray(new String[0]);
    }

    public String[] getTakenCourses(String personId) {
        ArrayList<String> takenCourses = new ArrayList<>();
        ResultSet takenCourseSet = QueryExecutor.getInstance().executeSelect(
                "EXEC detect_taken_courses @PersonId = " + personId
        );
        while (true) {
            try {
                if (!takenCourseSet.next()) break;
                takenCourses.add(takenCourseSet.getString("course_id"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return takenCourses.toArray(new String[0]);
    }

    public String[] getAvailableStudentCourses(String personId) {
        ArrayList<String> availableCourses = new ArrayList<>();
        ArrayList<String> coursesTakenByLecturers = new ArrayList<>();
        ResultSet availableSet = QueryExecutor.getInstance().executeSelect(
                "EXEC detect_available_courses @PersonId = " + personId
        );
        while (true) {
            try {
                if (!availableSet.next()) break;
                availableCourses.add(availableSet.getString("course_id"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ResultSet lecturerCoursesSet = QueryExecutor.getInstance().executeSelect(
                "EXEC get_courses_lecturers_taken @PersonId = " + personId
        );
        while (true) {
            try {
                if (!lecturerCoursesSet.next()) break;
                coursesTakenByLecturers.add(lecturerCoursesSet.getString("course_id"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return availableCourses.stream()
                .filter(coursesTakenByLecturers::contains)
                .toArray(String[]::new);
    }

    public String getLecturer(String courseId) {
        ResultSet lecturerSet = QueryExecutor.getInstance().executeSelect(
                "EXEC get_course_lecturer @CourseId = " + courseId
        );
        try {
            lecturerSet.next();
            return PersonFullNameQuery.instance.getFullName(lecturerSet.getString("lecturer_id"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
