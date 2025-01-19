package sample.auth.service.user

import org.springframework.stereotype.Service
import sample.auth.entity.user.UserEntity
import sample.auth.repository.user.UserRepository
import sample.auth.util.token.TokenUtil

@Service
class LoginService(
    private val userRepository: UserRepository
) {

    /**
     * 로그인 정보를 이용하여 사용자 조회 및 토큰 처리
     */
    fun login(email: String): UserEntity {
        // db 에서 사용자 조회
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("가입되지 않은 이메일입니다.")

        val accessToken = user.accessToken
        val refreshToken = user.refreshToken

        // 토큰의 상태를 보고 갱신 여부 결정
        when {
            accessToken.isNullOrBlank() -> {
                this.saveToken(user, accessToken = true, refreshToken = true)
            }
            else -> {
                // access token 검증
                val validateToken = TokenUtil.validateToken(accessToken)
                if (validateToken.isNullOrBlank()) {
                    // refresh token 검증
                    refreshToken?.let {
                        if (TokenUtil.validateToken(it).isNullOrBlank()) {
                            // refreshToken 은 트리거로 사용하며 만료시 둘 다 재갱신
                            this.saveToken(user = user, accessToken = true, refreshToken = true)
                        } else {
                            // accessToken 만 갱신
                            this.saveToken(user = user, accessToken = true)
                        }
                    }
                }
            }
        }

        return user
    }

    /**
     * 로그아웃
     */
    fun logout(user: UserEntity) {
        user.apply {
            this.accessToken = null
            this.refreshToken = null
        }

        userRepository.save(user)
    }

    /**
     * 토큰 저장
     */
    private fun saveToken(user: UserEntity, accessToken: Boolean = false, refreshToken: Boolean = false) {
        user.apply {
            if (accessToken) this.accessToken = TokenUtil.generateAccessToken(user.email)
            if (refreshToken) this.refreshToken = TokenUtil.generateRefreshToken(user.email)
        }

        userRepository.save(user)
    }
}