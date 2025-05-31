import Link from "next/link"
import { useEffect, useState } from "react"
import type { NextPage } from "next"
import styles from "../../styles/TablePage.module.css"
import { Professor } from "../../interfaces/index"

const API_URL = "http://localhost:8100/api/v1/professors"

const ProfessorsPage: NextPage = () => {
  const [professors, setProfessors] = useState<Professor[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function fetchProfessors() {
      try {
        setLoading(true)
        setError(null)
        const response = await fetch(API_URL)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        const data: Professor[] = await response.json()
        setProfessors(data)
      } catch (e: any) {
        setError(e.message)
        console.error("Failed to fetch professors:", e)
      } finally {
        setLoading(false)
      }
    }
    fetchProfessors()
  }, [])

  if (loading) return <p>Loading professors...</p>
  if (error) return <p>Error loading professors: {error}</p>

  return (
    <div className={styles.container}>
      <h1>Professors</h1>
      <Link href="/professors/new" className={styles.addButton}>
        Add New Professor
      </Link>
      {professors.length === 0 ? (
        <p>No professors found.</p>
      ) : (
        <table className={styles.table}>
          <thead>
            <tr>
              <th>ID</th>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Email</th>
              <th>Department</th>
              <th>Office</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {professors.map((prof) => (
              <tr key={prof.professorId}>
                <td>{prof.professorId}</td>
                <td>{prof.firstName}</td>
                <td>{prof.lastName}</td>
                <td>{prof.email}</td>
                <td>{prof.department}</td>
                <td>{prof.office || "N/A"}</td>
                <td>
                  <Link
                    href={`/professors/${prof.professorId}`}
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

export default ProfessorsPage
