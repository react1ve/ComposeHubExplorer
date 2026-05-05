package com.reactive.data.repositories

import com.github.kittinunf.result.onFailure
import com.github.kittinunf.result.onSuccess
import com.reactive.data.local.db.RepoDatabase
import com.reactive.data.local.mappers.RepoDboMapper
import com.reactive.data.remote.datasource.GithubDataSource
import com.reactive.data.sampleRepo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class GithubRepositoryImplTest {

    // RED: successful datasource response should be wrapped in Result.success
    @Test
    fun `GIVEN datasource returns repo WHEN getKotlinRepo THEN returns success result`() = runTest {
        // Given
        val dispatcher = StandardTestDispatcher(testScheduler)
        val dataSource = mockk<GithubDataSource>()
        val database = mockk<RepoDatabase>(relaxed = true)
        val mapper = mockk<RepoDboMapper>(relaxed = true)
        val expectedRepo = sampleRepo()
        coEvery { dataSource.getRepository("reactive", "compose-hub") } returns expectedRepo
        val repository = GithubRepositoryImpl(dataSource, dispatcher, database, mapper)

        // When
        val result = repository.getKotlinRepo(owner = "reactive", name = "compose-hub")
        advanceUntilIdle()

        // Then
        var actualRepo = sampleRepo().copy(id = -1)
        result
            .onSuccess { actualRepo = it }
            .onFailure { fail("Expected success but received failure: $it") }
        assertEquals(expectedRepo, actualRepo)
    }

    // RED: should delegate to datasource with correct arguments
    @Test
    fun `GIVEN owner and name WHEN getKotlinRepo THEN delegates to datasource with same args`() = runTest {
        // Given
        val dispatcher = StandardTestDispatcher(testScheduler)
        val dataSource = mockk<GithubDataSource>()
        val database = mockk<RepoDatabase>(relaxed = true)
        val mapper = mockk<RepoDboMapper>(relaxed = true)
        coEvery { dataSource.getRepository("reactive", "compose-hub") } returns sampleRepo()
        val repository = GithubRepositoryImpl(dataSource, dispatcher, database, mapper)

        // When
        repository.getKotlinRepo(owner = "reactive", name = "compose-hub")
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { dataSource.getRepository("reactive", "compose-hub") }
    }

    // RED: datasource exception should be wrapped in Result.failure
    @Test
    fun `GIVEN datasource throws WHEN getKotlinRepo THEN returns failure with same exception`() = runTest {
        // Given
        val dispatcher = StandardTestDispatcher(testScheduler)
        val dataSource = mockk<GithubDataSource>()
        val database = mockk<RepoDatabase>(relaxed = true)
        val mapper = mockk<RepoDboMapper>(relaxed = true)
        val expectedException = IllegalStateException("boom")
        coEvery { dataSource.getRepository("reactive", "compose-hub") } throws expectedException
        val repository = GithubRepositoryImpl(dataSource, dispatcher, database, mapper)

        // When
        val result = repository.getKotlinRepo(owner = "reactive", name = "compose-hub")
        advanceUntilIdle()

        // Then
        var actualException: Exception? = null
        result
            .onSuccess { fail("Expected failure but received success: $it") }
            .onFailure { actualException = it }
        assertSame(expectedException, actualException)
    }
}
