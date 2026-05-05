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
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test
import java.net.UnknownHostException

class GithubDataSourceTest {

    private val service = FakeSearchServiceApi()
    private val repoDtoMapper = RepoDtoMapper(UserDtoMapper())
    private val resultDtoMapper = RepoResultListDtoMapper(repoDtoMapper)
    private val cache = RepositoryCache()
    private val dataSource = GithubDataSource(service, repoDtoMapper, resultDtoMapper, cache)

    @Test
    fun `searchRepositories appends kotlin filter caches items and maps result`() = runTest {
        service.searchResponse = sampleResultListDto()

        val actual = dataSource.searchRepositories(page = 2, query = "compose")

        assertEquals("compose+language:kotlin", service.lastSearchQuery)
        assertEquals(2, service.lastSearchPage)
        assertEquals("stars", service.lastSortBy)
        assertEquals(sampleResultList(), actual)
        assertEquals(sampleRepo(), cache.getRepo("reactive/compose-hub"))
    }

    @Test
    fun `getRepository returns cached value without calling service`() = runTest {
        val cachedRepo = sampleRepo()
        cache.saveRepos(listOf(cachedRepo))
        service.repoResponse = sampleRepoDto().copy(id = 999)

        val actual = dataSource.getRepository(owner = "reactive", name = "compose-hub")

        assertEquals(cachedRepo, actual)
        assertEquals(0, service.getRepositoryCalls)
        assertNull(service.lastOwner)
        assertNull(service.lastRepoName)
    }

    @Test
    fun `getRepository maps connection issues to NoInternetConnectionException`() = runTest {
        service.repoException = UnknownHostException("offline")

        try {
            dataSource.getRepository(owner = "reactive", name = "compose-hub")
            fail("Expected NoInternetConnectionException to be thrown")
        } catch (_ : NoInternetConnectionException) {
            assertEquals("reactive", service.lastOwner)
            assertEquals("compose-hub", service.lastRepoName)
        }
    }

    @Test
    fun `isLastPage returns correct value around page boundary`() {
        assertEquals(false, dataSource.isLastPage(currentPage = 0, totalCount = 30))
        assertEquals(true, dataSource.isLastPage(currentPage = 1, totalCount = 31))
    }

    private class FakeSearchServiceApi : SearchServiceApi {
        var searchResponse : ResultListDto<RepoDto> = sampleResultListDto()
        var repoResponse : RepoDto = sampleRepoDto()
        var searchException : Exception? = null
        var repoException : Exception? = null

        var lastSearchQuery : String? = null
        var lastSearchPage : Int? = null
        var lastSortBy : String? = null
        var lastOwner : String? = null
        var lastRepoName : String? = null
        var getRepositoryCalls = 0

        override suspend fun searchRepositories(
            query : String,
            page : Int,
            sortBy : String,
        ) : ResultListDto<RepoDto> {
            lastSearchQuery = query
            lastSearchPage = page
            lastSortBy = sortBy
            searchException?.let { throw it }
            return searchResponse
        }

        override suspend fun getRepository(owner : String, name : String) : RepoDto {
            getRepositoryCalls += 1
            lastOwner = owner
            lastRepoName = name
            repoException?.let { throw it }
            return repoResponse
        }
    }
}

