package com.reactive.data.usecases

import androidx.paging.PagingData
import com.github.kittinunf.result.Result
import com.reactive.domain.model.Repo
import com.reactive.domain.model.User
import com.reactive.domain.repositories.GithubRepository
import com.reactive.domain.usecases.GetPagedKotlinReposUseCase
import com.reactive.domain.usecases.GetRepoUseCase
import com.reactive.domain.usecases.SearchReposUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GithubUseCasesTest {

    // RED: GetPagedKotlinReposUseCase delegates to repository and returns the same Flow
    @Test
    fun `GIVEN repository flow WHEN GetPagedKotlinReposUseCase invoked THEN returns same flow instance`() {
        // Given
        val repository = FakeGithubRepository()
        val expectedFlow = flowOf(PagingData.empty<Repo>())
        repository.kotlinReposFlow = expectedFlow
        val useCase = GetPagedKotlinReposUseCase(repository)

        // When
        val actualFlow = useCase()

        // Then
        assertSame(expectedFlow, actualFlow)
        assertEquals(1, repository.getKotlinReposCalls)
    }

    // RED: SearchReposUseCase passes query without modification
    @Test
    fun `GIVEN query WHEN SearchReposUseCase invoked THEN passes query to repository unchanged`() {
        // Given
        val repository = FakeGithubRepository()
        val expectedFlow = flowOf(PagingData.empty<Repo>())
        repository.searchReposFlow = expectedFlow
        val useCase = SearchReposUseCase(repository)

        // When
        val actualFlow = useCase("compose")

        // Then
        assertSame(expectedFlow, actualFlow)
        assertEquals("compose", repository.lastSearchQuery)
    }

    // RED: GetRepoUseCase passes owner and name, returns same Result
    @Test
    fun `GIVEN owner and name WHEN GetRepoUseCase invoked THEN delegates and returns same result`() = runBlocking {
        // Given
        val repository = FakeGithubRepository()
        val expectedResult = successResult(sampleRepo())
        repository.repoResult = expectedResult
        val useCase = GetRepoUseCase(repository)

        // When
        val actualResult = useCase(owner = "reactive", name = "compose-hub")

        // Then
        assertSame(expectedResult, actualResult)
        assertEquals("reactive", repository.lastOwner)
        assertEquals("compose-hub", repository.lastName)
    }

    private class FakeGithubRepository : GithubRepository {
        var kotlinReposFlow: Flow<PagingData<Repo>> = flowOf(PagingData.empty())
        var searchReposFlow: Flow<PagingData<Repo>> = flowOf(PagingData.empty())
        var repoResult: Result<Repo, Exception> = successResult(sampleRepo())

        var getKotlinReposCalls = 0
        var lastSearchQuery: String? = null
        var lastOwner: String? = null
        var lastName: String? = null

        override fun getKotlinRepos(): Flow<PagingData<Repo>> {
            getKotlinReposCalls += 1
            return kotlinReposFlow
        }

        override fun searchKotlinRepos(query: String): Flow<PagingData<Repo>> {
            lastSearchQuery = query
            return searchReposFlow
        }

        override suspend fun getKotlinRepo(owner: String, name: String): Result<Repo, Exception> {
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
    owner = User(login = "reactive", id = 7, avatarUrl = "avatar"),
    description = "Compose sample",
    url = "https://github.com/reactive/compose-hub",
    homepage = "https://reactive.dev",
    stars = 42,
    language = "Kotlin",
    forks = 5,
)

private fun successResult(repo: Repo): Result<Repo, Exception> = Result.of { repo }
