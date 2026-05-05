package com.reactive.data.remote

import com.reactive.data.sampleRepo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RepositoryCacheTest {

    // RED: saved repo should be retrievable by fullName
    @Test
    fun `GIVEN repo saved WHEN getRepo called with fullName THEN returns that repo`() {
        // Given
        val cache = RepositoryCache()
        val repo = sampleRepo()
        cache.saveRepos(listOf(repo))

        // When
        val result = cache.getRepo("reactive/compose-hub")

        // Then
        assertEquals(repo, result)
    }

    // RED: absent key should return null
    @Test
    fun `GIVEN empty cache WHEN getRepo called THEN returns null`() {
        // Given
        val cache = RepositoryCache()

        // When
        val result = cache.getRepo("unknown/repo")

        // Then
        assertNull(result)
    }

    // RED: re-saving should overwrite the value
    @Test
    fun `GIVEN repo saved WHEN saving updated version THEN getRepo returns updated`() {
        // Given
        val cache = RepositoryCache()
        val repo = sampleRepo()
        cache.saveRepos(listOf(repo))

        // When
        val updated = repo.copy(stars = 999)
        cache.saveRepos(listOf(updated))

        // Then
        assertEquals(999, cache.getRepo("reactive/compose-hub")!!.stars)
    }

    // RED: multiple repos should be stored independently
    @Test
    fun `GIVEN multiple repos saved WHEN getRepo called THEN each is retrievable`() {
        // Given
        val cache = RepositoryCache()
        val repo1 = sampleRepo()
        val repo2 = sampleRepo().copy(id = 2, fullName = "reactive/other", name = "other")

        // When
        cache.saveRepos(listOf(repo1, repo2))

        // Then
        assertEquals(repo1, cache.getRepo("reactive/compose-hub"))
        assertEquals(repo2, cache.getRepo("reactive/other"))
    }

    // RED: key lookup should be case-sensitive
    @Test
    fun `GIVEN repo saved WHEN getRepo called with different case THEN returns null`() {
        // Given
        val cache = RepositoryCache()
        cache.saveRepos(listOf(sampleRepo()))

        // When
        val result = cache.getRepo("Reactive/Compose-Hub")

        // Then
        assertNull(result)
    }
}
