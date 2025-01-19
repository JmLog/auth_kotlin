package sample.auth.service.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import sample.auth.dto.security.CustomUserDetail
import sample.auth.repository.user.UserRepository

@Service
class CustomUserDetailService (
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(email = username)
            ?: throw IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다.")

        return CustomUserDetail(user)
    }
}