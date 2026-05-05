@file:Suppress("unused")

package com.reactive.composerepos

import androidx.paging.PagingData
import com.github.kittinunf.result.Result
import com.reactive.domain.model.Repo
import com.reactive.domain.model.User
import com.reactive.domain.repositories.GithubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun sampleRepo() = Repo(
    id = 1,
    name = "compose-hub",
    fullName = "reactive/compose-hub",
    owner = User(login = "reactive", id = 101, avatarUrl = "avatar"),
    description = "Compose sample",
    url = "https://github.com/reactive/compose-hub",
    homepage = "https://reactive.dev",
    stars = 120,
    language = "Kotlin",
    forks = 12,
)

fun successResult(repo : Repo) : Result<Repo, Exception> = Result.of { repo }

fun failureResult(exception : Exception) : Result<Repo, Exception> = Result.of { throw exception }

class FakeGithubRepository : GithubRepository {
    var kotlinReposFlow : Flow<PagingData<Repo>> = flowOf(PagingData.empty())
    var searchReposFlow : Flow<PagingData<Repo>> = flowOf(PagingData.empty())
    var repoResult : Result<Repo, Exception> = successResult(sampleRepo())

    var getKotlinReposCalls = 0
    val searchQueries = mutableListOf<String>()
    var lastOwner : String? = null
    var lastName : String? = null

    override fun getKotlinRepos() : Flow<PagingData<Repo>> {
        getKotlinReposCalls += 1
        return kotlinReposFlow
    }

    override fun searchKotlinRepos(query : String) : Flow<PagingData<Repo>> {
        searchQueries += query
        return searchReposFlow
    }

    override suspend fun getKotlinRepo(owner : String, name : String) : Result<Repo, Exception> {
        lastOwner = owner
        lastName = name
        return repoResult
    }
}

