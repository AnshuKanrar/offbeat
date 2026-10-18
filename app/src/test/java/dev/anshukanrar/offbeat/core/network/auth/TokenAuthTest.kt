package dev.anshukanrar.offbeat.core.network.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class TokenAuthTest {

    @Test
    fun token_matchesTheExampleFromTheSubsonicDocs() {
        val token = TokenAuth.token(password = "sesame", salt = "c19b2d")
        assertEquals("26719a1196d2a940705a59634eb18eab", token)
    }

    @Test
    fun newSalt_is16CharactersAndDiffersEachTime() {
        val a = TokenAuth.newSalt()
        val b = TokenAuth.newSalt()
        assertEquals(16, a.length)
        assertNotEquals(a, b)
    }
}