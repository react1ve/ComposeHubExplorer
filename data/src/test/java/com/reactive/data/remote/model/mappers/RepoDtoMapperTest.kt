package com.reactive.data.remote.model.mappers

import com.reactive.data.sampleRepo
import com.reactive.data.sampleRepoDto
import org.junit.Assert.assertEquals
import org.junit.Test

class RepoDtoMapperTest {

    private val mapper = RepoDtoMapper(UserDtoMapper())

    // RED: nullable DTO fields should become empty strings in domain
    @Test
    fun `GIVEN dto with null fields WHEN mapToDomainModel THEN replaces nulls with empty strings`() {
        // Given
        val dto = sampleRepoDto(
            description = null,
            htmlUrl = null,
            homepage = null,
            language = null,
        )

        // When
        val actual = mapper.mapToDomainModel(dto)

        // Then
        assertEquals("", actual.description)
        assertEquals("", actual.url)
        assertEquals("", actual.homepage)
        assertEquals("", actual.language)
    }

    // RED: non-null fields should be mapped correctly
    @Test
    fun `GIVEN dto with all fields WHEN mapToDomainModel THEN maps all values correctly`() {
        // Given
        val dto = sampleRepoDto()

        // When
        val actual = mapper.mapToDomainModel(dto)

        // Then
        assertEquals(sampleRepo(), actual)
    }

    // RED: domain to DTO mapping should be lossless
    @Test
    fun `GIVEN domain repo WHEN mapFromDomainModel THEN produces correct dto`() {
        // Given
        val domain = sampleRepo()

        // When
        val actual = mapper.mapFromDomainModel(domain)

        // Then
        assertEquals(sampleRepoDto(), actual)
    }
}
