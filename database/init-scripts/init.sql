DROP TABLE IF EXISTS Enrollments CASCADE;
DROP TABLE IF EXISTS Schedule CASCADE;
DROP TABLE IF EXISTS Courses CASCADE;
DROP TABLE IF EXISTS Classrooms CASCADE;
DROP TABLE IF EXISTS Professors CASCADE;
DROP TABLE IF EXISTS Students CASCADE;

CREATE TABLE Students (
    student_id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL, 
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    major VARCHAR(100),
    enrollment_date DATE DEFAULT CURRENT_DATE 
);

CREATE TABLE Professors (
    professor_id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    department VARCHAR(100) NOT NULL,
    office VARCHAR(50)
);

CREATE TABLE Classrooms (
    room_id SERIAL PRIMARY KEY,
    room_number VARCHAR(20) UNIQUE NOT NULL,
    capacity INT NOT NULL CHECK (capacity > 0),
    has_projector BOOLEAN DEFAULT FALSE,
    building VARCHAR(100)
);

CREATE TABLE Courses (
    course_id SERIAL PRIMARY KEY,
    course_code VARCHAR(20) UNIQUE NOT NULL, 
    course_name VARCHAR(255) NOT NULL,
    credits INT NOT NULL CHECK (credits > 0 AND credits < 20), 
    department VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE Schedule (
    schedule_id SERIAL PRIMARY KEY, 
    course_id INT NOT NULL, 
    professor_id INT NOT NULL, 
    room_id INT NOT NULL, 
    day_of_week VARCHAR(10) NOT NULL CHECK (day_of_week IN ('Luni', 'Marti', 'Miercuri', 'Joi', 'Vineri', 'Sambata', 'Duminica')), 
    start_time TIME NOT NULL,
    end_time TIME NOT NULL, 
    semester VARCHAR(50) NOT NULL,
    academic_year VARCHAR(9) NOT NULL, 

    CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES Courses(course_id) ON DELETE CASCADE, 
    CONSTRAINT fk_professor FOREIGN KEY (professor_id) REFERENCES Professors(professor_id) ON DELETE RESTRICT, 
    CONSTRAINT fk_classroom FOREIGN KEY (room_id) REFERENCES Classrooms(room_id) ON DELETE RESTRICT, 
    CONSTRAINT check_time CHECK (end_time > start_time), 
    CONSTRAINT unique_room_time_slot UNIQUE (room_id, day_of_week, semester, academic_year, start_time) 
);

CREATE TABLE Enrollments (
    enrollment_id SERIAL PRIMARY KEY, 
    student_id INT NOT NULL, 
    schedule_id INT NOT NULL,
    enrollment_date DATE DEFAULT CURRENT_DATE, 
    grade NUMERIC(5, 2) CHECK (grade >= 0.00 AND grade <= 100.00), 
	
    CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES Students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_schedule FOREIGN KEY (schedule_id) REFERENCES Schedule(schedule_id) ON DELETE CASCADE,
    CONSTRAINT unique_student_schedule UNIQUE (student_id, schedule_id)
);

-- indexes (index names are identifiers too, but typically less critical for JPA mapping errors)
CREATE INDEX idx_enrollments_student_id ON Enrollments(student_id);
CREATE INDEX idx_enrollments_schedule_id ON Enrollments(schedule_id);
CREATE INDEX idx_schedule_course_id ON Schedule(course_id);
CREATE INDEX idx_schedule_professor_id ON Schedule(professor_id);
CREATE INDEX idx_schedule_room_id ON Schedule(room_id);
CREATE INDEX idx_students_lastname ON Students(last_name); 
CREATE INDEX idx_professors_lastname ON Professors(last_name); 
CREATE INDEX idx_courses_code ON Courses(course_code); 