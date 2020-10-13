CREATE DATABASE MARC

USE MARC

CREATE TABLE Department_T (
	department_id INT NOT NULL,
	department_code VARCHAR(20) NOT NULL,
	department_name VARCHAR(50) NOT NULL,
	CONSTRAINT PK_Department PRIMARY KEY (department_id)
);

CREATE TABLE Person_T (
	person_id INT,
	department_id INT NOT NULL,
	person_name VARCHAR(30) NOT NULL,
	surname VARCHAR(30) NOT NULL,
	email VARCHAR(50) NOT NULL,
	person_password VARCHAR(50) NOT NULL,
	birth_date DATE NOT NULL,
	street VARCHAR(50) NOT NULL,
	city VARCHAR(50) NOT NULL,
	person_state VARCHAR(50) NOT NULL,
	CONSTRAINT PK_Person PRIMARY KEY (person_id),
	CONSTRAINT FK_Person_Department FOREIGN KEY (department_id) REFERENCES Department_T (department_id)
);

CREATE TABLE Student_T (
	student_id INT,
	semester INT NOT NULL,
	GPA REAL NOT NULL,
	total_credits INT NOT NULL,
	isHonor BIT NOT NULL,
	CONSTRAINT PK_Student PRIMARY KEY (student_id),
	CONSTRAINT FK_Student_Person FOREIGN KEY (student_id) REFERENCES Person_T (person_id)
);

CREATE TABLE Lecturer_T (
	lecturer_id INT,
	lecturer_rank VARCHAR(20) NOT NULL,
	total_education_year INT NOT NULL,
	CONSTRAINT PK_Lecturer PRIMARY KEY (lecturer_id),
	CONSTRAINT FK_Lecturer_Person FOREIGN KEY (lecturer_id) REFERENCES Person_T (person_id)
);

CREATE TABLE Course_T (
	course_id INT,
	department_id INT NOT NULL,
	course_code VARCHAR(20) NOT NULL,
	course_name VARCHAR(50) NOT NULL,
	CONSTRAINT PK_Course PRIMARY KEY (course_id),
	CONSTRAINT FK_Course_Department FOREIGN KEY (department_id) REFERENCES Department_T (department_id)
);

CREATE TABLE CourseRecord_T (
    record_id INT PRIMARY KEY IDENTITY,
    person_id INT FOREIGN KEY REFERENCES Person_T (person_id),
    course_id INt FOREIGN KEY REFERENCES Course_T (course_id)
);

CREATE TABLE Classroom_T (
	course_id INT NOT NULL,
	person_id INT NOT NULL,
	end_date DATE NOT NULL,
	CONSTRAINT PK_Classroom PRIMARY KEY CLUSTERED (course_id, person_id),
	CONSTRAINT FK_Classroom_Person FOREIGN KEY (person_id) REFERENCES Person_T (person_id),
	CONSTRAINT FK_Classroom_Course FOREIGN KEY (course_id) REFERENCES Course_T (course_id)
);

CREATE TABLE Node_T (
	node_id INT IDENTITY (1,1),
	course_id INT NOT NULL,
	creation_date DATE NOT NULL,
	title VARCHAR(100) NOT NULL,
	node_description VARCHAR(255) NOT NULL,
	CONSTRAINT PK_Node PRIMARY KEY (node_id),
	CONSTRAINT FK_Node_Course FOREIGN KEY (course_id) REFERENCES Course_T (course_id)
);

CREATE TABLE Comment_T (
	node_id INT NOT NULL,
	person_id INT NOT NULL,
	comment_text VARCHAR(255) NOT NULL,
	reference_user INT,
	CONSTRAINT PK_Comment PRIMARY KEY CLUSTERED (node_id, person_id),
	CONSTRAINT FK_Comment_Person FOREIGN KEY (person_id) REFERENCES Person_T (person_id),
	CONSTRAINT FK_Comment_Node FOREIGN KEY (node_id) REFERENCES Node_T (node_id)
);

CREATE TABLE Announcement_T (
	announcement_id INT,
	CONSTRAINT PK_Announcement PRIMARY KEY (announcement_id),
	CONSTRAINT FK_Announcement_Node FOREIGN KEY (announcement_id) REFERENCES Node_T (node_id)
);

