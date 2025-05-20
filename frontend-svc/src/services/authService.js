import axios from "axios"

//const API_URL = "http://localhost:8080/api"

// Add request interceptor to add token to all requests
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token")
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Add response interceptor to handle token expiration
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token")
      window.location.href = "/auth"
    }
    return Promise.reject(error)
  }
)

// Mock data for testing different scenarios
const MOCK_USERS = [
  {
    username: "testuser",
    password: "password123",
    email: "test@example.com",
    fullName: "Test User",
  },
  {
    username: "existinguser",
    password: "password123",
    email: "existing@example.com",
    fullName: "Existing User",
  },
]

export const authService = {
  async register(userData) {
    try {
      // Simulate API delay
      await new Promise((resolve) => setTimeout(resolve, 500))

      // Check if username already exists
      if (MOCK_USERS.some((user) => user.username === userData.username)) {
        throw {
          response: {
            data: {
              error: "Username already exists",
              code: "USERNAME_TAKEN",
            },
          },
        }
      }

      // Check if email already exists
      if (MOCK_USERS.some((user) => user.email === userData.email)) {
        throw {
          response: {
            data: {
              error: "Email already registered",
              code: "EMAIL_TAKEN",
            },
          },
        }
      }

      // Mock successful registration
      const mockResponse = {
        success: true,
        message: "User registered successfully",
        user: {
          id: Math.random().toString(36).substr(2, 9),
          username: userData.username,
          email: userData.email,
          fullName: userData.fullName,
        },
      }

      return mockResponse

      // Real API call (commented out)
      /*
      const response = await axios.post(`${API_URL}/register`, {
        username: userData.username,
        email: userData.email,
        fullName: userData.fullName,
        password: userData.password,
      });
      return response.data;
      */
    } catch (error) {
      throw error.response?.data || { error: "Registration failed" }
    }
  },

  async login(credentials) {
    try {
      // Simulate API delay
      await new Promise((resolve) => setTimeout(resolve, 500))

      // Find user in mock data
      const user = MOCK_USERS.find((u) => u.username === credentials.username)

      // Check if user exists and password matches
      if (!user || user.password !== credentials.password) {
        throw {
          response: {
            data: {
              error: "Invalid username or password",
              code: "INVALID_CREDENTIALS",
            },
          },
        }
      }

      // Mock successful login
      const mockResponse = {
        success: true,
        message: "Login successful",
        token: `mock-jwt-token-${Math.random().toString(36).substr(2, 9)}`,
        user: {
          id: Math.random().toString(36).substr(2, 9),
          username: user.username,
          email: user.email,
          fullName: user.fullName,
        },
      }

      // Store mock token
      localStorage.setItem("token", mockResponse.token)
      // Store username
      localStorage.setItem("username", user.username)

      return mockResponse

      // Real API call (commented out)
      /*
      const response = await axios.post(`${API_URL}/login`, {
        username: credentials.username,
        password: credentials.password,
      });
      return response.data;
      */
    } catch (error) {
      throw error.response?.data || { error: "Login failed" }
    }
  },

  logout() {
    localStorage.removeItem("token")
  },

  isAuthenticated() {
    return !!localStorage.getItem("token")
  },

  getToken() {
    return localStorage.getItem("token")
  },
}
