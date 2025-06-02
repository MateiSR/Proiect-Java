import { useState, ChangeEvent, FormEvent, useEffect } from "react"
import { useRouter } from "next/router"
import type { NextPage } from "next"
import styles from "../../styles/FormPage.module.css"
import { Student, Schedule } from "../../interfaces"

const API_BASE_URL = "http://localhost:8100/api/v1"

const NewEnrollmentPage: NextPage = () => {
  const router = useRouter()
  const [formData, setFormData] = useState({
    studentId: "",
    scheduleId: "",
    enrollmentDate: new Date().toISOString().split("T")[0],
    grade: "",
  })
  const [students, setStudents] = useState<Student[]>([])
  const [schedules, setSchedules] = useState<Schedule[]>([])
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState<boolean>(false)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [studentsRes, schedulesRes] = await Promise.all([
          fetch(`${API_BASE_URL}/students`),
          fetch(`${API_BASE_URL}/schedules`),
        ])
        setStudents(await studentsRes.json())
        setSchedules(await schedulesRes.json())
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
      studentId: parseInt(formData.studentId),
      scheduleId: parseInt(formData.scheduleId),
      enrollmentDate: formData.enrollmentDate,
      grade: formData.grade === "" ? null : parseFloat(formData.grade),
    }

    try {
      const response = await fetch(`${API_BASE_URL}/enrollments`, {
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
      router.push("/enrollments")
    } catch (err: any) {
      setError(err.message)
      console.error("Failed to create enrollment:", err)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className={styles.container}>
      <h1>Add New Enrollment</h1>
      {error && <p className={styles.error}>{error}</p>}
      <form onSubmit={handleSubmit} className={styles.form}>
        <div>
          <label htmlFor="studentId">Student:</label>
          <select
            id="studentId"
            name="studentId"
            value={formData.studentId}
            onChange={handleChange}
            required
          >
            <option value="">Select Student</option>
            {students.map((s) => (
              <option key={s.studentId} value={s.studentId}>
                {s.firstName} {s.lastName} ({s.email})
              </option>
            ))}
          </select>
        </div>
        <div>
          <label htmlFor="scheduleId">Schedule:</label>
          <select
            id="scheduleId"
            name="scheduleId"
            value={formData.scheduleId}
            onChange={handleChange}
            required
          >
            <option value="">Select Schedule</option>
            {schedules.map((s) => (
              <option key={s.scheduleId} value={s.scheduleId}>
                ID: {s.scheduleId} - {s.course?.courseCode} ({s.dayOfWeek}{" "}
                {s.startTime}) - Prof: {s.professor?.lastName} - Room:{" "}
                {s.classroom?.roomNumber}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label htmlFor="enrollmentDate">Enrollment Date:</label>
          <input
            type="date"
            id="enrollmentDate"
            name="enrollmentDate"
            value={formData.enrollmentDate}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="grade">Grade (optional):</label>
          <input
            type="number"
            id="grade"
            name="grade"
            value={formData.grade}
            onChange={handleChange}
            step="0.01"
            min="0"
            max="100"
          />
        </div>
        <button
          type="submit"
          disabled={submitting}
          className={styles.submitButton}
        >
          {submitting ? "Submitting..." : "Add Enrollment"}
        </button>
      </form>
    </div>
  )
}

export default NewEnrollmentPage
