package hse.diploma.cybersecplatform.mock

import hse.diploma.cybersecplatform.domain.model.Difficulty
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.domain.model.VulnerabilityType

val mockTasksItems =
    listOf(
        // XSS tasks
        Task(
            vulnerabilityType = VulnerabilityType.XSS,
            number = 1,
            description = "Отраженная XSS атака с использованием параметров URL",
            difficulty = Difficulty.EASY,
        ),
        Task(
            vulnerabilityType = VulnerabilityType.XSS,
            number = 2,
            description = "Сохраненная XSS атака через форму комментариев",
            difficulty = Difficulty.MEDIUM,
        ),
        Task(
            vulnerabilityType = VulnerabilityType.XSS,
            number = 3,
            description = "DOM-based XSS с динамическим обновлением страницы",
            difficulty = Difficulty.HARD,
        ),
        Task(
            vulnerabilityType = VulnerabilityType.XSS,
            number = 4,
            description = "Обфускация XSS кода для обхода защитных систем",
            difficulty = Difficulty.MEDIUM,
        ),
        Task(
            vulnerabilityType = VulnerabilityType.XSS,
            number = 5,
            description = "XSS атака через загрузку SVG файлов",
            difficulty = Difficulty.HARD,
        ),
        // CSRF tasks
        Task(
            vulnerabilityType = VulnerabilityType.CSRF,
            number = 1,
            description = "CSRF на странице изменения пароля",
            difficulty = Difficulty.EASY,
        ),
        Task(
            vulnerabilityType = VulnerabilityType.CSRF,
            number = 2,
            description = "CSRF через скрытую форму атаки",
            difficulty = Difficulty.MEDIUM,
        ),
        Task(
            vulnerabilityType = VulnerabilityType.CSRF,
            number = 3,
            description = "CSRF атака на финансовую транзакцию",
            difficulty = Difficulty.HARD,
        ),
        Task(
            vulnerabilityType = VulnerabilityType.CSRF,
            number = 4,
            description = "Простой CSRF через GET запрос",
            difficulty = Difficulty.EASY,
        ),
        Task(
            vulnerabilityType = VulnerabilityType.CSRF,
            number = 5,
            description = "CSRF атака на API запросы",
            difficulty = Difficulty.HARD,
        ),
        // SQL Injection tasks
        Task(
            vulnerabilityType = VulnerabilityType.SQL,
            number = 1,
            description = "Basic SQL Injection через поле ввода логина",
            difficulty = Difficulty.EASY,
        ),
        Task(
            vulnerabilityType = VulnerabilityType.SQL,
            number = 2,
            description = "Blind SQL Injection через URL параметр",
            difficulty = Difficulty.MEDIUM,
        ),
        Task(
            vulnerabilityType = VulnerabilityType.SQL,
            number = 3,
            description = "Union-based SQL Injection для извлечения данных",
            difficulty = Difficulty.HARD,
        ),
        Task(
            vulnerabilityType = VulnerabilityType.SQL,
            number = 4,
            description = "Error-based SQL Injection для получения структуры базы данных",
            difficulty = Difficulty.MEDIUM,
        ),
        Task(
            vulnerabilityType = VulnerabilityType.SQL,
            number = 5,
            description = "Сложная SQL Injection атака на несколько баз данных",
            difficulty = Difficulty.HARD,
        ),
    )
