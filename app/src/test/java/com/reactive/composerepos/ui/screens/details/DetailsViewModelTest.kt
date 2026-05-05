@file:Suppress("DEPRECATION")

package com.reactive.composerepos.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import com.reactive.composerepos.FakeGithubRepository
import com.reactive.composerepos.failureResult
import com.reactive.composerepos.sampleRepo
import com.reactive.composerepos.successResult
import com.reactive.composerepos.ui.screens.ScreenStatus
import com.reactive.domain.model.NoInternetConnectionException
import com.reactive.domain.usecases.GetRepoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }

    // RED: on successful load, state should contain repo
    @Test
    fun `GIVEN successful repo load WHEN init THEN state contains repo with SUCCESS`() = dispatcher.runBlockingTest {
        // Given
        val repository = FakeGithubRepository().apply {
            repoResult = successResult(sampleRepo())
        }

        // When
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        // Then
        assertEquals(sampleRepo(), viewModel.state.value.repo)
        assertEquals(ScreenStatus.SUCCESS, viewModel.state.value.screenStatus)
    }

    // RED: should forward owner and name from SavedStateHandle to use case
    @Test
    fun `GIVEN savedStateHandle with owner and name WHEN init THEN delegates to repository`() = dispatcher.runBlockingTest {
        // Given
        val repository = FakeGithubRepository().apply {
            repoResult = successResult(sampleRepo())
        }

        // When
        createViewModel(repository)
        advanceUntilIdle()

        // Then
        assertEquals("reactive", repository.lastOwner)
        assertEquals("compose-hub", repository.lastName)
    }

    // RED: no internet should result in NO_INTERNET status and null repo
    @Test
    fun `GIVEN no internet WHEN init THEN state has NO_INTERNET and null repo`() = dispatcher.runBlockingTest {
        // Given
        val repository = FakeGithubRepository().apply {
            repoResult = failureResult(NoInternetConnectionException())
        }

        // When
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        // Then
        assertNull(viewModel.state.value.repo)
        assertEquals(ScreenStatus.NO_INTERNET, viewModel.state.value.screenStatus)
    }

    // RED: unexpected error should result in ERROR status and null repo
    @Test
    fun `GIVEN unexpected error WHEN init THEN state has ERROR and null repo`() = dispatcher.runBlockingTest {
        // Given
        val repository = FakeGithubRepository().apply {
            repoResult = failureResult(IllegalStateException("boom"))
        }

        // When
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        // Then
        assertNull(viewModel.state.value.repo)
        assertEquals(ScreenStatus.ERROR, viewModel.state.value.screenStatus)
    }

    private fun createViewModel(repository: FakeGithubRepository) = DetailsViewModel(
        savedStateHandle = SavedStateHandle(
            mapOf("owner" to "reactive", "name" to "compose-hub"),
        ),
        getRepoUseCase = GetRepoUseCase(repository),
    )
}
