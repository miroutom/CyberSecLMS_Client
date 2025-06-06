describe("Tasks Page", () => {
  beforeEach(() => {
    cy.visit("/")
  })

  it("should display list of tasks", () => {
    cy.get('[data-test="task-list"]').should("exist")
    cy.get('[data-test="task-item"]').should("have.length.at.least", 1)
  })

  it("should display task details correctly", () => {
    cy.get('[data-test="task-item"]')
      .first()
      .within(() => {
        cy.get('[data-test="task-title"]').should("exist")
        cy.get('[data-test="task-vulnerability"]').should("exist")
        cy.get('[data-test="task-description"]').should("exist")
      })
  })

  it("should navigate to task details when clicked", () => {
    cy.get('[data-test="task-item"]').first().click()
    cy.url().should("include", "/task/")
  })

  it("should filter tasks by vulnerability type", () => {
    cy.get('[data-test="vulnerability-filter"]').select("XSS")
    cy.get('[data-test="task-item"]').each(($el) => {
      cy.wrap($el)
        .find('[data-test="task-vulnerability"]')
        .should("contain", "XSS")
    })
  })
})
