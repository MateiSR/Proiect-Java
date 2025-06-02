import { useRouter } from "next/router"
import { useEffect, useState } from "react"
import Link from "next/link"
import type { NextPage } from "next"
import styles from "../../styles/DetailPage.module.css"
import { Schedule } from "@/interfaces"

const API_URL_BASE = "http://localhost:8100/api/v1/schedules"

const ScheduleDetailPage: NextPage = () => {
  const router = useRouter()
  const { id } = router.query
  const [schedule, setSchedule] = useState<Schedule | null>(null)
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!id || typeof id !== "string") return

    async function fetchSchedule() {
      try {
        setLoading(true)
        setError(null)
        const response = await fetch(`${API_URL_BASE}/${id}`)
        if (!response.ok) {
          if (response.status === 404) {
            setError("Schedule not found.")
          } else {
            throw new Error(`HTTP error! status: ${response.status}`)
          }
          return
        }
        const data: Schedule = await response.json()
        setSchedule(data)
      } catch (e: any) {
        setError(e.message)
        console.error("Failed to fetch schedule:", e)
      } finally {
        setLoading(false)
      }
    }
    fetchSchedule()
  }, [id])

  if (loading) return <p>Loading schedule details...</p>
  if (error) return <p>Error: {error}</p>
  if (!schedule) return <p>Schedule not found.</p>

  return (
    <div className={styles.container}>
      <h1>Schedule Details</h1>
      <div className={styles.details}>
        <p>
          <strong>ID:</strong> {schedule.scheduleId}
        </p>
        <p>
          <strong>Course:</strong> {schedule.course?.courseCode} -{" "}
          {schedule.course?.courseName}
        </p>
        <p>
          <strong>Professor:</strong> {schedule.professor?.firstName}{" "}
          {schedule.professor?.lastName}
        </p>
        <p>
          <strong>Classroom:</strong> {schedule.classroom?.roomNumber}
        </p>
        <p>
          <strong>Day:</strong> {schedule.dayOfWeek}
        </p>
        <p>
          <strong>Time:</strong> {schedule.startTime} - {schedule.endTime}
        </p>
        <p>
          <strong>Semester:</strong> {schedule.semester}
        </p>
        <p>
          <strong>Academic Year:</strong> {schedule.academicYear}
        </p>
      </div>
      <br />
      <Link href="/schedules" className={styles.backLink}>
        Back to Schedules List
      </Link>
    </div>
  )
}

export default ScheduleDetailPage
