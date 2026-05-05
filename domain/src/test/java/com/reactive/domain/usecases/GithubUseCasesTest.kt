package com.reactive.domain.usecases

import androidx.paging.PagingData
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.onFailure
import com.github.kittinunf.result.onSuccess
import com.reactive.domain.model.Repo
import com.reactive.domain.model.User
import com.reactive.domain.repositories.GithubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class GithubUseCasesTest {

    @Test
    fun `GetPagedKotlinReposUseCase returns repository flow`() {
        val repository = FakeGithubRepository()
        val expectedFlow = flowOf(PagingData.empty<Repo>())
        repository.kotlinReposFlow = expectedFlow

        val useCase = GetPagedKotlinReposUseCase(repository)

        val actualFlow = useCase()

        assertSame(expectedFlow, actualFlow)
        assertEquals(1, repository.getKotlinReposCalls)
    }

    @Test
    fun `SearchReposUseCase forwards query to repository`() {
        val repository = FakeGithubRepository()
        val expectedFlow = flowOf(PagingData.empty<Repo>())
        repository.searchReposFlow = expectedFlow
        val useCase = SearchReposUseCase(repository)

        val actualFlow = useCase("compose")

        assertSame(expectedFlow, actualFlow)
        assertEquals("compose", repository.lastSearchQuery)
    }

    @Test
    fun `GetRepoUseCase forwards owner and name to repository`() = runBlocking {
        val repository = FakeGithubRepository()
        val expectedResult = successResult(sampleRepo())
        repository.repoResult = expectedResult
        val useCase = GetRepoUseCase(repository)

        val actualResult = useCase(owner = "reactive", name = "compose-hub")

        var actualRepo : Repo? = null
        actualResult
            .onSuccess { actualRepo = it }
            .onFailure { fail("Expected success but received failure") }

        assertEquals(sampleRepo(), actualRepo)
        assertEquals("reactive", repository.lastOwner)
        assertEquals("compose-hub", repository.lastName)
    }

    private class FakeGithubRepository : GithubRepository {
        var kotlinReposFlow : Flow<PagingData<Repo>> = flowOf(PagingData.empty())
        var searchReposFlow : Flow<PagingData<Repo>> = flowOf(PagingData.empty())
        var repoResult : Result<Repo, Exception> = Result.of { sampleRepo() }

        var getKotlinReposCalls = 0
        var lastSearchQuery : String? = null
        var lastOwner : String? = null
        var lastName : String? = null

        override fun getKotlinRepos() : Flow<PagingData<Repo>> {
            getKotlinReposCalls += 1
            return kotlinReposFlow
        }

        override fun searchKotlinRepos(query : String) : Flow<PagingData<Repo>> {
            lastSearchQuery = query
            return searchReposFlow
        }

        override suspend fun getKotlinRepo(owner : String, name : String) : Result<Repo, Exception> {
            lastOwner = owner
            lastName = name
            return repoResult
        }
    }
}

private fun sampleRepo() = Repo(
    id = 1,
    name = "compose-hub",
    fullName = "reactive/compose-hub",
    owner = User(login = "reactive", id = 101, avatarUrl = "avatar"),
    description = "Compose sample",
    url = "https://github.com/reactive/compose-hub",
    homepage = "https://reactive.dev",
    stars = 120,
    language = "Kotlin",
    forks = 12,
)

private fun successResult(repo : Repo) : Result<Repo, Exception> = Result.of { repo }

