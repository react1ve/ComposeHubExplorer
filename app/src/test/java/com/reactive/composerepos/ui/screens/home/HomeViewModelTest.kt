@file:Suppress("DEPRECATION")

package com.reactive.composerepos.ui.screens.home

import androidx.paging.PagingData
import com.reactive.composerepos.FakeGithubRepository
import com.reactive.domain.model.Repo
import com.reactive.domain.usecases.GetPagedKotlinReposUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

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

    // RED: ViewModel should request data from UseCase on creation
    @Test
    fun `GIVEN repository WHEN HomeViewModel created THEN requests paged repos once`() = dispatcher.runBlockingTest {
        // ...existing code...
    }

    // RED: items should be non-null (Flow available for subscription)
    @Test
    fun `GIVEN repository WHEN HomeViewModel created THEN items flow is available`() = dispatcher.runBlockingTest {
        // Given
        val repository = FakeGithubRepository().apply {
            kotlinReposFlow = flowOf(PagingData.empty<Repo>())
        }

        // When
        val viewModel = HomeViewModel(GetPagedKotlinReposUseCase(repository))

        // Then
        assertNotNull(viewModel.items)
    }
}