CREATE TABLE LectureNote_T (
	lecture_note_id INT,
	document VARCHAR(100) NOT NULL,
	CONSTRAINT PK_LectureNote PRIMARY KEY (lecture_note_id),
	CONSTRAINT FK_LectureNote_Node FOREIGN KEY (lecture_note_id) REFERENCES Node_T (node_id)
);

CREATE TABLE Assignment_T (
	assignment_id INT,
	end_date DATE NOT NULL,
	CONSTRAINT PK_Assignment PRIMARY KEY (assignment_id),
	CONSTRAINT FK_Assignment_Node FOREIGN KEY (assignment_id) REFERENCES Node_T (node_id)
);

CREATE TABLE Delivery_T (
	student_id INT NOT NULL,
	assignment_id INT NOT NULL,
	document VARCHAR(100) NOT NULL,
	CONSTRAINT PK_Delivery PRIMARY KEY CLUSTERED (student_id, assignment_id),
	CONSTRAINT FK_Delivery_Student FOREIGN KEY (student_id) REFERENCES Student_T (student_id),
	CONSTRAINT FK_Delivery_Assignment FOREIGN KEY (assignment_id) REFERENCES Assignment_T (assignment_id)
);

CREATE TABLE Person_Login_Info (
    person_id INT,
    email VARCHAR(50),
    person_password VARCHAR(50),
);

-- Views

CREATE VIEW Person_Full_Name_T AS
SELECT person_id, CONCAT(person_name, ' ', LEFT(surname,1)+LOWER(SUBSTRING(surname,2,LEN(surname)))) AS FullName FROM Person_T;

-- Triggers

CREATE TRIGGER Person_Login_Info_Trigger
ON Person_T
AFTER INSERT
AS
BEGIN
    INSERT INTO Person_Login_Info SELECT person_id,email,person_password FROM Person_T WHERE person_state = 'NULL'
END;

-- Stored Procedures

CREATE PROCEDURE detect_available_courses
    @PersonId INT
AS
    SELECT DISTINCT c.course_id FROM Course_T c, Person_T p, Department_T d, CourseRecord_T cr WHERE p.person_id = @PersonId AND
    p.department_id = d.department_id AND d.department_id = c.department_id AND c.course_name NOT IN
        (SELECT course_name FROM Course_T c, CourseRecord_T cr, Person_T p WHERE c.course_id = cr.course_id
        AND cr.person_id = p.person_id AND p.person_id = @PersonId)
go

CREATE PROCEDURE detect_department_courses
    @PersonId INT
AS
    SELECT course_id FROM Person_T p, Department_T d, Course_T c WHERE p.department_id = d.department_id
    AND d.department_id = c.department_id AND p.person_id = @PersonId
go

CREATE PROCEDURE detect_student_homeworks
    @PersonId INT
AS
    SELECT * FROM Person_T p, CourseRecord_T cr, Course_T c, Node_T n, Assignment_T a
    WHERE p.person_id = cr.person_id AND cr.course_id = c.course_id
    AND n.course_id = c.course_id AND n.node_id = a.assignment_id AND p.person_id IN (
        SELECT DISTINCT l.lecturer_id FROM Course_T c, Lecturer_T l, Person_T p, CourseRecord_T cr
        WHERE c.course_id = cr.course_id AND cr.person_id = l.lecturer_id AND c.course_id  IN (
            SELECT course_id FROM Person_T p, Department_T d, Course_T c WHERE p.department_id = d.department_id
            AND d.department_id = c.department_id AND p.person_id = @PersonId
            )
        )
go

CREATE PROCEDURE detect_taken_courses
    @PersonId INT
AS
    SELECT * FROM Course_T c, CourseRecord_T cr, Person_T p WHERE c.course_id = cr.course_id
    AND cr.person_id = p.person_id AND p.person_id = @PersonId
go

CREATE PROCEDURE get_announcements
    @PersonId INT
AS
    SELECT * FROM Person_T p, CourseRecord_T cr, Course_T c, Node_T n, Announcement_T a WHERE p.person_id = @PersonId
    AND p.person_id = cr.person_id AND c.course_id = cr.course_id AND n.course_id = c.course_id
    AND announcement_id = n.node_id
go

CREATE PROCEDURE get_course_lecturer
    @CourseId INT
AS
    SELECT DISTINCT l.lecturer_id FROM Course_T c, Lecturer_T l, Person_T p, CourseRecord_T cr
    WHERE c.course_id = cr.course_id AND cr.person_id = l.lecturer_id AND c.course_id = @CourseId
