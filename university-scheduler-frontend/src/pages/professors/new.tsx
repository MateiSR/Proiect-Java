// pages/professors/new.tsx
import { useState, ChangeEvent, FormEvent } from "react"
import { useRouter } from "next/router"
import type { NextPage } from "next"
import styles from "../../styles/FormPage.module.css"

const API_URL = "http://localhost:8100/api/v1/professors"

const NewProfessorPage: NextPage = () => {
  const router = useRouter()
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    department: "",
    office: "",
  })
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState<boolean>(false)

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setError(null)
    setSubmitting(true)

    const payload = {
      ...formData,
      office: formData.office === "" ? null : formData.office,
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
          .catch(() => ({ message: "Unknown error during error processing" }))
        throw new Error(
          errorData.message || `HTTP error! status: ${response.status}`
        )
      }
      router.push("/professors")
    } catch (err: any) {
      setError(err.message)
      console.error("Failed to create professor:", err)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className={styles.container}>
      <h1>Add New Professor</h1>
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
          <label htmlFor="department">Department:</label>
          <input
            type="text"
            id="department"
            name="department"
            value={formData.department}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="office">Office:</label>
          <input
            type="text"
            id="office"
            name="office"
            value={formData.office}
            onChange={handleChange}
          />
        </div>
        <button
          type="submit"
          disabled={submitting}
          className={styles.submitButton}
        >
          {submitting ? "Submitting..." : "Add Professor"}
        </button>
      </form>
    </div>
  )
}

export default NewProfessorPage
