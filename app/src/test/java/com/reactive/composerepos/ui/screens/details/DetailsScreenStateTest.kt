package com.reactive.composerepos.ui.screens.details

import com.reactive.composerepos.ui.screens.ScreenStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DetailsScreenStateTest {

    @Test
    fun `Empty companion has null repo and LOADING status`() {
        val empty = DetailsScreenState.Empty

        assertNull(empty.repo)
        assertEquals(ScreenStatus.LOADING, empty.screenStatus)
    }

    @Test
    fun `default constructor values match Empty companion`() {
        val default = DetailsScreenState()

        assertEquals(DetailsScreenState.Empty, default)
    }

    @Test
    fun `copy updates only specific fields`() {
        val state = DetailsScreenState.Empty.copy(screenStatus = ScreenStatus.ERROR)

        assertNull(state.repo)
        assertEquals(ScreenStatus.ERROR, state.screenStatus)
    }
}

