import { useRouter } from "next/router"
import { useEffect, useState } from "react"
import Link from "next/link"
import type { NextPage } from "next"
import styles from "../../styles/DetailPage.module.css"
import { Professor } from "@/interfaces"

const API_URL_BASE = "http://localhost:8100/api/v1/professors"

const ProfessorDetailPage: NextPage = () => {
  const router = useRouter()
  const { id } = router.query // string or string[] or undefined
  const [professor, setProfessor] = useState<Professor | null>(null)
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!id || typeof id !== "string") return

    async function fetchProfessor() {
      try {
        setLoading(true)
        setError(null)
        const response = await fetch(`${API_URL_BASE}/${id}`)
        if (!response.ok) {
          if (response.status === 404) {
            setError("Professor not found.")
          } else {
            throw new Error(`HTTP error! status: ${response.status}`)
          }
          return
        }
        const data: Professor = await response.json()
        setProfessor(data)
      } catch (e: any) {
        setError(e.message)
        console.error("Failed to fetch professor:", e)
      } finally {
        setLoading(false)
      }
    }
    fetchProfessor()
  }, [id])

  if (loading) return <p>Loading professor details...</p>
  if (error) return <p>Error: {error}</p>
  if (!professor) return <p>Professor not found.</p>

  return (
    <div className={styles.container}>
      <h1>Professor Details</h1>
      <div className={styles.details}>
        <p>
          <strong>ID:</strong> {professor.professorId}
        </p>
        <p>
          <strong>First Name:</strong> {professor.firstName}
        </p>
        <p>
          <strong>Last Name:</strong> {professor.lastName}
        </p>
        <p>
          <strong>Email:</strong> {professor.email}
        </p>
        <p>
          <strong>Department:</strong> {professor.department}
        </p>
        <p>
          <strong>Office:</strong> {professor.office || "N/A"}
        </p>
      </div>
      <br />
      <Link href="/professors" className={styles.backLink}>
        Back to Professors List
      </Link>
    </div>
  )
}

export default ProfessorDetailPage
