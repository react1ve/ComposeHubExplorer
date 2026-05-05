package com.reactive.composerepos.ui.screens.search

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SearchStateTest {

    @Test
    fun `default SearchState has empty query and not debouncing`() {
        val state = SearchState()

        assertEquals("", state.query)
        assertFalse(state.isDebouncing)
    }

    @Test
    fun `copy updates only query`() {
        val state = SearchState(query = "compose", isDebouncing = true)
        val updated = state.copy(query = "kotlin")

        assertEquals("kotlin", updated.query)
        assertEquals(true, updated.isDebouncing)
    }

    @Test
    fun `equality works on all fields`() {
        val state1 = SearchState(query = "test", isDebouncing = false)
        val state2 = SearchState(query = "test", isDebouncing = false)

        assertEquals(state1, state2)
        assertEquals(state1.hashCode(), state2.hashCode())
    }
}
