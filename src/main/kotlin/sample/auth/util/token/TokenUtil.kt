package sample.auth.util.token

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*

object TokenUtil {
    private val secretKey = Keys.hmacShaKeyFor("f6BOAGB0uwxh7QusW5Vee81oPZVzCYTyAXcyH0nnX3U=".toByteArray())

    /**
     * Access Token: 짧은 유효 기간 (15분)
     */
    fun generateAccessToken(email: String): String {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15분
            .signWith(secretKey)
            .compact()
    }

    /**
     * Refresh Token: 긴 유효 기간 (7일)
     */
    fun generateRefreshToken(email: String): String {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7일
            .signWith(secretKey)
            .compact()
    }

    /**
     * Jwt 토큰 검증
     */
    fun validateToken(token: String): String? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
            claims.subject // 토큰의 이메일 또는 고유 식별자 반환
        } catch (ex: Exception) {
            null // 유효하지 않은 토큰
        }
    }
}