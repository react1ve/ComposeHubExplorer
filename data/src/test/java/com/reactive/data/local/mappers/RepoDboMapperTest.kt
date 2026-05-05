package com.reactive.data.local.mappers

import com.reactive.data.sampleRepo
import com.reactive.data.sampleRepoDbo
import org.junit.Assert.assertEquals
import org.junit.Test

class RepoDboMapperTest {

    private val mapper = RepoDboMapper(UserDboMapper())

    // RED: nullable DBO fields should become empty strings in domain
    @Test
    fun `GIVEN dbo with null fields WHEN mapToDomainModel THEN replaces nulls with empty strings`() {
        // Given
        val dbo = sampleRepoDbo(
            description = null,
            htmlUrl = null,
            homepage = null,
            language = null,
        )

        // When
        val actual = mapper.mapToDomainModel(dbo)

        // Then
        assertEquals("", actual.description)
        assertEquals("", actual.url)
        assertEquals("", actual.homepage)
        assertEquals("", actual.language)
    }

    // RED: non-null DBO fields should map correctly
    @Test
    fun `GIVEN dbo with all fields WHEN mapToDomainModel THEN maps all values correctly`() {
        // Given
        val dbo = sampleRepoDbo()

        // When
        val actual = mapper.mapToDomainModel(dbo)

        // Then
        assertEquals(sampleRepo(), actual)
    }

    // RED: domain to DBO mapping should be lossless
    @Test
    fun `GIVEN domain repo WHEN mapFromDomainModel THEN produces correct dbo`() {
        // Given
        val domain = sampleRepo()

        // When
        val actual = mapper.mapFromDomainModel(domain)

        // Then
        assertEquals(sampleRepoDbo(), actual)
    }
}
