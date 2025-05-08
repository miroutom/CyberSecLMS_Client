package hse.diploma.cybersecplatform.di

import dagger.Binds
import dagger.Module
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import hse.diploma.cybersecplatform.domain.repository.AuthRepoImpl
import hse.diploma.cybersecplatform.domain.repository.UserRepo
import hse.diploma.cybersecplatform.domain.repository.UserRepoImpl
import hse.diploma.cybersecplatform.domain.repository.VulnerabilityRepo
import hse.diploma.cybersecplatform.domain.repository.VulnerabilityRepoImpl
import javax.inject.Singleton

@Module
interface RepoModule {
    @Binds
    @Singleton
    fun bindVulnerabilityRepo(vulnerabilityRepo: VulnerabilityRepoImpl): VulnerabilityRepo

    @Binds
    fun bindAuthRepo(authRepo: AuthRepoImpl): AuthRepo

    @Binds
    fun bindUserRepo(userRepo: UserRepoImpl): UserRepo
}
