package sample.auth.filter.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import sample.auth.dto.security.CustomUserDetail
import sample.auth.dto.user.LoginResponseDto
import sample.auth.service.user.LoginService

class LoginFilter(
    private val authenticationManager: AuthenticationManager,
    private val loginService: LoginService
): UsernamePasswordAuthenticationFilter() {

    init {
        // UsernamePasswordAuthenticationFilter: username -> email 로 변경
        usernameParameter = "email"
    }

    /**
     * 로그인
     */
    override fun attemptAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Authentication {
        val email = obtainUsername(request)
        val password = obtainPassword(request)

        val authentication = UsernamePasswordAuthenticationToken(email, password)

        return authenticationManager.authenticate(authentication)
    }

    /**
     * 로그인 성공
     */
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        // 여기서 로그인 성공한 유저의 email 가져오기
        val userDetail = authResult.principal as CustomUserDetail
        val email = userDetail.username
        val user = loginService.login(email)

        val objectMapper = jacksonObjectMapper().registerKotlinModule()
        val loginResponseDto = LoginResponseDto(
            user.accessToken ?: "",
            user.refreshToken ?: ""
        )
        // JSON 직렬화
        val userJson = objectMapper.writeValueAsString(loginResponseDto)

        // 응답 작성
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_OK

        val responseBody = """
        {
            "code": 200,
            "data": $userJson,
            "message": "로그인에 성공했습니다.",
        }
        """.trimIndent()

        response.writer.write(responseBody)
    }

    /**
     * 로그인 실패
     */
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        /**
         * throw UsernameNotFoundException("가입되지 않은 이메일입니다.")
         * throw BadCredentialsException("비밀번호가 일치하지 않습니다.")
         */
        val errorMessage = failed.message;

        // 응답 작성
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val responseBody = """
        {
            "code": 401,
            "message": "$errorMessage",
        }
        """.trimIndent()

        response.writer.write(responseBody)
    }
}