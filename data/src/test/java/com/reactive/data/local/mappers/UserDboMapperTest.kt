package com.reactive.data.local.mappers

import com.reactive.data.local.model.UserDbo
import com.reactive.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Test

class UserDboMapperTest {

    private val mapper = UserDboMapper()

    // RED: DBO to Domain should map userId to id
    @Test
    fun `GIVEN user dbo WHEN mapToDomainModel THEN maps userId to id and all fields`() {
        // Given
        val dbo = UserDbo(userId = 101, login = "reactive", avatarUrl = "avatar")

        // When
        val result = mapper.mapToDomainModel(dbo)

        // Then
        assertEquals(User(login = "reactive", id = 101, avatarUrl = "avatar"), result)
    }

    // RED: Domain to DBO should map id to userId
    @Test
    fun `GIVEN domain user WHEN mapFromDomainModel THEN maps id to userId and all fields`() {
        // Given
        val domain = User(login = "reactive", id = 101, avatarUrl = "avatar")

        // When
        val result = mapper.mapFromDomainModel(domain)

        // Then
        assertEquals(UserDbo(userId = 101, login = "reactive", avatarUrl = "avatar"), result)
    }
}
