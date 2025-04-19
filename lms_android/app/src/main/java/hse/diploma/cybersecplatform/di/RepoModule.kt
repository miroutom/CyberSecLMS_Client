package hse.diploma.cybersecplatform.di

import dagger.Binds
import dagger.Module
import hse.diploma.cybersecplatform.data.repo.AuthRepoImpl
import hse.diploma.cybersecplatform.data.repo.VulnerabilityRepoImpl
import hse.diploma.cybersecplatform.domain.AuthRepo
import hse.diploma.cybersecplatform.domain.VulnerabilityRepo
import javax.inject.Singleton

@Module
interface RepoModule {
    @Binds
    @Singleton
    fun bindVulnerabilityRepo(vulnerabilityRepo: VulnerabilityRepoImpl): VulnerabilityRepo

    @Binds
    @Singleton
    fun bindAuthRepo(authRepo: AuthRepoImpl): AuthRepo
}
