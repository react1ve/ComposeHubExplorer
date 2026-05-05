package com.reactive.data.remote

import com.reactive.data.sampleRepo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RepositoryCacheTest {

    @Test
    fun `saveRepos stores repos by full name and getRepo returns cached value`() {
        val cache = RepositoryCache()
        val repo = sampleRepo()

        cache.saveRepos(listOf(repo))

        assertEquals(repo, cache.getRepo("reactive/compose-hub"))
    }

    @Test
    fun `getRepo returns null when value is absent`() {
        val cache = RepositoryCache()

        assertNull(cache.getRepo("unknown/repo"))
    }
}

