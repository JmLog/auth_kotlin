package sample.auth.util

import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Test
import sample.auth.util.token.TokenUtil

class TokenUtilTest {

    private val secretKey = Keys.hmacShaKeyFor("f6BOAGB0uwxh7QusW5Vee81oPZVzCYTyAXcyH0nnX3U=".toByteArray())

    @Test
    fun generateAccessToken() {
        // given
        val email = "wmlals0002@gmail.com"

        // when
        val token = TokenUtil.generateAccessToken(email)
        println("token: $token")

        // then
        val validateToken = TokenUtil.validateToken(token)
        println("validateToken: $validateToken")
    }
}