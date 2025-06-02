import Link from "next/link"
import { useEffect, useState } from "react"
import type { NextPage } from "next"
import styles from "../../styles/TablePage.module.css"
import { Schedule } from "../../interfaces"

const API_URL = "http://localhost:8100/api/v1/schedules"

const SchedulesPage: NextPage = () => {
  const [schedules, setSchedules] = useState<Schedule[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function fetchSchedules() {
      try {
        setLoading(true)
        setError(null)
        const response = await fetch(API_URL)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        const data: Schedule[] = await response.json()
        setSchedules(data)
      } catch (e: any) {
        setError(e.message)
        console.error("Failed to fetch schedules:", e)
      } finally {
        setLoading(false)
      }
    }
    fetchSchedules()
  }, [])

  if (loading) return <p>Loading schedules...</p>
  if (error) return <p>Error loading schedules: {error}</p>

  return (
    <div className={styles.container}>
      <h1>Schedules</h1>
      <Link href="/schedules/new" className={styles.addButton}>
        Add New Schedule
      </Link>
      {schedules.length === 0 ? (
        <p>No schedules found.</p>
      ) : (
        <table className={styles.table}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Course</th>
              <th>Professor</th>
              <th>Classroom</th>
              <th>Day</th>
              <th>Time</th>
              <th>Semester</th>
              <th>Year</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {schedules.map((sch) => (
              <tr key={sch.scheduleId}>
                <td>{sch.scheduleId}</td>
                <td>
                  {sch.course?.courseCode} - {sch.course?.courseName}
                </td>
                <td>
                  {sch.professor?.firstName} {sch.professor?.lastName}
                </td>
                <td>{sch.classroom?.roomNumber}</td>
                <td>{sch.dayOfWeek}</td>
                <td>
                  {sch.startTime} - {sch.endTime}
                </td>
                <td>{sch.semester}</td>
                <td>{sch.academicYear}</td>
                <td>
                  <Link
                    href={`/schedules/${sch.scheduleId}`}
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

export default SchedulesPage
