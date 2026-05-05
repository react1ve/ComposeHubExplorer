package com.reactive.data.remote.model.mappers

import com.reactive.data.sampleRepo
import com.reactive.data.sampleRepoDto
import com.reactive.data.sampleResultList
import com.reactive.data.sampleResultListDto
import org.junit.Assert.assertEquals
import org.junit.Test

class RepoResultListDtoMapperTest {

    private val mapper = RepoResultListDtoMapper(RepoDtoMapper(UserDtoMapper()))

    @Test
    fun `mapToDomainModel converts dto list to domain list`() {
        val dto = sampleResultListDto(
            items = listOf(
                sampleRepoDto(),
                sampleRepoDto().copy(id = 2, name = "second", fullName = "reactive/second"),
            ),
        )

        val actual = mapper.mapToDomainModel(dto)

        assertEquals(
            sampleResultList(
                items = listOf(
                    sampleRepo(),
                    sampleRepo().copy(id = 2, name = "second", fullName = "reactive/second"),
                ),
            ),
            actual,
        )
    }

    @Test
    fun `mapFromDomainModel converts domain list back to dto list`() {
        val domain = sampleResultList(
            items = listOf(
                sampleRepo(),
                sampleRepo().copy(id = 2, name = "second", fullName = "reactive/second"),
            ),
        )

        val actual = mapper.mapFromDomainModel(domain)

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
}

