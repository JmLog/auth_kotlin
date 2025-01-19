package sample.auth.config.security

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import sample.auth.filter.security.CustomAuthenticationProvider
import sample.auth.filter.security.CustomLogoutHandler
import sample.auth.filter.security.JwtAuthenticationFilter
import sample.auth.filter.security.LoginFilter
import sample.auth.service.user.LoginService

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val customAuthenticationProvider: CustomAuthenticationProvider,
    private val customLogoutHandler: CustomLogoutHandler,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val loginService: LoginService
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager {
        return configuration.authenticationManager
    }

    @Bean
    fun filterChain(
        http: HttpSecurity,
    ): SecurityFilterChain {
        // PasswordEncoder 를 CustomAuthenticationProvider 에 주입
        customAuthenticationProvider.setPasswordEncoder(passwordEncoder())

        http
            .csrf { it.disable() } // jwt 사용시 csrf 필요 없음
            .formLogin { it.disable() } // 기본 from login 사용 안함
            .httpBasic { it.disable() } // 기본 http basic 사용 안함
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/",
                    "/error",
                    "/login"
                ).permitAll() // 인증 없이 허용
                    .anyRequest() // 다른 요청은
                    .authenticated() // 인증 필요
            }
            // jwt 사용시 session 필요 없음
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            // jwt 필터 추가
            .addFilterBefore(jwtAuthenticationFilter, LoginFilter::class.java)
            // 로그인 필터 추가
            .addFilterAt(
                LoginFilter(
                    authenticationManager(authenticationConfiguration),
                    loginService
                ),
                UsernamePasswordAuthenticationFilter::class.java
            )
            // 각각의 응답을 생성하기 위해 추가
            .authenticationProvider(customAuthenticationProvider)
            // 로그아웃 핸들러 추가
            .logout {
                it.logoutUrl("/logout")
                it.addLogoutHandler(customLogoutHandler)
                it.logoutSuccessHandler { _, response, _ ->
                    // 응답 작성
                    response.contentType = "application/json"
                    response.characterEncoding = "UTF-8"
                    response.status = HttpServletResponse.SC_OK

                    val responseBody = """
                    {
                        "code": 200,
                        "data": null,
                        "message": "로그아웃에 성공했습니다.",
                    }
                    """.trimIndent()

                    response.writer.write(responseBody)
                }
            }

        return http.build();
    }
}