go

CREATE PROCEDURE get_courses_lecturers_taken
    @PersonId INT
AS
    SELECT cr.person_id, c.course_id, c.course_name, c.course_code FROM Lecturer_T l, Course_T c, CourseRecord_T cr, Person_T p, Department_T d
    WHERE p.person_id = @PersonId AND l.lecturer_id = cr.person_id AND p.department_id = d.department_id AND c.department_id = d.department_id
    AND c.course_id = cr.course_id
go

CREATE PROCEDURE get_current_homeworks
    @LecturerId INT
AS
    SELECT * FROM Person_T p, CourseRecord_T cr, Course_T c, Node_T n, Assignment_T a
    WHERE p.person_id = @LecturerId AND p.person_id = cr.person_id AND cr.course_id = c.course_id
    AND n.course_id = c.course_id AND n.node_id = a.assignment_id
go

CREATE PROCEDURE get_delivered_homeworks
    @AssignmentId INT
AS
    SELECT student_id FROM Assignment_T a, Delivery_T d WHERE  a.assignment_id = @AssignmentId
    AND a.assignment_id = d.assignment_id
go

CREATE PROCEDURE get_document
    @StudentId INT,
    @AssignmentId INT
AS
  SELECT document FROM Delivery_T d, Person_T p, Assignment_T a WHERE  p.person_id = d.student_id
    AND a.assignment_id = d.assignment_id AND person_id = @StudentId AND a.assignment_id = @AssignmentId
go

CREATE PROCEDURE get_homeworks_done
    @PersonId INT
AS
    SELECT assignment_id FROM Person_T p, Delivery_T d WHERE p.person_id = d.student_id AND person_id = @PersonId
go

CREATE PROCEDURE get_last_node_record
AS
    SELECT TOP 1 * FROM Node_T ORDER BY node_id DESC
go

CREATE PROCEDURE get_lecture_notes
    @PersonId INT
AS
    SELECT * FROM Person_T p, CourseRecord_T cr, Course_T c, Node_T n, LectureNote_T l WHERE p.person_id = @PersonId
    AND p.person_id = cr.person_id AND c.course_id = cr.course_id AND n.course_id = c.course_id
    AND lecture_note_id = n.node_id
go

--Insert Department
INSERT INTO Department_T VALUES (1,'CSE','Computer Science Engineering')
INSERT INTO Department_T VALUES (2,'ME','Mechanical Engineering')
INSERT INTO Department_T VALUES (3,'IE','Industrial Engineering')
INSERT INTO Department_T VALUES (4,'EE','Electrical and Electronics Engineering')
INSERT INTO Department_T VALUES (5,'BIOE','Bioengineering Engineering')
INSERT INTO Department_T VALUES (6,'MSE','Metallurgical and Materials Science Engineering')
INSERT INTO Department_T VALUES (7,'KMM','Chemical  Engineering')
INSERT INTO Department_T VALUES (8,'ENVE','Environmental Engineering')
INSERT INTO Department_T VALUES (9,'GRF','Graphic')
INSERT INTO Department_T VALUES (10,'IA','Interior Architecture')

--Insert Course
INSERT INTO Course_T VALUES (2025,1,'CSE2025','Data Structures')
INSERT INTO Course_T VALUES (3055,1,'CSE3055','Database Systems')
INSERT INTO Course_T VALUES (2138,1,'CSE2138','Systems Programming')
INSERT INTO Course_T VALUES (4117,1,'CSE4117','Microprocessors')
INSERT INTO Course_T VALUES (4074,1,'CSE4074','Computer Networks')

INSERT INTO Course_T VALUES (3021,2,'ME3021','System Dynamic')
INSERT INTO Course_T VALUES (3015,2,'ME3015','Mechanisms')
INSERT INTO Course_T VALUES (4041,2,'ME4041','CAD Technology')
INSERT INTO Course_T VALUES (2001,2,'ME2001','Mecanical Engineering Fundamentals')
INSERT INTO Course_T VALUES (2063,2,'ME2063','Thermo')

INSERT INTO Course_T VALUES (3064,3,'IE3064','Simulation')
INSERT INTO Course_T VALUES (2151,3,'IE2151','Probability')
INSERT INTO Course_T VALUES (2152,3,'IE2152','Statistics')
INSERT INTO Course_T VALUES (3045,3,'IE3045','Int to MIS')
INSERT INTO Course_T VALUES (3036,3,'IE3036','Work Study & Ergonomics')

