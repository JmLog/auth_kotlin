## 코틀린 인증 관련 코드
코틀린, 스프링부트, 스프링 시큐리티를 이용한 인증 관련 코드입니다.  
spring security 의 기존 filter를 override 하여 인증을 처리하는 방식으로 제작됬습니다.

## Dependencies & Versions
- **Kotlin**: 1.9.25
- **Spring Boot**: 3.4.21
- **Spring Security**: 포함된 버전


## Flow
SecurityConfig 를 생성하여 필요한 filter 들을 override 하여 인증을 처리합니다.
1. **로그인**: UsernamePasswordAuthenticationFilter를 확장하는 LoginFilter를 생성하여 로그인에 필요한 매서드를 override 하여 로직을 처리한다.
2. **로그아웃**: LogoutHandler를 확장하는 CustomLogoutHandler를 생성하여 logout 매서드를 override 하여 로직을 처리한다.
3. **인가**: 인가가 필요한 요청이 들어오면 OncePerRequestFilter를 확장하는 JwtAuthenticationFilter 생성하여 doFilterInternal 매서드를 override 하여 인가 로직을 처리한다.
   