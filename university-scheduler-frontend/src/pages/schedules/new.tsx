import { useState, ChangeEvent, FormEvent, useEffect } from "react"
import { useRouter } from "next/router"
import type { NextPage } from "next"
import styles from "../../styles/FormPage.module.css"
import {
  Course,
  Professor,
  Classroom,
  DayOfWeek,
  DaysOfWeek,
} from "../../interfaces"

const API_BASE_URL = "http://localhost:8100/api/v1"

const NewSchedulePage: NextPage = () => {
  const router = useRouter()
  const [formData, setFormData] = useState({
    courseId: "",
    professorId: "",
    classroomId: "",
    dayOfWeek: DaysOfWeek[0],
    startTime: "09:00",
    endTime: "11:00",
    semester: "",
    academicYear: "",
  })
  const [courses, setCourses] = useState<Course[]>([])
  const [professors, setProfessors] = useState<Professor[]>([])
  const [classrooms, setClassrooms] = useState<Classroom[]>([])
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState<boolean>(false)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [coursesRes, professorsRes, classroomsRes] = await Promise.all([
          fetch(`${API_BASE_URL}/courses`),
          fetch(`${API_BASE_URL}/professors`),
          fetch(`${API_BASE_URL}/classrooms`),
        ])
        setCourses(await coursesRes.json())
        setProfessors(await professorsRes.json())
        setClassrooms(await classroomsRes.json())
      } catch (e) {
        setError("Failed to load prerequisite data for form.")
        console.error(e)
      }
    }
    fetchData()
  }, [])

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setError(null)
    setSubmitting(true)

    const payload = {
      ...formData,
      courseId: parseInt(formData.courseId),
      professorId: parseInt(formData.professorId),
      classroomId: parseInt(formData.classroomId),
    }

    try {
      const response = await fetch(`${API_BASE_URL}/schedules`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      })

      if (!response.ok) {
        const errorData = await response
          .json()
          .catch(() => ({ message: "Unknown error" }))
        throw new Error(
          errorData.message || `HTTP error! status: ${response.status}`
        )
      }
      router.push("/schedules")
    } catch (err: any) {
      setError(err.message)
      console.error("Failed to create schedule:", err)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className={styles.container}>
      <h1>Add New Schedule</h1>
      {error && <p className={styles.error}>{error}</p>}
      <form onSubmit={handleSubmit} className={styles.form}>
        <div>
          <label htmlFor="courseId">Course:</label>
          <select
            id="courseId"
            name="courseId"
            value={formData.courseId}
            onChange={handleChange}
            required
          >
            <option value="">Select Course</option>
            {courses.map((c) => (
              <option key={c.courseId} value={c.courseId}>
                {c.courseCode} - {c.courseName}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label htmlFor="professorId">Professor:</label>
          <select
            id="professorId"
            name="professorId"
            value={formData.professorId}
            onChange={handleChange}
            required
          >
            <option value="">Select Professor</option>
            {professors.map((p) => (
              <option key={p.professorId} value={p.professorId}>
                {p.firstName} {p.lastName}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label htmlFor="classroomId">Classroom:</label>
          <select
            id="classroomId"
            name="classroomId"
            value={formData.classroomId}
            onChange={handleChange}
            required
          >
            <option value="">Select Classroom</option>
            {classrooms.map((c) => (
              <option key={c.roomId} value={c.roomId}>
                {c.roomNumber} (Cap: {c.capacity})
              </option>
            ))}
          </select>
        </div>
        <div>
          <label htmlFor="dayOfWeek">Day of Week:</label>
          <select
            id="dayOfWeek"
            name="dayOfWeek"
            value={formData.dayOfWeek}
            onChange={handleChange}
            required
          >
            {DaysOfWeek.map((day) => (
              <option key={day} value={day}>
                {day}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label htmlFor="startTime">Start Time:</label>
          <input
            type="time"
            id="startTime"
            name="startTime"
            value={formData.startTime}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="endTime">End Time:</label>
          <input
            type="time"
            id="endTime"
            name="endTime"
            value={formData.endTime}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="semester">Semester:</label>
          <input
            type="text"
            id="semester"
            name="semester"
            value={formData.semester}
            onChange={handleChange}
            required
            placeholder="e.g., Toamna 2024"
          />
        </div>
        <div>
          <label htmlFor="academicYear">Academic Year:</label>
          <input
            type="text"
            id="academicYear"
            name="academicYear"
            value={formData.academicYear}
            onChange={handleChange}
            required
            placeholder="e.g., 2024-2025"
          />
        </div>
        <button
          type="submit"
          disabled={submitting}
          className={styles.submitButton}
        >
          {submitting ? "Submitting..." : "Add Schedule"}
        </button>
      </form>
    </div>
  )
}

export default NewSchedulePage
