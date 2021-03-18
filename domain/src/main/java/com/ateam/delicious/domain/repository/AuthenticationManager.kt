package com.ateam.delicious.domain.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

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
            Timber.d("Add sono in get collection ma non c'è Uid")
            null
        } else {
            Timber.d("Add sono in get collection sto provando a creare la coll con $universalUserId")
            dataBase.collection("favourites-$universalUserId")
        }
    }


}
