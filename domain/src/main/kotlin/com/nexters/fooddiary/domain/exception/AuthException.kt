package com.nexters.fooddiary.domain.exception

sealed class AuthException : Exception() {
    data class InvalidToken(override val message: String? = "Token is invalid") : AuthException()
}
