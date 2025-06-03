import tasks from "@/tasks"

describe("Tasks", () => {
  it("should have correct structure for each task", () => {
    tasks.forEach((task) => {
      expect(task).toHaveProperty("id")
      expect(task).toHaveProperty("title")
      expect(task).toHaveProperty("vulnerability")
      expect(task).toHaveProperty("description")
      expect(task).toHaveProperty("path")
      expect(task).toHaveProperty("language")
    })
  })

  it("should have unique IDs", () => {
    const ids = tasks.map((task) => task.id)
    const uniqueIds = new Set(ids)
    expect(ids.length).toBe(uniqueIds.size)
  })

  it("should have valid vulnerability types", () => {
    const validVulnerabilities = ["XSS", "CSRF"]
    tasks.forEach((task) => {
      expect(validVulnerabilities).toContain(task.vulnerability)
    })
  })

  it("should have valid language types", () => {
    const validLanguages = ["html", "javascript"]
    tasks.forEach((task) => {
      expect(validLanguages).toContain(task.language)
    })
  })
})
