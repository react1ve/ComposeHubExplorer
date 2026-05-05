package com.reactive.data.remote.model.mappers

import com.reactive.data.sampleRepo
import com.reactive.data.sampleRepoDto
import org.junit.Assert.assertEquals
import org.junit.Test

class RepoDtoMapperTest {

    private val mapper = RepoDtoMapper(UserDtoMapper())

    @Test
    fun `mapToDomainModel converts nullable dto fields to empty strings`() {
        val dto = sampleRepoDto(
            description = null,
            htmlUrl = null,
            homepage = null,
            language = null,
        )

        val actual = mapper.mapToDomainModel(dto)

        assertEquals(
            sampleRepo().copy(
                description = "",
                url = "",
                homepage = "",
                language = "",
            ),
            actual,
        )
    }

    @Test
    fun `mapFromDomainModel converts repo back to dto`() {
        assertEquals(sampleRepoDto(), mapper.mapFromDomainModel(sampleRepo()))
    }
}

