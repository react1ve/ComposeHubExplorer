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

    // region GetPagedKotlinReposUseCase

    // RED: UseCase should return the same Flow instance from repository
    @Test
    fun `GIVEN repository with flow WHEN GetPagedKotlinReposUseCase invoked THEN returns same flow`() {
        // Given
        val repository = FakeGithubRepository()
        val expectedFlow = flowOf(PagingData.empty<Repo>())
        repository.kotlinReposFlow = expectedFlow
        val useCase = GetPagedKotlinReposUseCase(repository)

        // When
        val actualFlow = useCase()

        // Then
        assertSame(expectedFlow, actualFlow)
    }

    // RED: UseCase should call repository exactly once
    @Test
    fun `GIVEN repository WHEN GetPagedKotlinReposUseCase invoked THEN calls repository exactly once`() {
        // Given
        val repository = FakeGithubRepository()
        val useCase = GetPagedKotlinReposUseCase(repository)

        // When
        useCase()

        // Then
        assertEquals(1, repository.getKotlinReposCalls)
    }

    // endregion

    // region SearchReposUseCase

    // RED: UseCase should forward query unchanged
    @Test
    fun `GIVEN query WHEN SearchReposUseCase invoked THEN forwards query to repository`() {
        // Given
        val repository = FakeGithubRepository()
        val useCase = SearchReposUseCase(repository)

        // When
        useCase("compose")

        // Then
        assertEquals("compose", repository.lastSearchQuery)
    }

    // RED: UseCase should return Flow from repository
    @Test
    fun `GIVEN repository with flow WHEN SearchReposUseCase invoked THEN returns same flow`() {
        // Given
        val repository = FakeGithubRepository()
        val expectedFlow = flowOf(PagingData.empty<Repo>())
        repository.searchReposFlow = expectedFlow
        val useCase = SearchReposUseCase(repository)

        // When
        val actualFlow = useCase("compose")

        // Then
        assertSame(expectedFlow, actualFlow)
    }

    // endregion

    // region GetRepoUseCase

    // RED: UseCase should forward owner and name to repository
    @Test
    fun `GIVEN owner and name WHEN GetRepoUseCase invoked THEN forwards them to repository`() = runBlocking {
        // Given
        val repository = FakeGithubRepository()
        repository.repoResult = successResult(sampleRepo())
        val useCase = GetRepoUseCase(repository)

        // When
        useCase(owner = "reactive", name = "compose-hub")

        // Then
        assertEquals("reactive", repository.lastOwner)
        assertEquals("compose-hub", repository.lastName)
    }

    // RED: UseCase should return success Result with repo
    @Test
    fun `GIVEN successful repository WHEN GetRepoUseCase invoked THEN returns success with repo`() = runBlocking {
        // Given
        val repository = FakeGithubRepository()
        val expectedRepo = sampleRepo()
        repository.repoResult = successResult(expectedRepo)
        val useCase = GetRepoUseCase(repository)

        // When
        val result = useCase(owner = "reactive", name = "compose-hub")

        // Then
        var actualRepo: Repo? = null
        result
            .onSuccess { actualRepo = it }
            .onFailure { fail("Expected success but received failure") }
        assertEquals(expectedRepo, actualRepo)
    }

    // RED: UseCase should propagate failure Result from repository
    @Test
    fun `GIVEN failing repository WHEN GetRepoUseCase invoked THEN returns failure result`() {
        runBlocking {
            // Given
            val repository = FakeGithubRepository()
            repository.repoResult = failureResult(IllegalStateException("boom"))
            val useCase = GetRepoUseCase(repository)

            // When
            val result = useCase(owner = "reactive", name = "compose-hub")

            // Then
            result
                .onSuccess { fail("Expected failure but received success") }
                .onFailure { assertEquals("boom", it.message) }
        }
    }

    // endregion

    private class FakeGithubRepository : GithubRepository {
        var kotlinReposFlow: Flow<PagingData<Repo>> = flowOf(PagingData.empty())
        var searchReposFlow: Flow<PagingData<Repo>> = flowOf(PagingData.empty())
        var repoResult: Result<Repo, Exception> = Result.of { sampleRepo() }

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
    owner = User(login = "reactive", id = 101, avatarUrl = "avatar"),
    description = "Compose sample",
    url = "https://github.com/reactive/compose-hub",
    homepage = "https://reactive.dev",
    stars = 120,
    language = "Kotlin",
    forks = 12,
)

private fun successResult(repo: Repo): Result<Repo, Exception> = Result.of { repo }
private fun failureResult(exception: Exception): Result<Repo, Exception> = Result.of { throw exception }
