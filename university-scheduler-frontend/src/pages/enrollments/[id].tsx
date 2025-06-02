import { useRouter } from "next/router"
import { useEffect, useState } from "react"
import Link from "next/link"
import type { NextPage } from "next"
import styles from "../../styles/DetailPage.module.css"
import { Enrollment } from "@/interfaces"

const API_URL_BASE = "http://localhost:8100/api/v1/enrollments"

const EnrollmentDetailPage: NextPage = () => {
  const router = useRouter()
  const { id } = router.query
  const [enrollment, setEnrollment] = useState<Enrollment | null>(null)
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!id || typeof id !== "string") return

    async function fetchEnrollment() {
      try {
        setLoading(true)
        setError(null)
        const response = await fetch(`${API_URL_BASE}/${id}`)
        if (!response.ok) {
          if (response.status === 404) {
            setError("Enrollment not found.")
          } else {
            throw new Error(`HTTP error! status: ${response.status}`)
          }
          return
        }
        const data: Enrollment = await response.json()
        setEnrollment(data)
      } catch (e: any) {
        setError(e.message)
        console.error("Failed to fetch enrollment:", e)
      } finally {
        setLoading(false)
      }
    }
    fetchEnrollment()
  }, [id])

  if (loading) return <p>Loading enrollment details...</p>
  if (error) return <p>Error: {error}</p>
  if (!enrollment) return <p>Enrollment not found.</p>

  return (
    <div className={styles.container}>
      <h1>Enrollment Details</h1>
      <div className={styles.details}>
        <p>
          <strong>ID:</strong> {enrollment.enrollmentId}
        </p>
        <p>
          <strong>Student:</strong> {enrollment.student?.firstName}{" "}
          {enrollment.student?.lastName} (ID: {enrollment.student?.studentId})
        </p>
        <p>
          <strong>Scheduled Course:</strong>{" "}
          {enrollment.schedule?.course?.courseName} (
          {enrollment.schedule?.course?.courseCode})
        </p>
        <p>
          <strong>Professor:</strong>{" "}
          {enrollment.schedule?.professor?.firstName}{" "}
          {enrollment.schedule?.professor?.lastName}
        </p>
        <p>
          <strong>Classroom:</strong>{" "}
          {enrollment.schedule?.classroom?.roomNumber}
        </p>
        <p>
          <strong>Day & Time:</strong> {enrollment.schedule?.dayOfWeek},{" "}
          {enrollment.schedule?.startTime} - {enrollment.schedule?.endTime}
        </p>
        <p>
          <strong>Semester:</strong> {enrollment.schedule?.semester}{" "}
          {enrollment.schedule?.academicYear}
        </p>
        <p>
          <strong>Enrollment Date:</strong>{" "}
          {new Date(enrollment.enrollmentDate).toLocaleDateString()}
        </p>
        <p>
          <strong>Grade:</strong>{" "}
          {enrollment.grade !== null ? enrollment.grade : "N/A"}
        </p>
      </div>
      <br />
      <Link href="/enrollments" className={styles.backLink}>
        Back to Enrollments List
      </Link>
    </div>
  )
}

export default EnrollmentDetailPage
