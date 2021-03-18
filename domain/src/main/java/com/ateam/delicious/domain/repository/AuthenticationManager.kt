package com.ateam.delicious.domain.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

interface AuthenticationManager {

    fun getUid(): String?
    fun isUserLoggedIn(): Boolean
    fun getCollection(dataBase: FirebaseFirestore): CollectionReference?
}

class AuthenticationManagerImpl() : AuthenticationManager {
    override fun getUid(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override fun isUserLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override fun getCollection(dataBase: FirebaseFirestore): CollectionReference? {
        val universalUserId = getUid()
        return if (universalUserId == null) {
            null
        } else {
            dataBase.collection("favourites-$universalUserId")
        }
    }


}
