@file:Suppress("DEPRECATION")

package com.reactive.composerepos.ui.screens.search

import com.reactive.composerepos.FakeGithubRepository
import com.reactive.domain.usecases.SearchReposUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

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
    fun `onSearchUpdated updates query immediately`() = dispatcher.runBlockingTest {
        val viewModel = SearchViewModel(
            searchReposUseCase = SearchReposUseCase(FakeGithubRepository()),
            debounceMs = 300L,
        )

        viewModel.onSearchUpdated("compose")

        assertEquals(SearchState(query = "compose", isDebouncing = false), viewModel.state.value)
    }

    @Test
    fun `searchResult triggers initial empty query and debounced user query`() = dispatcher.runBlockingTest {
        val repository = FakeGithubRepository()
        val viewModel = SearchViewModel(
            searchReposUseCase = SearchReposUseCase(repository),
            debounceMs = 300L,
        )

        val job = launch { viewModel.searchResult.collect {} }

        advanceUntilIdle()
        assertEquals(listOf(""), repository.searchQueries)

        viewModel.onSearchUpdated("compose")
        dispatcher.advanceTimeBy(299)
        assertEquals(listOf(""), repository.searchQueries)

        dispatcher.advanceTimeBy(1)
        advanceUntilIdle()

        assertEquals(listOf("", "compose"), repository.searchQueries)
        assertFalse(viewModel.state.value.isDebouncing)
        assertEquals("compose", viewModel.state.value.query)

        job.cancel()
    }

    @Test
    fun `searchResult ignores duplicate consecutive queries`() = dispatcher.runBlockingTest {
        val repository = FakeGithubRepository()
        val viewModel = SearchViewModel(
            searchReposUseCase = SearchReposUseCase(repository),
            debounceMs = 300L,
        )

        val job = launch { viewModel.searchResult.collect {} }

        advanceUntilIdle()
        viewModel.onSearchUpdated("compose")
        dispatcher.advanceTimeBy(300)
        advanceUntilIdle()

        viewModel.onSearchUpdated("compose")
        dispatcher.advanceTimeBy(300)
        advanceUntilIdle()

        assertEquals(listOf("", "compose"), repository.searchQueries)

        job.cancel()
    }

    @Test
    fun `searchResult emits only latest query after rapid updates`() = dispatcher.runBlockingTest {
        val repository = FakeGithubRepository()
        val viewModel = SearchViewModel(
            searchReposUseCase = SearchReposUseCase(repository),
            debounceMs = 300L,
        )

        val job = launch { viewModel.searchResult.collect {} }

        advanceUntilIdle()
        viewModel.onSearchUpdated("compose")
        dispatcher.advanceTimeBy(150)
        viewModel.onSearchUpdated("compose hub")
        dispatcher.advanceTimeBy(299)
        assertEquals(listOf(""), repository.searchQueries)

        dispatcher.advanceTimeBy(1)
        advanceUntilIdle()

        assertEquals(listOf("", "compose hub"), repository.searchQueries)
        assertEquals("compose hub", viewModel.state.value.query)

        job.cancel()
    }
}

