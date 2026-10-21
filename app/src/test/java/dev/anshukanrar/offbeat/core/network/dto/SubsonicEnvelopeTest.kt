package dev.anshukanrar.offbeat.core.network.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SubsonicEnvelopeTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun okReply_decodes() {
        val text =
            """{"subsonic-response":{"status":"ok","version":"1.16.1","type":"navidrome","serverVersion":"0.64.2 (10114574)","openSubsonic":true}}"""

        val result = json.decodeFromString<SubsonicEnvelope>(text)

        assertEquals("ok", result.response.status)
        assertEquals("navidrome", result.response.type)
        assertNull(result.response.error)
    }

    @Test
    fun failedReply_decodes() {
        val text =
            """{"subsonic-response":{"status":"failed","version":"1.16.1","type":"navidrome","serverVersion":"0.64.2 (10114574)","openSubsonic":true,"error":{"code":40,"message":"Wrong username or password"}}}"""
        val result = json.decodeFromString<SubsonicEnvelope>(text)
        assertEquals("failed", result.response.status)
        assertNotNull(result.response.error)
        assertEquals(40, result.response.error?.code)
        assertEquals("Wrong username or password", result.response.error?.message)
    }
}