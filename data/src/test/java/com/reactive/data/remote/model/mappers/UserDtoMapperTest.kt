package com.reactive.data.remote.model.mappers

import com.reactive.data.remote.model.UserDto
import com.reactive.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Test

class UserDtoMapperTest {

    private val mapper = UserDtoMapper()

    // RED: DTO to Domain should preserve all fields
    @Test
    fun `GIVEN user dto WHEN mapToDomainModel THEN maps all fields to domain user`() {
        // Given
        val dto = UserDto(login = "reactive", id = 101, avatarUrl = "avatar")

        // When
        val result = mapper.mapToDomainModel(dto)

        // Then
        assertEquals(User(login = "reactive", id = 101, avatarUrl = "avatar"), result)
    }

    // RED: Domain to DTO should preserve all fields
    @Test
    fun `GIVEN domain user WHEN mapFromDomainModel THEN maps all fields to dto`() {
        // Given
        val domain = User(login = "reactive", id = 101, avatarUrl = "avatar")

        // When
        val result = mapper.mapFromDomainModel(domain)

        // Then
        assertEquals(UserDto(login = "reactive", id = 101, avatarUrl = "avatar"), result)
    }
}
