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

    @Test
    fun `init loads repository details successfully`() = dispatcher.runBlockingTest {
        val repository = FakeGithubRepository().apply {
            repoResult = successResult(sampleRepo())
        }

        val viewModel = DetailsViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    "owner" to "reactive",
                    "name" to "compose-hub",
                ),
            ),
            getRepoUseCase = GetRepoUseCase(repository),
        )
        advanceUntilIdle()

        assertEquals(
            DetailsScreenState(
                repo = sampleRepo(),
                screenStatus = ScreenStatus.SUCCESS,
            ),
            viewModel.state.value,
        )
        assertEquals("reactive", repository.lastOwner)
        assertEquals("compose-hub", repository.lastName)
    }

    @Test
    fun `init exposes no internet status when repository loading fails offline`() = dispatcher.runBlockingTest {
        val repository = FakeGithubRepository().apply {
            repoResult = failureResult(NoInternetConnectionException())
        }

        val viewModel = DetailsViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    "owner" to "reactive",
                    "name" to "compose-hub",
                ),
            ),
            getRepoUseCase = GetRepoUseCase(repository),
        )
        advanceUntilIdle()

        assertNull(viewModel.state.value.repo)
        assertEquals(ScreenStatus.NO_INTERNET, viewModel.state.value.screenStatus)
    }

    @Test
    fun `init exposes generic error status when repository loading fails unexpectedly`() = dispatcher.runBlockingTest {
        val repository = FakeGithubRepository().apply {
            repoResult = failureResult(IllegalStateException("boom"))
        }

        val viewModel = DetailsViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    "owner" to "reactive",
                    "name" to "compose-hub",
                ),
            ),
            getRepoUseCase = GetRepoUseCase(repository),
        )
        advanceUntilIdle()

        assertNull(viewModel.state.value.repo)
        assertEquals(ScreenStatus.ERROR, viewModel.state.value.screenStatus)
    }
}

