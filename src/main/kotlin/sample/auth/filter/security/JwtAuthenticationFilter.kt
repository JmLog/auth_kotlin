package sample.auth.filter.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import sample.auth.repository.user.UserRepository
import sample.auth.util.token.TokenUtil

@Component
class JwtAuthenticationFilter(
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.substring(7) // "Bearer " 이후의 토큰 추출
            val email = TokenUtil.validateToken(token) // 토큰 검증 및 이메일 추출

            // 토큰 검증 성공
            if (email != null) {
                val user = userRepository.findByEmail(email) // 이메일로 사용자 조회
                if (user != null) {
                    val authentication = UsernamePasswordAuthenticationToken(
                        user, // Principal
                        null, // Credentials (JWT 사용 시 null)
                        listOf() // Authorities
                    )
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }

        filterChain.doFilter(request, response)
    }
}