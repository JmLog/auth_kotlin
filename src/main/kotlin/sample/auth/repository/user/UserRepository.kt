package sample.auth.repository.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sample.auth.entity.user.UserEntity

@Repository
interface UserRepository: JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
}