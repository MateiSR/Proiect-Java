import { useRouter } from "next/router"
import { useEffect, useState } from "react"
import Link from "next/link"
import type { NextPage } from "next"
import styles from "@/styles/DetailPage.module.css"
import { Student } from "@/interfaces"

const API_URL_BASE = "http://localhost:8100/api/v1/students"

const StudentDetailPage: NextPage = () => {
  const router = useRouter()
  const { id } = router.query // (string[] | string | undefined)
  const [student, setStudent] = useState<Student | null>(null)
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!id || typeof id !== "string") return

    async function fetchStudent() {
      try {
        setLoading(true)
        setError(null)
        const response = await fetch(`${API_URL_BASE}/${id}`)
        if (!response.ok) {
          if (response.status === 404) {
            setError("Student not found.")
          } else {
            throw new Error(`HTTP error! status: ${response.status}`)
          }
          return
        }
        const data: Student = await response.json()
        setStudent(data)
      } catch (e: any) {
        setError(e.message)
        console.error("Failed to fetch student:", e)
      } finally {
        setLoading(false)
      }
    }
    fetchStudent()
  }, [id])

  if (loading) return <p>Loading student details...</p>
  if (error) return <p>Error: {error}</p>
  if (!student) return <p>Student not found.</p>

  return (
    <div className={styles.container}>
      <h1>Student Details</h1>
      <div className={styles.details}>
        <p>
          <strong>ID:</strong> {student.studentId}
        </p>
        <p>
          <strong>First Name:</strong> {student.firstName}
        </p>
        <p>
          <strong>Last Name:</strong> {student.lastName}
        </p>
        <p>
          <strong>Email:</strong> {student.email}
        </p>
        <p>
          <strong>Major:</strong> {student.major || "N/A"}
        </p>
        <p>
          <strong>Enrollment Date:</strong> {student.enrollmentDate}
        </p>
      </div>
      {/* <Link href={`/students/edit/${student.studentId}`}><a>Edit Student</a></Link> */}
      <br />
      <Link href="/students" className={styles.backLink}>
        Back to Students List
      </Link>
    </div>
  )
}

export default StudentDetailPage
