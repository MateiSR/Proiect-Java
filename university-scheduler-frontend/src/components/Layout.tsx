import React, { ReactNode } from "react"
import Navigation from "./Navigation"
import styles from "./Layout.module.css"

interface LayoutProps {
  children: ReactNode
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <>
      <Navigation />
      <main className={styles.container}>{children}</main>
    </>
  )
}

export default Layout
