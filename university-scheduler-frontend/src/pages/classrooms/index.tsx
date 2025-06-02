import Link from "next/link"
import { useEffect, useState } from "react"
import type { NextPage } from "next"
import styles from "../../styles/TablePage.module.css"
import { Classroom } from "../../interfaces"

const API_URL = "http://localhost:8100/api/v1/classrooms"

const ClassroomsPage: NextPage = () => {
  const [classrooms, setClassrooms] = useState<Classroom[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function fetchClassrooms() {
      try {
        setLoading(true)
        setError(null)
        const response = await fetch(API_URL)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        const data: Classroom[] = await response.json()
        setClassrooms(data)
      } catch (e: any) {
        setError(e.message)
        console.error("Failed to fetch classrooms:", e)
      } finally {
        setLoading(false)
      }
    }
    fetchClassrooms()
  }, [])

  if (loading) return <p>Loading classrooms...</p>
  if (error) return <p>Error loading classrooms: {error}</p>

  return (
    <div className={styles.container}>
      <h1>Classrooms</h1>
      <Link href="/classrooms/new" className={styles.addButton}>
        Add New Classroom
      </Link>
      {classrooms.length === 0 ? (
        <p>No classrooms found.</p>
      ) : (
        <table className={styles.table}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Room Number</th>
              <th>Capacity</th>
              <th>Projector</th>
              <th>Building</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {classrooms.map((room) => (
              <tr key={room.roomId}>
                <td>{room.roomId}</td>
                <td>{room.roomNumber}</td>
                <td>{room.capacity}</td>
                <td>{room.hasProjector ? "Yes" : "No"}</td>
                <td>{room.building || "N/A"}</td>
                <td>
                  <Link
                    href={`/classrooms/${room.roomId}`}
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

export default ClassroomsPage
