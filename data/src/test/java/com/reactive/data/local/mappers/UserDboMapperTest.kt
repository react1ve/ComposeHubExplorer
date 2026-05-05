package com.reactive.data.local.mappers

import com.reactive.data.local.model.UserDbo
import com.reactive.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Test

class UserDboMapperTest {

    private val mapper = UserDboMapper()

    @Test
    fun `mapToDomainModel maps dbo to domain`() {
        val dbo = UserDbo(userId = 101, login = "reactive", avatarUrl = "avatar")

        val result = mapper.mapToDomainModel(dbo)

        assertEquals(User(login = "reactive", id = 101, avatarUrl = "avatar"), result)
    }

    @Test
    fun `mapFromDomainModel maps domain to dbo`() {
        val domain = User(login = "reactive", id = 101, avatarUrl = "avatar")

        val result = mapper.mapFromDomainModel(domain)

        assertEquals(UserDbo(userId = 101, login = "reactive", avatarUrl = "avatar"), result)
    }
}

