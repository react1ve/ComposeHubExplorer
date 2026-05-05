package com.reactive.data.remote.model.mappers

import com.reactive.data.remote.model.UserDto
import com.reactive.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Test

class UserDtoMapperTest {

    private val mapper = UserDtoMapper()

    @Test
    fun `mapToDomainModel maps dto to domain`() {
        val dto = UserDto(login = "reactive", id = 101, avatarUrl = "avatar")

        val result = mapper.mapToDomainModel(dto)

        assertEquals(User(login = "reactive", id = 101, avatarUrl = "avatar"), result)
    }

    @Test
    fun `mapFromDomainModel maps domain to dto`() {
        val domain = User(login = "reactive", id = 101, avatarUrl = "avatar")

        val result = mapper.mapFromDomainModel(domain)

        assertEquals(UserDto(login = "reactive", id = 101, avatarUrl = "avatar"), result)
    }
}

