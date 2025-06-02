import { useState, useEffect, ChangeEvent, FormEvent } from "react"
import type { NextPage } from "next"
import Link from "next/link"
import styles from "../../styles/FormPage.module.css"
import tableStyles from "../../styles/TablePage.module.css"
import { Course, Schedule, DaysOfWeek, DayOfWeek } from "../../interfaces"

const API_BASE_URL = "http://localhost:8100/api/v1"

const PREDEFINED_START_TIMES = ["09:00", "11:00", "14:00", "16:00"]

const GenerateSchedulePage: NextPage = () => {
  const [allCourses, setAllCourses] = useState<Course[]>([])
  const [selectedCourseIds, setSelectedCourseIds] = useState<Set<number>>(
    new Set()
  )
  const [semester, setSemester] = useState<string>("")
  const [academicYear, setAcademicYear] = useState<string>("")
  const [selectedDays, setSelectedDays] = useState<Set<DayOfWeek>>(new Set())
  const [selectedStartTimes, setSelectedStartTimes] = useState<Set<string>>(
    new Set()
  )
  const [defaultDurationHours, setDefaultDurationHours] = useState<number>(2)

  const [generatedSchedules, setGeneratedSchedules] = useState<Schedule[]>([])
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState<boolean>(false)
  const [loadingCourses, setLoadingCourses] = useState<boolean>(true)

  useEffect(() => {
    async function fetchCourses() {
      try {
        setLoadingCourses(true)
        const response = await fetch(`${API_BASE_URL}/courses`)
        if (!response.ok) {
          throw new Error("Failed to fetch courses")
        }
        const data: Course[] = await response.json()
        setAllCourses(data)
      } catch (e: any) {
        setError(e.message)
        console.error("Failed to fetch courses:", e)
      } finally {
        setLoadingCourses(false)
      }
    }
    fetchCourses()
  }, [])

  const handleCourseSelectionChange = (courseId: number) => {
    setSelectedCourseIds((prev) => {
      const newSelection = new Set(prev)
      if (newSelection.has(courseId)) {
        newSelection.delete(courseId)
      } else {
        newSelection.add(courseId)
      }
      return newSelection
    })
  }

  const handleDaySelectionChange = (day: DayOfWeek) => {
    setSelectedDays((prev) => {
      const newSelection = new Set(prev)
      if (newSelection.has(day)) {
        newSelection.delete(day)
      } else {
        newSelection.add(day)
      }
      return newSelection
    })
  }

  const handleStartTimeSelectionChange = (time: string) => {
    setSelectedStartTimes((prev) => {
      const newSelection = new Set(prev)
      if (newSelection.has(time)) {
        newSelection.delete(time)
      } else {
        newSelection.add(time)
      }
      return newSelection
    })
  }

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    setGeneratedSchedules([])

    if (selectedCourseIds.size === 0) {
      setError("Please select at least one course.")
      setSubmitting(false)
      return
    }
    if (selectedDays.size === 0) {
      setError("Please select at least one day of the week.")
      setSubmitting(false)
      return
    }
    if (selectedStartTimes.size === 0) {
      setError("Please select at least one start time.")
      setSubmitting(false)
      return
    }

    const payload = {
      courseIds: Array.from(selectedCourseIds),
      semester,
      academicYear,
      daysOfWeek: Array.from(selectedDays),
      startTimes: Array.from(selectedStartTimes),
      defaultDurationHours,
    }

    try {
      const response = await fetch(
        `${API_BASE_URL}/schedules/generate-automatic`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        }
      )

      const responseData = await response.json()
      if (!response.ok) {
        throw new Error(
          responseData.message || `HTTP error! status: ${response.status}`
        )
      }
      setGeneratedSchedules(responseData)
      if (responseData.length === 0 && payload.courseIds.length > 0) {
        setError(
          "No schedules could be generated with the given constraints for the selected courses."
        )
      } else if (responseData.length > 0) {
        // clear errors
        setError(null)
      }
    } catch (err: any) {
      setError(err.message)
      console.error("Failed to generate schedule:", err)
    } finally {
      setSubmitting(false)
    }
  }

  if (loadingCourses) return <p>Loading courses for selection...</p>

  return (
    <div className={styles.container}>
      <h1>Generate Automatic Schedule</h1>
      {error && <p className={styles.error}>{error}</p>}
      <form onSubmit={handleSubmit} className={styles.form}>
        <fieldset>
          <legend>Courses to Schedule</legend>
          {allCourses.length === 0 && <p>No courses available to select.</p>}
          {allCourses.map((course) => (
            <div key={course.courseId} className={styles.checkboxItem}>
              <input
                type="checkbox"
                id={`course-${course.courseId}`}
                checked={selectedCourseIds.has(course.courseId)}
                onChange={() => handleCourseSelectionChange(course.courseId)}
              />
              <label htmlFor={`course-${course.courseId}`}>
                {course.courseCode} - {course.courseName}
              </label>
            </div>
          ))}
        </fieldset>

        <div>
          <label htmlFor="semester">Semester:</label>
          <input
            type="text"
            id="semester"
            value={semester}
            onChange={(e) => setSemester(e.target.value)}
            required
            placeholder="e.g., Toamna 2024"
          />
        </div>

        <div>
          <label htmlFor="academicYear">Academic Year:</label>
          <input
            type="text"
            id="academicYear"
            value={academicYear}
            onChange={(e) => setAcademicYear(e.target.value)}
            required
            placeholder="e.g., 2024-2025"
          />
        </div>

        <fieldset>
          <legend>Preferred Days of Week</legend>
          {DaysOfWeek.map((day) => (
            <div key={day} className={styles.checkboxItem}>
              <input
                type="checkbox"
                id={`day-${day}`}
                checked={selectedDays.has(day)}
                onChange={() => handleDaySelectionChange(day)}
              />
              <label htmlFor={`day-${day}`}>{day}</label>
            </div>
          ))}
        </fieldset>

        <fieldset>
          <legend>Preferred Start Times</legend>
          {PREDEFINED_START_TIMES.map((time) => (
            <div key={time} className={styles.checkboxItem}>
              <input
                type="checkbox"
                id={`time-${time.replace(":", "")}`}
                checked={selectedStartTimes.has(time)}
                onChange={() => handleStartTimeSelectionChange(time)}
              />
              <label htmlFor={`time-${time.replace(":", "")}`}>{time}</label>
            </div>
          ))}
        </fieldset>

        <div>
          <label htmlFor="defaultDurationHours">
            Default Class Duration (hours):
          </label>
          <input
            type="number"
            id="defaultDurationHours"
            value={defaultDurationHours}
            onChange={(e) =>
              setDefaultDurationHours(parseInt(e.target.value, 10) || 1)
            }
            required
            min="1"
            max="4"
          />
        </div>

        <button
          type="submit"
          disabled={submitting || loadingCourses}
          className={styles.submitButton}
        >
          {submitting ? "Generating..." : "Generate Schedule"}
        </button>
      </form>

      {generatedSchedules.length > 0 && (
        <div className={tableStyles.container} style={{ marginTop: "30px" }}>
          <h2>Generated Schedules</h2>
          <table className={tableStyles.table}>
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
              </tr>
            </thead>
            <tbody>
              {generatedSchedules.map((sch) => (
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
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      <div style={{ marginTop: "20px" }}>
        <Link href="/schedules" className={styles.backLink}>
          Back to Schedules List
        </Link>
      </div>
    </div>
  )
}

export default GenerateSchedulePage
