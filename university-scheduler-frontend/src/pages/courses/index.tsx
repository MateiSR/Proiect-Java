import Link from "next/link"
import { useEffect, useState } from "react"
import type { NextPage } from "next"
import styles from "../../styles/TablePage.module.css"
import { Course } from "../../interfaces"

const API_URL = "http://localhost:8100/api/v1/courses"

const CoursesPage: NextPage = () => {
  const [courses, setCourses] = useState<Course[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function fetchCourses() {
      try {
        setLoading(true)
        setError(null)
        const response = await fetch(API_URL)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        const data: Course[] = await response.json()
        setCourses(data)
      } catch (e: any) {
        setError(e.message)
        console.error("Failed to fetch courses:", e)
      } finally {
        setLoading(false)
      }
    }
    fetchCourses()
  }, [])

  if (loading) return <p>Loading courses...</p>
  if (error) return <p>Error loading courses: {error}</p>

  return (
    <div className={styles.container}>
      <h1>Courses</h1>
      <Link href="/courses/new" className={styles.addButton}>
        Add New Course
      </Link>
      {courses.length === 0 ? (
        <p>No courses found.</p>
      ) : (
        <table className={styles.table}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Code</th>
              <th>Name</th>
              <th>Credits</th>
              <th>Department</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {courses.map((course) => (
              <tr key={course.courseId}>
                <td>{course.courseId}</td>
                <td>{course.courseCode}</td>
                <td>{course.courseName}</td>
                <td>{course.credits}</td>
                <td>{course.department}</td>
                <td>
                  <Link
                    href={`/courses/${course.courseId}`}
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

export default CoursesPage
