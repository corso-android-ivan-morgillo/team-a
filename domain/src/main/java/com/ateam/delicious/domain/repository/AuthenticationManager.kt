package com.ateam.delicious.domain.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface AuthenticationManager {

    fun getUid(): String?
    fun isUserLoggedIn(): Boolean
}

class AuthenticationManagerImpl() : AuthenticationManager {
    override fun getUid(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override fun isUserLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }


}
