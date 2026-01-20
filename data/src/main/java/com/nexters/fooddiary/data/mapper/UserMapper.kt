package com.nexters.fooddiary.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.nexters.fooddiary.domain.model.User
import javax.inject.Inject

class UserMapper @Inject constructor() {
    fun toDomainUser(firebaseUser: FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email,
            displayName = firebaseUser.displayName,
            photoUrl = firebaseUser.photoUrl?.toString()
        )
    }
}
