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

    @Test
    fun `getKotlinRepo returns success result when datasource succeeds`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val dataSource = mockk<GithubDataSource>()
        val database = mockk<RepoDatabase>(relaxed = true)
        val mapper = mockk<RepoDboMapper>(relaxed = true)
        val expectedRepo = sampleRepo()
        coEvery { dataSource.getRepository("reactive", "compose-hub") } returns expectedRepo
        val repository = GithubRepositoryImpl(dataSource, dispatcher, database, mapper)

        val result = repository.getKotlinRepo(owner = "reactive", name = "compose-hub")
        advanceUntilIdle()

        var actualRepo = sampleRepo().copy(id = -1)
        result
            .onSuccess { actualRepo = it }
            .onFailure { fail("Expected success result but received failure: $it") }

        assertEquals(expectedRepo, actualRepo)
        coVerify(exactly = 1) { dataSource.getRepository("reactive", "compose-hub") }
    }

    @Test
    fun `getKotlinRepo returns failure result when datasource throws`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val dataSource = mockk<GithubDataSource>()
        val database = mockk<RepoDatabase>(relaxed = true)
        val mapper = mockk<RepoDboMapper>(relaxed = true)
        val expectedException = IllegalStateException("boom")
        coEvery { dataSource.getRepository("reactive", "compose-hub") } throws expectedException
        val repository = GithubRepositoryImpl(dataSource, dispatcher, database, mapper)

        val result = repository.getKotlinRepo(owner = "reactive", name = "compose-hub")
        advanceUntilIdle()

        var actualException : Exception? = null
        result
            .onSuccess { fail("Expected failure result but received success: $it") }
            .onFailure { actualException = it }

        assertSame(expectedException, actualException)
        coVerify(exactly = 1) { dataSource.getRepository("reactive", "compose-hub") }
    }
}

