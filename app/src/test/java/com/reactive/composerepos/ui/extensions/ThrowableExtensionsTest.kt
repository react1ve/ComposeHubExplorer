package com.reactive.composerepos.ui.extensions

import com.reactive.composerepos.ui.screens.ScreenStatus
import com.reactive.domain.model.NoInternetConnectionException
import org.junit.Assert.assertEquals
import org.junit.Test

class ThrowableExtensionsTest {

    // RED: offline error should map to NO_INTERNET
    @Test
    fun `GIVEN NoInternetConnectionException WHEN getScreenStatusDependingOnError THEN returns NO_INTERNET`() {
        // ...existing code...
    }

    // RED: generic error should map to ERROR
    @Test
    fun `GIVEN generic exception WHEN getScreenStatusDependingOnError THEN returns ERROR`() {
        // ...existing code...
    }

    // RED: RuntimeException should also map to ERROR
    @Test
    fun `GIVEN RuntimeException WHEN getScreenStatusDependingOnError THEN returns ERROR`() {
        // ...existing code...
    }

    // RED: NullPointerException should also map to ERROR (not NO_INTERNET)
    @Test
    fun `GIVEN NullPointerException WHEN getScreenStatusDependingOnError THEN returns ERROR`() {
        // Given
        val throwable: Throwable = NullPointerException()

        // When
        val result = throwable.getScreenStatusDependingOnError()

        // Then
        assertEquals(ScreenStatus.ERROR, result)
    }
}
