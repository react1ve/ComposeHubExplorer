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

    // region onSearchUpdated

    // RED: query should update immediately without waiting for debounce
    @Test
    fun `GIVEN viewModel WHEN onSearchUpdated called THEN state query updates immediately`() = dispatcher.runBlockingTest {
        // ...existing code...
    }

    // RED: isDebouncing should not be true before subscribing to searchResult
    @Test
    fun `GIVEN viewModel WHEN onSearchUpdated called THEN isDebouncing remains false without collector`() = dispatcher.runBlockingTest {
        // ...existing code...
    }

    // endregion

    // region Debounce behavior

    // RED: initial empty query should emit immediately (debounce 0 for "")
    @Test
    fun `GIVEN collector WHEN searchResult collected THEN initial empty query triggers search immediately`() = dispatcher.runBlockingTest {
        // ...existing code...
    }

    // RED: search should NOT fire before debounce elapses
    @Test
    fun `GIVEN query typed WHEN debounce not elapsed THEN search not triggered`() = dispatcher.runBlockingTest {
        // ...existing code...
    }

    // RED: after debounce, search should fire
    @Test
    fun `GIVEN query typed WHEN debounce elapses THEN search triggers with query`() = dispatcher.runBlockingTest {
        // ...existing code...
    }

    // endregion

    // region distinctUntilChanged

    // RED: duplicate consecutive query should NOT trigger another search
    @Test
    fun `GIVEN same query typed twice WHEN debounce elapses THEN search triggers only once`() = dispatcher.runBlockingTest {
        // ...existing code...
    }

    // endregion

    // region Rapid typing (flatMapLatest)

    // RED: during rapid input, only the last query should be emitted
    @Test
    fun `GIVEN rapid input WHEN debounce elapses THEN only last query triggers search`() = dispatcher.runBlockingTest {
        // Given
        val repository = FakeGithubRepository()
        val viewModel = createViewModel(repository)
        val job = launch { viewModel.searchResult.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onSearchUpdated("compose")
        dispatcher.advanceTimeBy(150)
        viewModel.onSearchUpdated("compose hub")
        dispatcher.advanceTimeBy(300)
        advanceUntilIdle()

        // Then
        assertEquals(listOf("", "compose hub"), repository.searchQueries)
        assertEquals("compose hub", viewModel.state.value.query)
        job.cancel()
    }

    // endregion

    private fun createViewModel(
        repository: FakeGithubRepository = FakeGithubRepository(),
    ) = SearchViewModel(
        searchReposUseCase = SearchReposUseCase(repository),
        debounceMs = 300L,
    )
}
