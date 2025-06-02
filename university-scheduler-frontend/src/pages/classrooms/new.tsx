import { useState, ChangeEvent, FormEvent } from "react"
import { useRouter } from "next/router"
import type { NextPage } from "next"
import styles from "../../styles/FormPage.module.css"

const API_URL = "http://localhost:8100/api/v1/classrooms"

const NewClassroomPage: NextPage = () => {
  const router = useRouter()
  const [formData, setFormData] = useState({
    roomNumber: "",
    capacity: 0,
    hasProjector: false,
    building: "",
  })
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState<boolean>(false)

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value, type } = e.target
    if (type === "checkbox") {
      const { checked } = e.target as HTMLInputElement
      setFormData((prev) => ({ ...prev, [name]: checked }))
    } else {
      setFormData((prev) => ({
        ...prev,
        [name]: type === "number" ? parseInt(value, 10) || 0 : value,
      }))
    }
  }

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setError(null)
    setSubmitting(true)

    const payload = {
      ...formData,
      building: formData.building === "" ? null : formData.building,
    }

    try {
      const response = await fetch(API_URL, {
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
      router.push("/classrooms")
    } catch (err: any) {
      setError(err.message)
      console.error("Failed to create classroom:", err)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className={styles.container}>
      <h1>Add New Classroom</h1>
      {error && <p className={styles.error}>{error}</p>}
      <form onSubmit={handleSubmit} className={styles.form}>
        <div>
          <label htmlFor="roomNumber">Room Number:</label>
          <input
            type="text"
            id="roomNumber"
            name="roomNumber"
            value={formData.roomNumber}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="capacity">Capacity:</label>
          <input
            type="number"
            id="capacity"
            name="capacity"
            value={formData.capacity}
            onChange={handleChange}
            required
            min="1"
          />
        </div>
        <div className={styles.checkboxContainer}>
          <label htmlFor="hasProjector">Has Projector:</label>
          <input
            type="checkbox"
            id="hasProjector"
            name="hasProjector"
            checked={formData.hasProjector}
            onChange={handleChange}
            className={styles.checkboxInput}
          />
        </div>
        <div>
          <label htmlFor="building">Building:</label>
          <input
            type="text"
            id="building"
            name="building"
            value={formData.building}
            onChange={handleChange}
          />
        </div>
        <button
          type="submit"
          disabled={submitting}
          className={styles.submitButton}
        >
          {submitting ? "Submitting..." : "Add Classroom"}
        </button>
      </form>
    </div>
  )
}

export default NewClassroomPage
