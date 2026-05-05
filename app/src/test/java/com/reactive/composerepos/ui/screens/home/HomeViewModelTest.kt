@file:Suppress("DEPRECATION")

package com.reactive.composerepos.ui.screens.home

import com.reactive.composerepos.FakeGithubRepository
import com.reactive.domain.usecases.GetPagedKotlinReposUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
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

    @Test
    fun `init requests paged repos once`() = dispatcher.runBlockingTest {
        val repository = FakeGithubRepository()

        HomeViewModel(GetPagedKotlinReposUseCase(repository))

        assertEquals(1, repository.getKotlinReposCalls)
    }
}

