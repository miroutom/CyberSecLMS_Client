//import axios from "axios"

// Mock data for tasks
const MOCK_TASKS = [
  {
    id: "1",
    title: "Задание 1: XSS",
    vulnerability: "XSS",
    description: "Сохранение XSS в HTML-контексте без кодирования",
    path: "/vulnerable-app/xss.html",
    language: "html",
  },
  {
    id: "2",
    title: "Задание 2: XSS",
    vulnerability: "XSS",
    description: "Сохранение XSS в HTML-контексте без кодирования",
    path: "/vulnerable-app/xss.html",
    language: "html",
  },
  {
    id: "3",
    title: "Задание 3: XSS",
    vulnerability: "XSS",
    description: "Сохранение XSS в HTML-контексте без кодирования",
    path: "/vulnerable-app/xss.html",
    language: "html",
  },
  {
    id: "4",
    title: "Задание 2: CSRF",
    vulnerability: "CSRF",
    description: "CSRF, где проверка токена зависит от наличия токена",
    path: "/vulnerable-app/csrf.html",
    language: "javascript",
  },
]

export const taskService = {
  async getTasksByVulnerability(vulnerability) {
    try {
      // Simulate API delay
      await new Promise((resolve) => setTimeout(resolve, 500))

      // Filter tasks by vulnerability
      const tasks = MOCK_TASKS.filter(
        (task) => task.vulnerability === vulnerability
      )

      // Return mock response
      return {
        success: true,
        tasks,
      }

      // Real API call (commented out)
      /*
      const response = await axios.get(`${API_URL}/tasks/${vulnerability}`);
      return response.data;
      */
    } catch (error) {
      throw error.response?.data || { error: "Failed to fetch tasks" }
    }
  },

  async getAllTasks() {
    try {
      // Simulate API delay
      await new Promise((resolve) => setTimeout(resolve, 500))

      // Return all mock tasks
      return {
        success: true,
        tasks: MOCK_TASKS,
      }

      // Real API call (commented out)
      /*
      const response = await axios.get(`${API_URL}/tasks`);
      return response.data;
      */
    } catch (error) {
      throw error.response?.data || { error: "Failed to fetch all tasks" }
    }
  },
}
