package com.reactive.data.remote.services

import com.reactive.data.Constants
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PagingInterceptorTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        client = OkHttpClient.Builder()
            .addInterceptor(PagingInterceptor())
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    // --- RED: interceptor appends per_page query parameter ---

    @Test
    fun `interceptor adds per_page parameter to request URL`() {
        server.enqueue(MockResponse().setBody("{}"))

        val request = Request.Builder()
            .url(server.url("/search/repositories?q=kotlin"))
            .build()

        client.newCall(request).execute()

        val recordedRequest = server.takeRequest()
        val url = recordedRequest.requestUrl!!
        assertEquals(Constants.ELEMENTS_PER_PAGE.toString(), url.queryParameter("per_page"))
    }

    // --- RED: interceptor preserves existing query parameters ---

    @Test
    fun `interceptor preserves existing query parameters`() {
        server.enqueue(MockResponse().setBody("{}"))

        val request = Request.Builder()
            .url(server.url("/search/repositories?q=kotlin&page=2&s=stars"))
            .build()

        client.newCall(request).execute()

        val recordedRequest = server.takeRequest()
        val url = recordedRequest.requestUrl!!
        assertEquals("kotlin", url.queryParameter("q"))
        assertEquals("2", url.queryParameter("page"))
        assertEquals("stars", url.queryParameter("s"))
        assertEquals(Constants.ELEMENTS_PER_PAGE.toString(), url.queryParameter("per_page"))
    }

    // --- RED: interceptor works with URL without query parameters ---

    @Test
    fun `interceptor works with URL that has no existing query parameters`() {
        server.enqueue(MockResponse().setBody("{}"))

        val request = Request.Builder()
            .url(server.url("/repos/owner/name"))
            .build()

        client.newCall(request).execute()

        val recordedRequest = server.takeRequest()
        val url = recordedRequest.requestUrl!!
        assertEquals(Constants.ELEMENTS_PER_PAGE.toString(), url.queryParameter("per_page"))
        assertTrue(url.toString().contains("per_page=${Constants.ELEMENTS_PER_PAGE}"))
    }
}

