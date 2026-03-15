package com.nexters.fooddiary.domain.model

sealed class DeleteAccountError {

    data object RecentLoginRequired : DeleteAccountError()

    data object NoUserSignedIn : DeleteAccountError()

    data class Unknown(val cause: Throwable) : DeleteAccountError()
}

class DeleteAccountException(
    val error: DeleteAccountError
) : Exception(
    when (error) {
        is DeleteAccountError.RecentLoginRequired -> "Recent login required"
        is DeleteAccountError.NoUserSignedIn -> "No user signed in"
        is DeleteAccountError.Unknown -> "Unknown delete account error: ${error.cause.message}"
    },
    (error as? DeleteAccountError.Unknown)?.cause
)

