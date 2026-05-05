package com.reactive.composerepos.ui.screens

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenStatusTest {

    @Test
    fun `isLoading returns true only for LOADING status`() {
        assertTrue(ScreenStatus.LOADING.isLoading())
    }

    @Test
    fun `isLoading returns false for SUCCESS`() {
        assertFalse(ScreenStatus.SUCCESS.isLoading())
    }

    @Test
    fun `isLoading returns false for NO_INTERNET`() {
        assertFalse(ScreenStatus.NO_INTERNET.isLoading())
    }

    @Test
    fun `isLoading returns false for ERROR`() {
        assertFalse(ScreenStatus.ERROR.isLoading())
    }

    @Test
    fun `enum contains all expected values`() {
        val values = ScreenStatus.values()
        assertTrue(values.contains(ScreenStatus.SUCCESS))
        assertTrue(values.contains(ScreenStatus.LOADING))
        assertTrue(values.contains(ScreenStatus.NO_INTERNET))
        assertTrue(values.contains(ScreenStatus.ERROR))
    }
}

