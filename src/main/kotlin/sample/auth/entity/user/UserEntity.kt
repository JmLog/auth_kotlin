package sample.auth.entity.user

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "user")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "name")
    val name: String,

    @Column(name = "email", unique = true)
    val email: String,

    @Column(name = "password")
    val password: String,

    @Column(name = "phone", unique = true)
    val phone: String,

    @Column(name = "access_token", nullable = true)
    var accessToken: String? = null,

    @Column(name = "refresh_token", nullable = true)
    var refreshToken: String? = null,

    @Column(name = "remember_token", nullable = true)
    val rememberToken: String? = null,

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: LocalDateTime? = null
) {
    @PrePersist
    fun onCreate() {
        createdAt = LocalDateTime.now() // 엔티티 생성 시
        updatedAt = LocalDateTime.now() // 생성 시 updatedAt도 초기화
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now() // 엔티티 업데이트 시
    }
}