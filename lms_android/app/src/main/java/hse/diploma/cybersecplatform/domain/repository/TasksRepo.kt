package hse.diploma.cybersecplatform.domain.repository

import hse.diploma.cybersecplatform.data.model.response.TaskSubmissionResponse
import hse.diploma.cybersecplatform.domain.model.Task

interface TasksRepo {
    suspend fun getTaskById(
        courseId: Int,
        taskId: Int,
    ): Result<Task>

    suspend fun markTaskAsCompleted(taskId: Int): Result<Unit>

    suspend fun submitTaskSolution(
        task: Task,
        code: String,
    ): Result<TaskSubmissionResponse>
}
