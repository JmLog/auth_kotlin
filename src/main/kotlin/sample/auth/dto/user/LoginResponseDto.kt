package sample.auth.dto.user

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginResponseDto(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("refresh_token")
    val refreshToken: String
)