INSERT INTO Course_T VALUES (3061,4,'EE3061','Signals and Systems')
INSERT INTO Course_T VALUES (3011,4,'EE3011','Electronics 1')
INSERT INTO Course_T VALUES (3012,4,'EE3012','Electronics 2')
INSERT INTO Course_T VALUES (2021,4,'EE2021','Computer Tools EE')
INSERT INTO Course_T VALUES (3042,4,'EE3042','Energy Converge')

INSERT INTO Course_T VALUES (1110,5,'BIOE1110','Introduction to Bioengineering')
INSERT INTO Course_T VALUES (2062,5,'BIOE2062','Math Application')
INSERT INTO Course_T VALUES (3911,5,'BIOE3011','Bioengineering Lab 1')
INSERT INTO Course_T VALUES (3912,5,'BIOE3012','Bioengineering Lab 2')
INSERT INTO Course_T VALUES (3081,5,'BIOE3081','Bioengineering Thermo')

INSERT INTO Course_T VALUES (2011,6,'MSE2011','Materials Science 1')
INSERT INTO Course_T VALUES (2012,6,'MSE2012','Materials Science 2')
INSERT INTO Course_T VALUES (2013,6,'MSE2013','Metallurgical Thermodynamics')
INSERT INTO Course_T VALUES (2014,6,'MSE2014','Solution Thermodynamics')
INSERT INTO Course_T VALUES (2016,6,'MSE2016','Materials Laboratory')

INSERT INTO Course_T VALUES (3121,7,'KMM3121','Mass Transfer')
INSERT INTO Course_T VALUES (2104,7,'KMM2104','Organic Chemistry')
INSERT INTO Course_T VALUES (3115,7,'KMM3115','Chemistry Lab 1')
INSERT INTO Course_T VALUES (3116,7,'KMM3116','Chemistry Lab 2')
INSERT INTO Course_T VALUES (3026,7,'KMM3026','Separation Operations')

INSERT INTO Course_T VALUES (2031,8,'ENVE2031','Environmental Enginnering Chemistry 1')
INSERT INTO Course_T VALUES (2032,8,'ENVE2032','Environmental Enginnering Chemistry 2')
INSERT INTO Course_T VALUES (3030,8,'ENVE3030','Solid Waste Enginnering')
INSERT INTO Course_T VALUES (3211,8,'ENVE3011','Water Supply Enginnering')
INSERT INTO Course_T VALUES (3022,8,'ENVE3022','Wastewater Enginnering')

INSERT INTO Course_T VALUES (1113,9,'GRF1113','Photo Graphic Design')
INSERT INTO Course_T VALUES (2112,9,'GRF2112','Graphic Image')
INSERT INTO Course_T VALUES (2211,9,'GRF2211','History of Graphic Arts 1')
INSERT INTO Course_T VALUES (2212,9,'GRF2212','History of Graphic Arts 2')
INSERT INTO Course_T VALUES (3111,9,'GRF3111','Media Analysis')

INSERT INTO Course_T VALUES (2511,10,'IA2511','Studio Interior')
INSERT INTO Course_T VALUES (2513,10,'IA2513','Studio Furniture')
INSERT INTO Course_T VALUES (3512,10,'IA3512','Survey Restoration')
INSERT INTO Course_T VALUES (3516,10,'IA3516','Architecture')
INSERT INTO Course_T VALUES (4519,10,'IA4519','Portfolio')

--Insert Person
INSERT INTO Person_T VALUES (100,1,'Adam','RICHARDSON','adamrich@marun.edu','100','1985-07-08','Wall Street','New York City','New York')
INSERT INTO Lecturer_T VALUES (100,'Doc',15)

INSERT INTO Person_T VALUES (200,1,'Bera','BLACKBURN','berablack@marun.edu','200','1997-01-30','Istýklal Street','Istanbul','Beyoglu')
INSERT INTO Student_T VALUES (200,5,3.47,108,1)

INSERT INTO Person_T VALUES (201,1,'Saliha','HAWK','salihahawk@marun.edu','201','1995-07-10','Center Street','Istanbul','Sisli')
INSERT INTO Student_T VALUES (201,7,2.87,142,0)

