package com.reactive.data.remote.model.mappers

import com.reactive.data.sampleRepo
import com.reactive.data.sampleRepoDto
import com.reactive.data.sampleResultList
import com.reactive.data.sampleResultListDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RepoResultListDtoMapperTest {

    private val mapper = RepoResultListDtoMapper(RepoDtoMapper(UserDtoMapper()))

    // RED: DTO list with multiple items should map all correctly
    @Test
    fun `GIVEN dto list with items WHEN mapToDomainModel THEN maps all items correctly`() {
        // Given
        val dto = sampleResultListDto(
            items = listOf(
                sampleRepoDto(),
                sampleRepoDto().copy(id = 2, name = "second", fullName = "reactive/second"),
            ),
        )

        // When
        val actual = mapper.mapToDomainModel(dto)

        // Then
        assertEquals(2, actual.items.size)
        assertEquals(sampleRepo(), actual.items[0])
        assertEquals(sampleRepo().copy(id = 2, name = "second", fullName = "reactive/second"), actual.items[1])
    }

    // RED: totalCount should be preserved during mapping
    @Test
    fun `GIVEN dto with totalCount WHEN mapToDomainModel THEN preserves totalCount`() {
        // Given
        val dto = sampleResultListDto()

        // When
        val actual = mapper.mapToDomainModel(dto)

        // Then
        assertEquals(dto.totalCount, actual.totalCount)
    }

    // RED: Domain to DTO reverse mapping
    @Test
    fun `GIVEN domain list WHEN mapFromDomainModel THEN produces correct dto list`() {
        // Given
        val domain = sampleResultList(
            items = listOf(
                sampleRepo(),
                sampleRepo().copy(id = 2, name = "second", fullName = "reactive/second"),
            ),
        )

        // When
        val actual = mapper.mapFromDomainModel(domain)

        // Then
        assertEquals(
            sampleResultListDto(
                items = listOf(
                    sampleRepoDto(),
                    sampleRepoDto().copy(id = 2, name = "second", fullName = "reactive/second"),
                ),
            ),
            actual,
        )
    }

    // RED: empty list should map correctly
    @Test
    fun `GIVEN empty dto list WHEN mapToDomainModel THEN returns empty domain list`() {
        // Given
        val dto = sampleResultListDto(items = emptyList())

        // When
        val actual = mapper.mapToDomainModel(dto)

        // Then
        assertTrue(actual.items.isEmpty())
        assertEquals(0, actual.totalCount)
    }
}
