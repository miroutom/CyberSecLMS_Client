package hse.diploma.cybersecplatform.mock

import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.domain.model.VulnerabilityType

private val mockSqlCourse =
    Course(
        vulnerabilityType = VulnerabilityType.SQL,
        tasksCount = 25,
    )

private val mockXssCourse =
    Course(
        vulnerabilityType = VulnerabilityType.XSS,
        tasksCount = 10,
    )

private val mockCsrfCourse =
    Course(
        vulnerabilityType = VulnerabilityType.CSRF,
        tasksCount = 15,
    )

val mockAllCourses =
    listOf(
        mockSqlCourse,
        mockXssCourse,
        mockCsrfCourse,
        mockXssCourse,
        mockSqlCourse,
        mockCsrfCourse,
        mockCsrfCourse,
        mockXssCourse,
        mockSqlCourse,
        mockCsrfCourse,
        mockSqlCourse,
    )

val mockCourses =
    listOf(
        Course(
            vulnerabilityType = VulnerabilityType.XSS,
            tasksCount = 10,
        ),
        Course(
            vulnerabilityType = VulnerabilityType.SQL,
            tasksCount = 10,
        ),
        Course(
            vulnerabilityType = VulnerabilityType.CSRF,
            tasksCount = 10,
        ),
        Course(
            vulnerabilityType = VulnerabilityType.XSS,
            completedTasks = 1,
            tasksCount = 10,
        ),
        Course(
            vulnerabilityType = VulnerabilityType.XSS,
            completedTasks = 3,
            tasksCount = 10,
        ),
        Course(
            vulnerabilityType = VulnerabilityType.XSS,
            completedTasks = 9,
            tasksCount = 10,
        ),
        Course(
            vulnerabilityType = VulnerabilityType.CSRF,
            completedTasks = 7,
            tasksCount = 10,
        ),
        Course(
            vulnerabilityType = VulnerabilityType.CSRF,
            completedTasks = 2,
            tasksCount = 10,
        ),
        Course(
            vulnerabilityType = VulnerabilityType.SQL,
            completedTasks = 4,
            tasksCount = 10,
        ),
    )