INSERT INTO Person_T VALUES (101,2,'Jasmine','CHRISTIE','jasminech@marun.edu','101','1982-02-28','Semerkant Street','Georgia','Los Angles')
INSERT INTO Lecturer_T VALUES (101,'Doc',17)

INSERT INTO Person_T VALUES (203,2,'Lloyd','FLEMING','lloydfleming@marun.edu','203','1994-03-18','Rainbow','Uskup','Moscow')
INSERT INTO Student_T VALUES (203,5,3.07,102,1)

INSERT INTO Person_T VALUES (204,2,'Vickie','GARRISON','vickiegarrison@marun.edu','204','1998-05-04','Unicorn','Zombie City','Cloud York')
INSERT INTO Student_T VALUES (204,3,2.55,84,0)

INSERT INTO Person_T VALUES (102,3,'Rogan','EDMONDS','roganedmonds@marun.edu','102','1977-09-14','East Avenue','East London','London')
INSERT INTO Lecturer_T VALUES (102,'Prof',20)

INSERT INTO Person_T VALUES (205,3,'Zara','GREGORY','zaragregory@marun.edu','205','1999-08-16','Wolf Street','Mexico City','Yorkshire')
INSERT INTO Student_T VALUES (205,7,2.07,102,0)

INSERT INTO Person_T VALUES (206,3,'Zakaria','SANFORD','zakariasanford@marun.edu','206','1993-11-09','Zeytinburnu','Bagcýlar','Adana')
INSERT INTO Student_T VALUES (206,5,3.18,118,1)

INSERT INTO Person_T VALUES (103,4,'Marissa','LITTLE','marissalittle@marun.edu','103','1964-06-16','Bottle Street','Bakery City','Glaskow')
INSERT INTO Lecturer_T VALUES (103,'Prof',25)

INSERT INTO Person_T VALUES (207,4,'Virginia','LAWRENCE','virginialawrence@marun.edu','207','1995-10-07','Shower Street','Shine City','Diamond')
INSERT INTO Student_T VALUES (207,3,2.85,95,0)

INSERT INTO Person_T VALUES (208,4,'Elen','MCNALLY','elenmcnally@marun.edu','208','1997-04-27','Talk Street','Host City','Old York')
INSERT INTO Student_T VALUES (208,7,3.8,160,1)

INSERT INTO Person_T VALUES (104,5,'Khalid','AYALA','khalidayala@marun.edu','104','1990-02-02','Call Street','Pony City','Tommy York')
INSERT INTO Lecturer_T VALUES (104,'Dr',10)

INSERT INTO Person_T VALUES (209,5,'Alana','MCINTYRE','alanamcintyre@marun.edu','209','1998-03-18','Hall Street','Little City','Big York')
INSERT INTO Student_T VALUES (209,3,3.01,68,1)

INSERT INTO Person_T VALUES (210,5,'Reagan','HOWE','reaganhowe@marun.edu','210','1996-08-26','Vatan Street','Yatan City','Satan York')
INSERT INTO Student_T VALUES (210,5,2.83,96,0)

INSERT INTO Person_T VALUES (105,6,'Logan','COLEMAN','logancoleman@marun.edu','105','1980-10-17','Cookie Street','Corn City','Everton')
INSERT INTO Lecturer_T VALUES (105,'Doc',16)

INSERT INTO Person_T VALUES (211,6,'Miley','HORNE','mileyhorne@marun.edu','211','1996-02-16','Coffee Street','Table City','Dubai')
INSERT INTO Student_T VALUES (211,5,3.6,120,1)

INSERT INTO Person_T VALUES (212,6,'Myra','GUTIERREZ','myragutierrez@marun.edu','212','1999-08-11','Phone Street','Zippo','Yozgat')
INSERT INTO Student_T VALUES (212,3,2.6,64,0)

INSERT INTO Person_T VALUES (106,7,'Lucian','KELLER','luciankeller@marun.edu','106','1986-03-18','Mouse Street','Head City','Moskow')
INSERT INTO Lecturer_T VALUES (106,'Dr',11)

INSERT INTO Person_T VALUES (213,7,'Stanley','BARCLAY','stanleybarclay@marun.edu','213','1996-11-28','Parfume Street','Lamb City','Kars')
INSERT INTO Student_T VALUES (213,5,2.41,96,0)

INSERT INTO Person_T VALUES (214,7,'Nafisa','STEELE','nafisasteele@marun.edu','214','1994-06-11','Ball Street','Safe City','Kilis')
INSERT INTO Student_T VALUES (214,3,3.48,74,1)

