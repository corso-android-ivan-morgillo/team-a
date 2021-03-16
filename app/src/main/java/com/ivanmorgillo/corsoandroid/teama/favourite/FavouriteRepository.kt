package com.ivanmorgillo.corsoandroid.teama.favourite

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetails
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface FavouriteRepository {
    suspend fun loadAll(): LoadFavouriteResult
    suspend fun add(favourite: RecipeDetails): Boolean
    suspend fun delete(idMeal: Long): Boolean
    suspend fun isFavourite(idMeal: Long): Boolean
}

class FavouriteRepositoryImpl(private val context: Context, private val gson: Gson) :
    FavouriteRepository {
    private val fireStoreDatabase by lazy {
        Firebase.firestore
    }
    private val favouriteCollection by lazy {
        val universalUserId = Firebase.auth.currentUser.uid
        fireStoreDatabase.collection("favourites-$universalUserId")
    }

    override suspend fun loadAll(): LoadFavouriteResult {
        val x = favouriteCollection
            .get()
            .await()
            .documents
            .map {
                it.data
                val name = it["name"] as String
                val id = it["id"] as Long
                val image = it["image"] as String
                RecipeDetails(
                    name = name,
                    image = image,
                    video = "",
                    idMeal = id,
                    ingredients = emptyList(),
                    instructions = "",
                    area = ""
                )
            }

        return LoadFavouriteResult.Success(x)
    }

    override suspend fun isFavourite(idMeal: Long): Boolean {
        val x = favouriteCollection
            .document(idMeal.toString())
            .get()
            .await()
        Timber.d("x is favourite --> $x")
        return x.exists()
    }


    override suspend fun add(favourite: RecipeDetails): Boolean {

        val favouriteMap = hashMapOf(

            "id" to favourite.idMeal,
            "name" to favourite.name,
            "image" to favourite.image

        )

        favouriteCollection
            .document(favourite.idMeal.toString())
            .set(favouriteMap)
            .await()
        return true

    }

    override suspend fun delete(idMeal: Long): Boolean {
        favouriteCollection
            .document(idMeal.toString())
            .delete()
            .await()
        return true
    }
}
