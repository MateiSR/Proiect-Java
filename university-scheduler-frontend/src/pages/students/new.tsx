import { useState } from "react"
import { useRouter } from "next/router"
import styles from "../../styles/FormPage.module.css"

const API_URL = "http://localhost:8100/api/v1/students"

export default function NewStudentPage() {
  const router = useRouter()
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    major: "",
    enrollmentDate: new Date().toISOString().split("T")[0],
  })
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    // added e: React.ChangeEvent<HTMLInputElement> to fix strict type checking
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    // added e: React.ChangeEvent<HTMLInputElement> to fix strict type checking
    // (same here)
    e.preventDefault()
    setError(null)
    setSubmitting(true)

    try {
      console.log("Submitting form data:", JSON.stringify(formData))
      const response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      })

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(
          errorData.message || `HTTP error! status: ${response.status}`
        )
      }
      // redirect on successful creation
      router.push("/students")
    } catch (err: any) {
      setError(err.message)
      console.error("Failed to create student:", err)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className={styles.container}>
      <h1>Add New Student</h1>
      {error && <p className={styles.error}>{error}</p>}
      <form onSubmit={handleSubmit} className={styles.form}>
        <div>
          <label htmlFor="firstName">First Name:</label>
          <input
            type="text"
            id="firstName"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="lastName">Last Name:</label>
          <input
            type="text"
            id="lastName"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="major">Major:</label>
          <input
            type="text"
            id="major"
            name="major"
            value={formData.major}
            onChange={handleChange}
          />
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
        <button
          type="submit"
          disabled={submitting}
          className={styles.submitButton}
        >
          {submitting ? "Submitting..." : "Add Student"}
        </button>
      </form>
    </div>
  )
}
