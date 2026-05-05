package com.reactive.data

import com.reactive.data.local.model.RepoDbo
import com.reactive.data.local.model.UserDbo
import com.reactive.data.remote.model.RepoDto
import com.reactive.data.remote.model.ResultListDto
import com.reactive.data.remote.model.UserDto
import com.reactive.domain.model.Repo
import com.reactive.domain.model.ResultList
import com.reactive.domain.model.User

fun sampleRepo() = Repo(
    id = 1,
    name = "compose-hub",
    fullName = "reactive/compose-hub",
    owner = User(
        login = "reactive",
        id = 101,
        avatarUrl = "https://avatars.githubusercontent.com/u/101",
    ),
    description = "Compose sample",
    url = "https://github.com/reactive/compose-hub",
    homepage = "https://reactive.dev",
    stars = 120,
    language = "Kotlin",
    forks = 12,
)

fun sampleRepoDto(
    description : String? = "Compose sample",
    htmlUrl : String? = "https://github.com/reactive/compose-hub",
    homepage : String? = "https://reactive.dev",
    language : String? = "Kotlin",
) = RepoDto(
    id = 1,
    name = "compose-hub",
    fullName = "reactive/compose-hub",
    owner = UserDto(
        login = "reactive",
        id = 101,
        avatarUrl = "https://avatars.githubusercontent.com/u/101",
    ),
    description = description,
    htmlUrl = htmlUrl,
    homepage = homepage,
    stargazersCount = 120,
    language = language,
    forksCount = 12,
)

fun sampleRepoDbo(
    description : String? = "Compose sample",
    htmlUrl : String? = "https://github.com/reactive/compose-hub",
    homepage : String? = "https://reactive.dev",
    language : String? = "Kotlin",
) = RepoDbo(
    id = 1,
    name = "compose-hub",
    fullName = "reactive/compose-hub",
    owner = UserDbo(
        userId = 101,
        login = "reactive",
        avatarUrl = "https://avatars.githubusercontent.com/u/101",
    ),
    description = description,
    htmlUrl = htmlUrl,
    homepage = homepage,
    stargazersCount = 120,
    language = language,
    forksCount = 12,
)

fun sampleResultListDto(items : List<RepoDto> = listOf(sampleRepoDto())) = ResultListDto(
    totalCount = items.size,
    items = items,
)

fun sampleResultList(items : List<Repo> = listOf(sampleRepo())) = ResultList(
    totalCount = items.size,
    items = items,
)


