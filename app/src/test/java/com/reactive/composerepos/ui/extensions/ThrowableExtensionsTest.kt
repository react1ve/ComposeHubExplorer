package com.reactive.composerepos.ui.extensions

import com.reactive.composerepos.ui.screens.ScreenStatus
import com.reactive.domain.model.NoInternetConnectionException
import org.junit.Assert.assertEquals
import org.junit.Test

class ThrowableExtensionsTest {

    @Test
    fun `getScreenStatusDependingOnError returns NO_INTERNET for NoInternetConnectionException`() {
        val result = NoInternetConnectionException().getScreenStatusDependingOnError()

        assertEquals(ScreenStatus.NO_INTERNET, result)
    }

    @Test
    fun `getScreenStatusDependingOnError returns ERROR for any other exception`() {
        val result = IllegalStateException("boom").getScreenStatusDependingOnError()

        assertEquals(ScreenStatus.ERROR, result)
    }
}

