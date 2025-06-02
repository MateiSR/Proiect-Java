import Link from "next/link"
import styles from "./Navigation.module.css"

const Navigation: React.FC = () => {
  return (
    <nav className={styles.nav}>
      <Link href="/">Home</Link>
      <Link href="/students">Students</Link>
      <Link href="/professors">Professors</Link>
      <Link href="/classrooms">Classrooms</Link>
      <Link href="/courses">Courses</Link>
      <Link href="/schedules">Schedules</Link>
      <Link href="/enrollments">Enrollments</Link>
    </nav>
  )
}

export default Navigation
