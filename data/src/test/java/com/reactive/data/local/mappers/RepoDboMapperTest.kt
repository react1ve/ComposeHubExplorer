package com.reactive.data.local.mappers

import com.reactive.data.sampleRepo
import com.reactive.data.sampleRepoDbo
import org.junit.Assert.assertEquals
import org.junit.Test

class RepoDboMapperTest {

    private val mapper = RepoDboMapper(UserDboMapper())

    @Test
    fun `mapToDomainModel converts nullable dbo fields to empty strings`() {
        val dbo = sampleRepoDbo(
            description = null,
            htmlUrl = null,
            homepage = null,
            language = null,
        )

        val actual = mapper.mapToDomainModel(dbo)

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
    fun `mapFromDomainModel converts repo back to dbo`() {
        assertEquals(sampleRepoDbo(), mapper.mapFromDomainModel(sampleRepo()))
    }
}

