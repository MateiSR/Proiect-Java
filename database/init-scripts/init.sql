DROP TABLE IF EXISTS Enrollments CASCADE;
DROP TABLE IF EXISTS Schedule CASCADE;
DROP TABLE IF EXISTS Courses CASCADE;
DROP TABLE IF EXISTS Classrooms CASCADE;
DROP TABLE IF EXISTS Professors CASCADE;
DROP TABLE IF EXISTS Students CASCADE;

CREATE TABLE Students (
    StudentID SERIAL PRIMARY KEY,
    FirstName VARCHAR(100) NOT NULL,
    LastName VARCHAR(100) NOT NULL,
    Email VARCHAR(255) UNIQUE NOT NULL,
    Major VARCHAR(100),
    EnrollmentDate DATE DEFAULT CURRENT_DATE
);

CREATE TABLE Professors (
    ProfessorID SERIAL PRIMARY KEY,
    FirstName VARCHAR(100) NOT NULL,
    LastName VARCHAR(100) NOT NULL,
    Email VARCHAR(255) UNIQUE NOT NULL,
    Department VARCHAR(100) NOT NULL,
    Office VARCHAR(50)
);

CREATE TABLE Classrooms (
    RoomID SERIAL PRIMARY KEY,
    RoomNumber VARCHAR(20) UNIQUE NOT NULL,
    Capacity INT NOT NULL CHECK (Capacity > 0),
    HasProjector BOOLEAN DEFAULT FALSE,
    Building VARCHAR(100)
);

-- Table for Courses
CREATE TABLE Courses (
    CourseID SERIAL PRIMARY KEY,
    CourseCode VARCHAR(20) UNIQUE NOT NULL,
    CourseName VARCHAR(255) NOT NULL,
    Credits INT NOT NULL CHECK (Credits > 0 AND Credits < 20),
    Department VARCHAR(100) NOT NULL,
    Description TEXT
);

CREATE TABLE Schedule (
    ScheduleID SERIAL PRIMARY KEY,
    CourseID INT NOT NULL,
    ProfessorID INT NOT NULL,
    RoomID INT NOT NULL,
    DayOfWeek VARCHAR(10) NOT NULL CHECK (DayOfWeek IN ('Luni', 'Marti', 'Miercuri', 'Joi', 'Vineri', 'Sambata', 'Duminica')),
    StartTime TIME NOT NULL,
    EndTime TIME NOT NULL,
    Semester VARCHAR(50) NOT NULL,
    AcademicYear VARCHAR(9) NOT NULL,

    CONSTRAINT fk_course FOREIGN KEY (CourseID) REFERENCES Courses(CourseID) ON DELETE CASCADE,
    CONSTRAINT fk_professor FOREIGN KEY (ProfessorID) REFERENCES Professors(ProfessorID) ON DELETE RESTRICT, -- Prevent deleting professor if they have scheduled classes
    CONSTRAINT fk_classroom FOREIGN KEY (RoomID) REFERENCES Classrooms(RoomID) ON DELETE RESTRICT, -- Prevent deleting classroom if it's scheduled
    CONSTRAINT check_time CHECK (EndTime > StartTime),
    -- prevent scheduling conflicts for the same room at the same time
    CONSTRAINT unique_room_time_slot UNIQUE (RoomID, DayOfWeek, Semester, AcademicYear, StartTime)
);

CREATE TABLE Enrollments (
    EnrollmentID SERIAL PRIMARY KEY,
    StudentID INT NOT NULL,
    ScheduleID INT NOT NULL,
    EnrollmentDate DATE DEFAULT CURRENT_DATE,
    Grade NUMERIC(5, 2) CHECK (Grade >= 0.00 AND Grade <= 100.00),
	
    CONSTRAINT fk_student FOREIGN KEY (StudentID) REFERENCES Students(StudentID) ON DELETE CASCADE,
    CONSTRAINT fk_schedule FOREIGN KEY (ScheduleID) REFERENCES Schedule(ScheduleID) ON DELETE CASCADE,
    CONSTRAINT unique_student_schedule UNIQUE (StudentID, ScheduleID) -- only once enrolled in the same class for a student
);

-- indexes
CREATE INDEX idx_enrollments_student_id ON Enrollments(StudentID);
CREATE INDEX idx_enrollments_schedule_id ON Enrollments(ScheduleID);
CREATE INDEX idx_schedule_course_id ON Schedule(CourseID);
CREATE INDEX idx_schedule_professor_id ON Schedule(ProfessorID);
CREATE INDEX idx_schedule_room_id ON Schedule(RoomID);
CREATE INDEX idx_students_lastname ON Students(LastName);
CREATE INDEX idx_professors_lastname ON Professors(LastName);
CREATE INDEX idx_courses_code ON Courses(CourseCode);
