package com.reactive.data.remote

import com.reactive.data.sampleRepo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RepositoryCacheEdgeCasesTest {

    @Test
    fun `saveRepos overwrites existing repo with same fullName`() {
        val cache = RepositoryCache()
        val repo = sampleRepo()
        cache.saveRepos(listOf(repo))

        val updated = repo.copy(stars = 999)
        cache.saveRepos(listOf(updated))

        assertEquals(999, cache.getRepo("reactive/compose-hub")!!.stars)
    }

    @Test
    fun `saveRepos stores multiple repos`() {
        val cache = RepositoryCache()
        val repo1 = sampleRepo()
        val repo2 = sampleRepo().copy(id = 2, fullName = "reactive/other", name = "other")

        cache.saveRepos(listOf(repo1, repo2))

        assertEquals(repo1, cache.getRepo("reactive/compose-hub"))
        assertEquals(repo2, cache.getRepo("reactive/other"))
    }

    @Test
    fun `saveRepos with empty list does nothing`() {
        val cache = RepositoryCache()

        cache.saveRepos(emptyList())

        assertNull(cache.getRepo("any/repo"))
    }

    @Test
    fun `getRepo is case-sensitive`() {
        val cache = RepositoryCache()
        cache.saveRepos(listOf(sampleRepo()))

        assertNull(cache.getRepo("Reactive/Compose-Hub"))
    }
}

