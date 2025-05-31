import Link from "next/link"
import { useEffect, useState } from "react"
import type { NextPage } from "next"
import styles from "../../styles/TablePage.module.css"

interface Student {
  studentId: number
  firstName: string
  lastName: string
  email: string
  major: string | null
  enrollmentDate: string
}

const API_URL = "http://localhost:8100/api/v1/students"

const StudentsPage: NextPage = () => {
  const [students, setStudents] = useState<Student[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function fetchStudents() {
      try {
        setLoading(true)
        setError(null)
        const response = await fetch(API_URL)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        const data: Student[] = await response.json()
        setStudents(data)
      } catch (e: any) {
        setError(e.message)
        console.error("Failed to fetch students:", e)
      } finally {
        setLoading(false)
      }
    }
    fetchStudents()
  }, [])

  if (loading) return <p>Loading students...</p>
  if (error) return <p>Error loading students: {error}</p>

  return (
    <div className={styles.container}>
      <h1>Students</h1>
      <Link href="/students/new" className={styles.addButton}>
        Add New Student
      </Link>
      {students.length === 0 ? (
        <p>No students found in db.</p>
      ) : (
        <table className={styles.table}>
          <thead>
            <tr>
              <th>ID</th>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Email</th>
              <th>Major</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {students.map((student) => (
              <tr key={student.studentId}>
                <td>{student.studentId}</td>
                <td>{student.firstName}</td>
                <td>{student.lastName}</td>
                <td>{student.email}</td>
                <td>{student.major || "N/A"}</td>
                <td>
                  <Link
                    href={`/students/${student.studentId}`}
                    className={styles.actionLink}
                  >
                    View
                  </Link>
                  {/* TODO: Edit / delete */}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

export default StudentsPage
