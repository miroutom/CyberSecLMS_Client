package hse.diploma.cybersecplatform.data.repository

import hse.diploma.cybersecplatform.data.api.ApiService
import hse.diploma.cybersecplatform.data.model.response.TaskSubmissionResponse
import hse.diploma.cybersecplatform.data.model.submission.TaskSubmission
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.domain.repository.TasksRepo
import java.util.Date
import javax.inject.Inject

class TasksRepoImpl @Inject constructor(
    private val apiService: ApiService,
) : TasksRepo {
    override suspend fun getTaskById(
        courseId: Int,
        taskId: Int,
    ): Result<Task> {
        return try {
            val response = apiService.getTaskById(courseId, taskId)
            if (response.isSuccessful) {
                response.body()?.let { task ->
                    Result.success(task)
                } ?: Result.failure(Exception("Task data is null"))
            } else {
                Result.failure(Exception("Failed to load task: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markTaskAsCompleted(taskId: Int): Result<Unit> {
        return try {
            val profileResponse = apiService.getUserProfile()
            val userId = profileResponse.body()?.id ?: throw Exception("User data is null")

            val response =
                apiService.completeTask(
                    userId = userId,
                    taskId = taskId,
                )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to complete task"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitTaskSolution(
        task: Task,
        code: String,
    ): Result<TaskSubmissionResponse> {
        return try {
            val profileResponse = apiService.getUserProfile()
            val userId = profileResponse.body()?.id ?: throw Exception("User data is null")

            val submission =
                TaskSubmission(
                    userId = userId,
                    taskId = task.id,
                    answer = code,
                    attachments = emptyList(),
                    status = "Completed",
                    submittedAt = Date(),
                    courseId = task.courseId,
                )

            val response =
                apiService.submitTask(
                    userId = userId,
                    taskId = task.id,
                    submission = submission,
                )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: run {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val error = response.errorBody()?.string() ?: "Failed to submit task"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
