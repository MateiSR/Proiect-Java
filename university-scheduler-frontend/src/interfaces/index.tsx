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
