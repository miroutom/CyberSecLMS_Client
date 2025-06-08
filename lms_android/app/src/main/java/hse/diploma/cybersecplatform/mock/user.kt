package hse.diploma.cybersecplatform.mock

import hse.diploma.cybersecplatform.data.model.analytics.UserStatistics
import hse.diploma.cybersecplatform.data.model.user.CourseProgress
import hse.diploma.cybersecplatform.data.model.user.UserData

val mockUser =
    UserData(
        id = 1,
        username = "test_user",
        fullName = "Test User",
        email = "user@example.com",
        profileImage = null,
        isAdmin = false,
        isTeacher = false,
        isActive = true,
        lastLogin = "2023-05-15T10:30:00Z",
        courses =
            listOf(
                CourseProgress(
                    courseId = 1,
                    title = "SQL Injection",
                    completedTasks = 3,
                    tasksCount = 5,
                ),
                CourseProgress(
                    courseId = 2,
                    title = "XSS Attacks",
                    completedTasks = 1,
                    tasksCount = 5,
                ),
                CourseProgress(
                    courseId = 3,
                    title = "Authentication Bypass",
                    completedTasks = 0,
                    tasksCount = 5,
                ),
            ),
    )

val mockStats =
    UserStatistics(
        averageScore = 78.5,
        completedCourses = 1,
        completedTasks = 4,
        coursesProgress =
            listOf(
                UserStatistics.CourseProgress(
                    averageScore = 85.0,
                    completionPercentage = 60.0,
                    courseId = 1,
                    courseName = "SQL Injection",
                    lastActivity = "2023-05-15T09:30:00Z",
                ),
                UserStatistics.CourseProgress(
                    averageScore = 72.0,
                    completionPercentage = 20.0,
                    courseId = 2,
                    courseName = "XSS Attacks",
                    lastActivity = "2023-05-14T14:45:00Z",
                ),
                UserStatistics.CourseProgress(
                    averageScore = 0.0,
                    completionPercentage = 0.0,
                    courseId = 3,
                    courseName = "Authentication Bypass",
                    lastActivity = "2023-05-13T16:20:00Z",
                ),
                UserStatistics.CourseProgress(
                    averageScore = 100.0,
                    completionPercentage = 100.0,
                    courseId = 4,
                    courseName = "Authentication Bypass",
                    lastActivity = "2023-05-13T16:20:00Z",
                ),
            ),
        joinedDate = "2023-01-10",
        lastActive = "2023-05-15T10:30:00Z",
        totalCourses = 3,
        totalPoints = 420,
        totalTasks = 15,
        userId = 1,
    )
