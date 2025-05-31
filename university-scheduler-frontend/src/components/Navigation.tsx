import Link from "next/link"
import styles from "./Navigation.module.css"

const Navigation: React.FC = () => {
  return (
    <nav className={styles.nav}>
      <Link href="/">Home</Link>
      <Link href="/students">Students</Link>
      <Link href="/professors">Professors</Link>
    </nav>
  )
}

export default Navigation