INSERT INTO Person_T VALUES (107,8,'Shay','PHELPS','shayphelps@marun.edu','107','1982-04-10','Bant Street','Cable City','Sakarya')
INSERT INTO Lecturer_T VALUES (107,'Doc',16)

INSERT INTO Person_T VALUES (215,8,'Hunter','PARKES','hunterparkes@marun.edu','215','1999-05-18','Watch Street','Burn City','Edirne')
INSERT INTO Student_T VALUES (215,5,3.12,102,1)

INSERT INTO Person_T VALUES (216,8,'Romario','BAUTISTA','romariobautista@marun.edu','216','1995-06-15','Paper Street','Cart City','Antalya')
INSERT INTO Student_T VALUES (216,7,2.98,144,0)

INSERT INTO Person_T VALUES (108,9,'Willard','MIRANDA','willardmiranda@marun.edu','108','1973-05-08','Water Street','Gotham City','Sarayova')
INSERT INTO Lecturer_T VALUES (108,'Doc',19)

INSERT INTO Person_T VALUES (217,9,'Omar','CALLAHAN','omarcallahan@marun.edu','217','1997-03-15','Soda Street','Pure City','Atina')
INSERT INTO Student_T VALUES (217,5,3.23,98,1)

INSERT INTO Person_T VALUES (218,9,'Benas','WELLS','benaswells@marun.edu','218','1996-12-11','Component Street','Crem City','Roma')
INSERT INTO Student_T VALUES (218,3,2.68,64,0)

INSERT INTO Person_T VALUES (109,10,'Ioana','MCCORMACK','ioanamccormack@marun.edu','109','1962-07-08','Colar Street','Git City','Kiev')
INSERT INTO Lecturer_T VALUES (109,'Prof',27)

INSERT INTO Person_T VALUES (219,10,'Anabel','TIERNEY','anabeltierney@marun.edu','219','1993-04-29','Dolar Street','Hist City','Baku')
INSERT INTO Student_T VALUES (219,7,2.71,148,0)

INSERT INTO Person_T VALUES (220,10,'Deniz','JIMENEZ','denizjimenez@marun.edu','220','1998-08-21','Coder Street','Clone City','Kahire')
INSERT INTO Student_T VALUES (220,3,3.02,72,1)

--Insert Node
INSERT INTO Node_T VALUES (3055,GETDATE(),'IMPORTANT!!!','You will get your laptops tomarrow.')
INSERT INTO Announcement_T SELECT COUNT(*) FROM Node_T

INSERT INTO Node_T VALUES (3055,GETDATE(),'QUIZ','You can get a quiz on Monday.')
INSERT INTO Announcement_T SELECT COUNT(*) FROM Node_T

INSERT INTO Node_T VALUES (3055,GETDATE(),'LAB SECTION','You have no lab section for this week.')
INSERT INTO Announcement_T SELECT COUNT(*) FROM Node_T

INSERT INTO Node_T VALUES (3055,GETDATE(),'NOTE.','We will continue this chapter.')
INSERT INTO LectureNote_T (lecture_note_id,document) VALUES ((SELECT COUNT(*) FROM Node_T),'Document.pdf')

INSERT INTO Node_T VALUES (3055,GETDATE(),'NOTE.','E-R diagram notes.')
INSERT INTO LectureNote_T (lecture_note_id,document) VALUES ((SELECT COUNT(*) FROM Node_T),'E-R_Diagram.pdf')

INSERT INTO Node_T VALUES (3055,GETDATE(),'Homework','Step 2 is available.')
INSERT INTO Assignment_T (assignment_id,end_date) VALUES ((SELECT COUNT(*) FROM Node_T),'2019-12-30')

--Insert Classroom
INSERT INTO Classroom_T VALUES (3055,200,'2020-01-09')
INSERT INTO Classroom_T VALUES (3055,201,'2020-01-09')

--Insert Delivery
INSERT INTO Delivery_T VALUES (200,6,'Homework200.pdf')
INSERT INTO Delivery_T VALUES (201,6,'Homework201.pdf')

--Insert Comment
INSERT INTO Comment_T(node_id,person_id,comment_text) VALUES (1,200,'I have no laptop.')
INSERT INTO Comment_T VALUES (1,201,'We can share my laptop.',200)
