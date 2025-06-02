export interface Student {
  studentId: number
  firstName: string
  lastName: string
  email: string
  major: string | null
  enrollmentDate: string
}

export interface Professor {
  professorId: number
  firstName: string
  lastName: string
  email: string
  department: string
  office: string | null
}

export interface Classroom {
  roomId: number
  roomNumber: string
  capacity: number
  hasProjector: boolean
  building: string | null
}

export interface Course {
  courseId: number
  courseCode: string
  courseName: string
  credits: number
  department: string
  description: string | null
}

export interface Schedule {
  scheduleId: number
  course: Course
  professor: Professor
  classroom: Classroom
  dayOfWeek: string
  startTime: string
  endTime: string
  semester: string
  academicYear: string
}

export interface Enrollment {
  enrollmentId: number
  student: Student
  schedule: Schedule
  enrollmentDate: string
  grade: number | null
}

export type DayOfWeek =
  | "Luni"
  | "Marti"
  | "Miercuri"
  | "Joi"
  | "Vineri"
  | "Sambata"
  | "Duminica"

export const DaysOfWeek: DayOfWeek[] = [
  "Luni",
  "Marti",
  "Miercuri",
  "Joi",
  "Vineri",
  "Sambata",
  "Duminica",
]
