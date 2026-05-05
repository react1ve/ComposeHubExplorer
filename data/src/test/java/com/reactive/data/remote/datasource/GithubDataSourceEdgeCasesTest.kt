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
import com.reactive.data.sampleResultListDto
import com.reactive.domain.model.NoInternetConnectionException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.net.ConnectException
import java.net.SocketTimeoutException

class GithubDataSourceEdgeCasesTest {

    private val service = FakeSearchServiceApi()
    private val repoDtoMapper = RepoDtoMapper(UserDtoMapper())
    private val resultDtoMapper = RepoResultListDtoMapper(repoDtoMapper)
    private val cache = RepositoryCache()
    private val dataSource = GithubDataSource(service, repoDtoMapper, resultDtoMapper, cache)

    // --- RED: empty query produces "language:kotlin" with no preceding plus ---

    @Test
    fun `searchRepositories with empty query sends only language filter`() = runTest {
        service.searchResponse = sampleResultListDto()

        dataSource.searchRepositories(page = 1, query = "")

        assertEquals("language:kotlin", service.lastSearchQuery)
    }

    // --- RED: getRepository calls service when cache miss ---

    @Test
    fun `getRepository calls service when not in cache and maps result`() = runTest {
        service.repoResponse = sampleRepoDto()

        val actual = dataSource.getRepository(owner = "reactive", name = "compose-hub")

        assertEquals(sampleRepo(), actual)
        assertEquals(1, service.getRepositoryCalls)
        assertEquals("reactive", service.lastOwner)
        assertEquals("compose-hub", service.lastRepoName)
    }

    // --- RED: ConnectException is mapped to NoInternetConnectionException ---

    @Test
    fun `searchRepositories maps ConnectException to NoInternetConnectionException`() = runTest {
        service.searchException = ConnectException("refused")

        try {
            dataSource.searchRepositories(page = 1, query = "compose")
            fail("Expected NoInternetConnectionException")
        } catch (_: NoInternetConnectionException) {
            // expected
        }
    }

    // --- RED: SocketTimeoutException is mapped to NoInternetConnectionException ---

    @Test
    fun `searchRepositories maps SocketTimeoutException to NoInternetConnectionException`() = runTest {
        service.searchException = SocketTimeoutException("timeout")

        try {
            dataSource.searchRepositories(page = 1, query = "compose")
            fail("Expected NoInternetConnectionException")
        } catch (_: NoInternetConnectionException) {
            // expected
        }
    }

    // --- RED: non-network exceptions are rethrown ---

    @Test
    fun `searchRepositories rethrows non-network exceptions directly`() = runTest {
        val original = IllegalArgumentException("bad request")
        service.searchException = original

        try {
            dataSource.searchRepositories(page = 1, query = "compose")
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals(original, e)
        }
    }

    // --- RED: getRepository with ConnectException wraps as NoInternetConnectionException ---

    @Test
    fun `getRepository maps ConnectException to NoInternetConnectionException`() = runTest {
        service.repoException = ConnectException("refused")

        try {
            dataSource.getRepository(owner = "reactive", name = "compose-hub")
            fail("Expected NoInternetConnectionException")
        } catch (_: NoInternetConnectionException) {
            // expected
        }
    }

    private class FakeSearchServiceApi : SearchServiceApi {
        var searchResponse: ResultListDto<RepoDto> = sampleResultListDto()
        var repoResponse: RepoDto = sampleRepoDto()
        var searchException: Exception? = null
        var repoException: Exception? = null

        var lastSearchQuery: String? = null
        var lastOwner: String? = null
        var lastRepoName: String? = null
        var getRepositoryCalls = 0

        override suspend fun searchRepositories(
            query: String,
            page: Int,
            sortBy: String,
        ): ResultListDto<RepoDto> {
            lastSearchQuery = query
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

