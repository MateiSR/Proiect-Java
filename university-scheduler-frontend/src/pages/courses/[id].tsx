import { useRouter } from "next/router"
import { useEffect, useState } from "react"
import Link from "next/link"
import type { NextPage } from "next"
import styles from "../../styles/DetailPage.module.css"
import { Course } from "@/interfaces"

const API_URL_BASE = "http://localhost:8100/api/v1/courses"

const CourseDetailPage: NextPage = () => {
  const router = useRouter()
  const { id } = router.query
  const [course, setCourse] = useState<Course | null>(null)
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!id || typeof id !== "string") return

    async function fetchCourse() {
      try {
        setLoading(true)
        setError(null)
        const response = await fetch(`${API_URL_BASE}/${id}`)
        if (!response.ok) {
          if (response.status === 404) {
            setError("Course not found.")
          } else {
            throw new Error(`HTTP error! status: ${response.status}`)
          }
          return
        }
        const data: Course = await response.json()
        setCourse(data)
      } catch (e: any) {
        setError(e.message)
        console.error("Failed to fetch course:", e)
      } finally {
        setLoading(false)
      }
    }
    fetchCourse()
  }, [id])

  if (loading) return <p>Loading course details...</p>
  if (error) return <p>Error: {error}</p>
  if (!course) return <p>Course not found.</p>

  return (
    <div className={styles.container}>
      <h1>Course Details</h1>
      <div className={styles.details}>
        <p>
          <strong>ID:</strong> {course.courseId}
        </p>
        <p>
          <strong>Code:</strong> {course.courseCode}
        </p>
        <p>
          <strong>Name:</strong> {course.courseName}
        </p>
        <p>
          <strong>Credits:</strong> {course.credits}
        </p>
        <p>
          <strong>Department:</strong> {course.department}
        </p>
        <p>
          <strong>Description:</strong> {course.description || "N/A"}
        </p>
      </div>
      <br />
      <Link href="/courses" className={styles.backLink}>
        Back to Courses List
      </Link>
    </div>
  )
}

export default CourseDetailPage
