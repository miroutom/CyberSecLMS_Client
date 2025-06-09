package hse.diploma.cybersecplatform.mock

import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType

val mockXssCourse =
    Course(
        id = 1,
        title = "XSS Attacks",
        description = "Изучите различные типы XSS уязвимостей",
        vulnerabilityType = VulnerabilityType.XSS.name,
        difficultyLevel = "medium",
        category = "web",
        tasks = mockTasks.filter { it.courseId == 1 },
    )

val mockSqlCourse =
    Course(
        id = 2,
        title = "SQL Injection",
        description = "Научитесь эксплуатировать SQL уязвимости",
        vulnerabilityType = VulnerabilityType.SQL.name,
        difficultyLevel = "hard",
        category = "database",
        tasks = mockTasks.filter { it.courseId == 2 },
    )

val mockCsrfCourse =
    Course(
        id = 3,
        title = "CSRF Attacks",
        description = "Понимание и эксплуатация CSRF уязвимостей",
        vulnerabilityType = VulnerabilityType.CSRF.name,
        difficultyLevel = "medium",
        category = "web",
        tasks = mockTasks.filter { it.courseId == 3 },
    )

val mockSqlCourse2 =
    Course(
        id = 4,
        title = "SQL Injection",
        description = "Научитесь эксплуатировать SQL уязвимости",
        vulnerabilityType = VulnerabilityType.SQL.name,
        difficultyLevel = "medium",
        category = "database",
        tasks = mockTasks.filter { it.courseId == 4 },
    )

val mockAllCourses =
    listOf(
        mockXssCourse,
        mockSqlCourse,
        mockCsrfCourse,
        mockSqlCourse2,
    )

val mockCourses =
    listOf(
        mockXssCourse,
        mockSqlCourse,
        mockCsrfCourse,
        mockSqlCourse2,
    )
