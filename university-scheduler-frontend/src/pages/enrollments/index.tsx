import Link from "next/link"
import { useEffect, useState } from "react"
import type { NextPage } from "next"
import styles from "../../styles/TablePage.module.css"
import { Enrollment } from "../../interfaces"

const API_URL = "http://localhost:8100/api/v1/enrollments"

const EnrollmentsPage: NextPage = () => {
  const [enrollments, setEnrollments] = useState<Enrollment[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function fetchEnrollments() {
      try {
        setLoading(true)
        setError(null)
        const response = await fetch(API_URL)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        const data: Enrollment[] = await response.json()
        setEnrollments(data)
      } catch (e: any) {
        setError(e.message)
        console.error("Failed to fetch enrollments:", e)
      } finally {
        setLoading(false)
      }
    }
    fetchEnrollments()
  }, [])

  if (loading) return <p>Loading enrollments...</p>
  if (error) return <p>Error loading enrollments: {error}</p>

  return (
    <div className={styles.container}>
      <h1>Enrollments</h1>
      <Link href="/enrollments/new" className={styles.addButton}>
        Add New Enrollment
      </Link>
      {enrollments.length === 0 ? (
        <p>No enrollments found.</p>
      ) : (
        <table className={styles.table}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Student</th>
              <th>Course (Scheduled)</th>
              <th>Enrollment Date</th>
              <th>Grade</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {enrollments.map((enr) => (
              <tr key={enr.enrollmentId}>
                <td>{enr.enrollmentId}</td>
                <td>
                  {enr.student?.firstName} {enr.student?.lastName}
                </td>
                <td>
                  {enr.schedule?.course?.courseCode} ({enr.schedule?.dayOfWeek}{" "}
                  {enr.schedule?.startTime})
                </td>
                <td>{new Date(enr.enrollmentDate).toLocaleDateString()}</td>
                <td>{enr.grade !== null ? enr.grade : "N/A"}</td>
                <td>
                  <Link
                    href={`/enrollments/${enr.enrollmentId}`}
                    className={styles.actionLink}
                  >
                    View
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

export default EnrollmentsPage
