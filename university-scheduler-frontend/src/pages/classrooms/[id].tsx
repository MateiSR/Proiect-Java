import { useRouter } from "next/router"
import { useEffect, useState } from "react"
import Link from "next/link"
import type { NextPage } from "next"
import styles from "../../styles/DetailPage.module.css"
import { Classroom } from "@/interfaces"

const API_URL_BASE = "http://localhost:8100/api/v1/classrooms"

const ClassroomDetailPage: NextPage = () => {
  const router = useRouter()
  const { id } = router.query
  const [classroom, setClassroom] = useState<Classroom | null>(null)
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!id || typeof id !== "string") return

    async function fetchClassroom() {
      try {
        setLoading(true)
        setError(null)
        const response = await fetch(`${API_URL_BASE}/${id}`)
        if (!response.ok) {
          if (response.status === 404) {
            setError("Classroom not found.")
          } else {
            throw new Error(`HTTP error! status: ${response.status}`)
          }
          return
        }
        const data: Classroom = await response.json()
        setClassroom(data)
      } catch (e: any) {
        setError(e.message)
        console.error("Failed to fetch classroom:", e)
      } finally {
        setLoading(false)
      }
    }
    fetchClassroom()
  }, [id])

  if (loading) return <p>Loading classroom details...</p>
  if (error) return <p>Error: {error}</p>
  if (!classroom) return <p>Classroom not found.</p>

  return (
    <div className={styles.container}>
      <h1>Classroom Details</h1>
      <div className={styles.details}>
        <p>
          <strong>ID:</strong> {classroom.roomId}
        </p>
        <p>
          <strong>Room Number:</strong> {classroom.roomNumber}
        </p>
        <p>
          <strong>Capacity:</strong> {classroom.capacity}
        </p>
        <p>
          <strong>Has Projector:</strong>{" "}
          {classroom.hasProjector ? "Yes" : "No"}
        </p>
        <p>
          <strong>Building:</strong> {classroom.building || "N/A"}
        </p>
      </div>
      <br />
      <Link href="/classrooms" className={styles.backLink}>
        Back to Classrooms List
      </Link>
    </div>
  )
}

export default ClassroomDetailPage
