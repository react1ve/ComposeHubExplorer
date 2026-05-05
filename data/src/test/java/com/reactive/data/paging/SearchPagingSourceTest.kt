package com.reactive.data.paging

import androidx.paging.PagingSource
import com.reactive.data.remote.RemoteConstants
import com.reactive.data.remote.datasource.GithubDataSource
import com.reactive.data.sampleRepo
import com.reactive.domain.model.Repo
import com.reactive.domain.model.ResultList
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchPagingSourceTest {

    private val dataSource = mockk<GithubDataSource>()

    // --- RED: empty query should return empty page without calling API ---

    @Test
    fun `load with empty query returns empty page without calling datasource`() = runTest {
        val pagingSource = SearchPagingSource(dataSource, query = "")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 30,
                placeholdersEnabled = false,
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(emptyList<Repo>(), page.data)
        assertNull(page.prevKey)
        assertNull(page.nextKey)
        coVerify(exactly = 0) { dataSource.searchRepositories(any(), any()) }
    }

    // --- RED: successful first page load ---

    @Test
    fun `load first page returns data with correct keys`() = runTest {
        val repos = listOf(sampleRepo(), sampleRepo().copy(id = 2, name = "second"))
        coEvery { dataSource.searchRepositories(RemoteConstants.FIRST_PAGE, "compose") } returns
                ResultList(totalCount = 100, items = repos)
        every { dataSource.isLastPage(RemoteConstants.FIRST_PAGE, 100) } returns false

        val pagingSource = SearchPagingSource(dataSource, query = "compose")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 30,
                placeholdersEnabled = false,
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(repos, page.data)
        assertNull(page.prevKey)
        assertEquals(RemoteConstants.FIRST_PAGE + 1, page.nextKey)
    }

    // --- RED: middle page has both prev and next keys ---

    @Test
    fun `load middle page has previous and next keys`() = runTest {
        val repos = listOf(sampleRepo())
        coEvery { dataSource.searchRepositories(2, "compose") } returns
                ResultList(totalCount = 200, items = repos)
        every { dataSource.isLastPage(2, 200) } returns false

        val pagingSource = SearchPagingSource(dataSource, query = "compose")

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 2,
                loadSize = 30,
                placeholdersEnabled = false,
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(repos, page.data)
        assertEquals(1, page.prevKey)
        assertEquals(3, page.nextKey)
    }

    // --- RED: last page has no next key ---

    @Test
    fun `load last page has no next key`() = runTest {
        val repos = listOf(sampleRepo())
        coEvery { dataSource.searchRepositories(5, "compose") } returns
                ResultList(totalCount = 150, items = repos)
        every { dataSource.isLastPage(5, 150) } returns true

        val pagingSource = SearchPagingSource(dataSource, query = "compose")

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 5,
                loadSize = 30,
                placeholdersEnabled = false,
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(repos, page.data)
        assertEquals(4, page.prevKey)
        assertNull(page.nextKey)
    }

    // --- RED: empty response returns empty page ---

    @Test
    fun `load returns empty page when response items are empty`() = runTest {
        coEvery { dataSource.searchRepositories(RemoteConstants.FIRST_PAGE, "noresults") } returns
                ResultList(totalCount = 0, items = emptyList())

        val pagingSource = SearchPagingSource(dataSource, query = "noresults")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 30,
                placeholdersEnabled = false,
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(emptyList<Repo>(), page.data)
        assertNull(page.prevKey)
        assertNull(page.nextKey)
    }

    // --- RED: exception returns LoadResult.Error ---

    @Test
    fun `load returns error when datasource throws exception`() = runTest {
        val exception = RuntimeException("network failure")
        coEvery { dataSource.searchRepositories(RemoteConstants.FIRST_PAGE, "compose") } throws exception

        val pagingSource = SearchPagingSource(dataSource, query = "compose")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 30,
                placeholdersEnabled = false,
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(exception, (result as PagingSource.LoadResult.Error).throwable)
    }

    // --- RED: getRefreshKey returns anchorPosition ---

    @Test
    fun `getRefreshKey returns anchor position from paging state`() {
        val pagingSource = SearchPagingSource(dataSource, query = "compose")
        val state = mockk<androidx.paging.PagingState<Int, Repo>>()
        every { state.anchorPosition } returns 5

        assertEquals(5, pagingSource.getRefreshKey(state))
    }

    @Test
    fun `getRefreshKey returns null when anchor position is null`() {
        val pagingSource = SearchPagingSource(dataSource, query = "compose")
        val state = mockk<androidx.paging.PagingState<Int, Repo>>()
        every { state.anchorPosition } returns null

        assertNull(pagingSource.getRefreshKey(state))
    }
}

