//import axios from "axios"

// Mock data for courses
const MOCK_COURSES = [
  {
    name: "Серверные",
    gradient: "linear-gradient(180deg, #F2F2FF 0%, #3764ED 100%)",
    titleColor: "linear-gradient(90deg, #3764ED 0%, #2C4FBC 50%, #1F3987 100%)",
    description:
      "Серверные уязвимости - внедрение вредоносных скриптов в приложение.",
    progress: 50,
    accentColor: "#3764ED",
  },
  {
    name: "CSRF",
    gradient: "linear-gradient(180deg, #F2F2FF 0%, #16B593 100%)",
    titleColor: "linear-gradient(90deg, #04916E 0%, #046E4C 50%, #004A2E 100%)",
    description: "CSRF уязвимости - подделка межсайтовых запросов.",
    progress: 15,
    accentColor: "#3AE8C5",
  },
  // {
  //   name: "SQL Injection",
  //   gradient: "linear-gradient(180deg, #FEF3E9 0%, #F9A866 100%)",
  //   titleColor: "linear-gradient(90deg, #F88F3A 0%, #DE7620 50%, #C6691D 100%)",
  //   description:
  //     "SQL Injection - внедрение вредоносного кода для манипуляции базой данных.",
  //   progress: 40,
  //   accentColor: "#F9A866",
  // },
]

// Mock data for all courses with task counts
const MOCK_ALL_COURSES = [
  {
    name: "Серверные",
    gradient: "linear-gradient(180deg, #F2F2FF 0%, #3764ED 100%)",
    titleColor: "linear-gradient(90deg, #3764ED 0%, #2C4FBC 50%, #1F3987 100%)",
    description:
      "Серверные уязвимости - внедрение вредоносных скриптов в приложение.",
    taskCount: 6,
    accentColor: "#3764ED",
  },
  {
    name: "CSRF",
    gradient: "linear-gradient(180deg, #F2F2FF 0%, #16B593 100%)",
    titleColor: "linear-gradient(90deg, #04916E 0%, #046E4C 50%, #004A2E 100%)",
    description: "CSRF уязвимости - подделка межсайтовых запросов.",
    taskCount: 15,
    accentColor: "#3AE8C5",
  },
  // {
  //   name: "SQL Injection",
  //   gradient: "linear-gradient(180deg, #FEF3E9 0%, #F9A866 100%)",
  //   titleColor: "linear-gradient(90deg, #F88F3A 0%, #DE7620 50%, #C6691D 100%)",
  //   description:
  //     "SQL Injection - внедрение вредоносного кода для манипуляции базой данных.",
  //   taskCount: 12,
  //   accentColor: "#F9A866",
  // },
]

export const courseService = {
  async getCourses() {
    try {
      // Simulate API delay
      await new Promise((resolve) => setTimeout(resolve, 500))

      // Return mock courses
      return {
        success: true,
        courses: MOCK_COURSES,
      }

      // Real API call (commented out)
      /*
      const response = await axios.get(`${API_URL}/courses`);
      return response.data;
      */
    } catch (error) {
      throw error.response?.data || { error: "Failed to fetch courses" }
    }
  },

  async getAllCourses() {
    try {
      // Simulate API delay
      await new Promise((resolve) => setTimeout(resolve, 500))

      // Return mock all courses
      return {
        success: true,
        courses: MOCK_ALL_COURSES,
      }

      // Real API call (commented out)
      /*
      const response = await axios.get(`${API_URL}/all-courses`);
      return response.data;
      */
    } catch (error) {
      throw error.response?.data || { error: "Failed to fetch all courses" }
    }
  },
}
