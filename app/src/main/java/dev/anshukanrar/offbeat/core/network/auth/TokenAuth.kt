package dev.anshukanrar.offbeat.core.network.auth
import java.security.MessageDigest
import java.security.SecureRandom

object TokenAuth{
    fun newSalt(): String{
        val bytes = ByteArray(8)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }
    fun token(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("MD5")
            .digest((password+salt).toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}

