package com.reactive.data.remote.datasource

import com.reactive.data.remote.RepositoryCache
import com.reactive.data.remote.model.RepoDto
import com.reactive.data.remote.model.ResultListDto
import com.reactive.data.remote.model.mappers.RepoDtoMapper
import com.reactive.data.remote.model.mappers.RepoResultListDtoMapper
import com.reactive.data.remote.model.mappers.UserDtoMapper
import com.reactive.data.remote.services.SearchServiceApi
import com.reactive.data.sampleRepo
import com.reactive.data.sampleRepoDto
import com.reactive.data.sampleResultList
import com.reactive.data.sampleResultListDto
import com.reactive.domain.model.NoInternetConnectionException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.net.UnknownHostException

class GithubDataSourceTest {

    private val service = FakeSearchServiceApi()
    private val repoDtoMapper = RepoDtoMapper(UserDtoMapper())
    private val resultDtoMapper = RepoResultListDtoMapper(repoDtoMapper)
    private val cache = RepositoryCache()
    private val dataSource = GithubDataSource(service, repoDtoMapper, resultDtoMapper, cache)

    // region searchRepositories

    // RED: query "compose" should reach API as "compose+language:kotlin"
    @Test
    fun `GIVEN query WHEN searchRepositories THEN appends kotlin language filter`() = runTest {
        // Given
        service.searchResponse = sampleResultListDto()

        // When
        dataSource.searchRepositories(page = 2, query = "compose")

        // Then
        assertEquals("compose+language:kotlin", service.lastSearchQuery)
    }

    // RED: page and sortBy params should be forwarded correctly
    @Test
    fun `GIVEN page WHEN searchRepositories THEN passes page and stars sort to API`() = runTest {
        // Given
        service.searchResponse = sampleResultListDto()

        // When
        dataSource.searchRepositories(page = 2, query = "compose")

        // Then
        assertEquals(2, service.lastSearchPage)
        assertEquals("stars", service.lastSortBy)
    }

    // RED: result should be mapped from DTO to domain model
    @Test
    fun `GIVEN API response WHEN searchRepositories THEN returns mapped domain result`() = runTest {
        // Given
        service.searchResponse = sampleResultListDto()

        // When
        val actual = dataSource.searchRepositories(page = 1, query = "compose")

        // Then
        assertEquals(sampleResultList(), actual)
    }

    // RED: search results should be cached
    @Test
    fun `GIVEN API response WHEN searchRepositories THEN caches returned items`() = runTest {
        // Given
        service.searchResponse = sampleResultListDto()

        // When
        dataSource.searchRepositories(page = 1, query = "compose")

        // Then
        assertEquals(sampleRepo(), cache.getRepo("reactive/compose-hub"))
    }

    // endregion

    // region getRepository

    // RED: if repo is in cache, return from cache without calling API
    @Test
    fun `GIVEN repo in cache WHEN getRepository THEN returns cached without calling API`() = runTest {
        // Given
        val cachedRepo = sampleRepo()
        cache.saveRepos(listOf(cachedRepo))
        service.repoResponse = sampleRepoDto().copy(id = 999)

        // When
        val actual = dataSource.getRepository(owner = "reactive", name = "compose-hub")

        // Then
        assertEquals(cachedRepo, actual)
        assertEquals(0, service.getRepositoryCalls)
    }

    // RED: if cache is empty, call API and return mapped result
    @Test
    fun `GIVEN empty cache WHEN getRepository THEN calls API and returns mapped result`() = runTest {
        // Given
        service.repoResponse = sampleRepoDto()

        // When
        val actual = dataSource.getRepository(owner = "reactive", name = "compose-hub")

        // Then
        assertEquals(sampleRepo(), actual)
        assertEquals(1, service.getRepositoryCalls)
        assertEquals("reactive", service.lastOwner)
        assertEquals("compose-hub", service.lastRepoName)
    }

    // endregion

    // region Error mapping

    // RED: UnknownHostException should be wrapped as NoInternetConnectionException
    @Test
    fun `GIVEN UnknownHostException WHEN getRepository THEN throws NoInternetConnectionException`() = runTest {
        // Given
        service.repoException = UnknownHostException("offline")

        // When / Then
        try {
            dataSource.getRepository(owner = "reactive", name = "compose-hub")
            fail("Expected NoInternetConnectionException to be thrown")
        } catch (_: NoInternetConnectionException) {
            // Verified: correct exception type thrown
        }
    }

    // endregion

    // region isLastPage

    // RED: should return false when more data is available
    @Test
    fun `GIVEN more data available WHEN isLastPage THEN returns false`() {
        // Given / When / Then
        assertEquals(false, dataSource.isLastPage(currentPage = 0, totalCount = 30))
    }

    // RED: should return true when data is exhausted
    @Test
    fun `GIVEN no more data WHEN isLastPage THEN returns true`() {
        // Given / When / Then
        assertEquals(true, dataSource.isLastPage(currentPage = 1, totalCount = 31))
    }

    // endregion

    private class FakeSearchServiceApi : SearchServiceApi {
        var searchResponse: ResultListDto<RepoDto> = sampleResultListDto()
        var repoResponse: RepoDto = sampleRepoDto()
        var searchException: Exception? = null
        var repoException: Exception? = null

        var lastSearchQuery: String? = null
        var lastSearchPage: Int? = null
        var lastSortBy: String? = null
        var lastOwner: String? = null
        var lastRepoName: String? = null
        var getRepositoryCalls = 0

        override suspend fun searchRepositories(
            query: String,
            page: Int,
            sortBy: String,
        ): ResultListDto<RepoDto> {
            lastSearchQuery = query
            lastSearchPage = page
            lastSortBy = sortBy
            searchException?.let { throw it }
            return searchResponse
        }

        override suspend fun getRepository(owner: String, name: String): RepoDto {
            getRepositoryCalls += 1
            lastOwner = owner
            lastRepoName = name
            repoException?.let { throw it }
            return repoResponse
        }
    }
}
