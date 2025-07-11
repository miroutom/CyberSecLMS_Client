package hse.diploma.cybersecplatform.di

import dagger.Binds
import dagger.Module
import hse.diploma.cybersecplatform.data.repository.AuthRepoImpl
import hse.diploma.cybersecplatform.data.repository.CoursesRepoImpl
import hse.diploma.cybersecplatform.data.repository.SettingsRepoImpl
import hse.diploma.cybersecplatform.data.repository.TasksRepoImpl
import hse.diploma.cybersecplatform.data.repository.UserRepoImpl
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import hse.diploma.cybersecplatform.domain.repository.CoursesRepo
import hse.diploma.cybersecplatform.domain.repository.SettingsRepo
import hse.diploma.cybersecplatform.domain.repository.TasksRepo
import hse.diploma.cybersecplatform.domain.repository.UserRepo

@Module
interface RepoModule {
    @Binds
    fun bindAuthRepo(authRepo: AuthRepoImpl): AuthRepo

    @Binds
    fun bindUserRepo(userRepo: UserRepoImpl): UserRepo

    @Binds
    fun bindCoursesRepo(coursesRepo: CoursesRepoImpl): CoursesRepo

    @Binds
    fun bindSettingsRepo(settingsRepo: SettingsRepoImpl): SettingsRepo

    @Binds
    fun bindTasksRepo(tasksRepo: TasksRepoImpl): TasksRepo
}
