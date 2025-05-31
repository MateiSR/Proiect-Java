import type { NextPage } from "next"

const HomePage: NextPage = () => {
  return (
    <div>
      <h1>University Scheduler</h1>
      <div className="flex justify-center mb-4">
        <p>
          Welcome to the main page of the University Scheduler application. This
          app allows you to manage students and professors.
        </p>
        <p>Check the navbar to continue!</p>
      </div>
    </div>
  )
}

export default HomePage
