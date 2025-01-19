package sample.auth.filter.security

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import sample.auth.dto.security.CustomUserDetail
import sample.auth.repository.user.UserRepository

@Component
class CustomAuthenticationProvider(
    private val userRepository: UserRepository
): AuthenticationProvider {

    private lateinit var passwordEncoder: PasswordEncoder

    // PasswordEncoder 설정 메서드 추가
    fun setPasswordEncoder(passwordEncoder: PasswordEncoder) {
        this.passwordEncoder = passwordEncoder
    }

    override fun authenticate(authentication: Authentication): Authentication {
        val email = authentication.name

        val user = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("가입되지 않은 이메일입니다.")

        // 비밀번호 검증
        require(passwordEncoder.matches(authentication.credentials.toString(), user.password)) {
            throw BadCredentialsException("비밀번호가 일치하지 않습니다.")
        }

        // UserEntity를 CustomUserDetail로 변환
        val customUserDetail = CustomUserDetail(user)

        return UsernamePasswordAuthenticationToken(customUserDetail, null, customUserDetail.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}