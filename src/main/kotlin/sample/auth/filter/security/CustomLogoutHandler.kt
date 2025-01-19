package sample.auth.filter.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Component
import sample.auth.repository.user.UserRepository
import sample.auth.service.user.LoginService
import sample.auth.util.token.TokenUtil

@Component
class CustomLogoutHandler(
    private val loginService: LoginService,
    private val userRepository: UserRepository
) : LogoutHandler {

    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.substring(7) // "Bearer " 이후의 토큰 추출
            val email = TokenUtil.validateToken(token) // 토큰 검증 및 이메일 추출

            // 토큰 검증 성공
            if (email != null) {
                val user = userRepository.findByEmail(email)
                    ?: throw IllegalArgumentException("가입되지 않은 이메일입니다.")

                // 로그아웃 로직 수행
                loginService.logout(user = user)
            }
        }
    }
}