package com.ateam.delicious.domain.repository

import com.ateam.delicious.domain.RecipeDetails
import com.ateam.delicious.domain.result.LoadFavouriteResult
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface FavouriteRepository {
    suspend fun loadAll(): LoadFavouriteResult
    suspend fun add(favourite: RecipeDetails): Boolean
    suspend fun delete(idMeal: Long): Boolean
    suspend fun isFavourite(idMeal: Long): Boolean
}

class FavouriteRepositoryImpl(
    private val fireStoreDatabase: FirebaseFirestore,
    private val authManager: AuthenticationManager
) : FavouriteRepository {

    private var favouriteCollection: CollectionReference? = authManager.getFavouriteCollection(fireStoreDatabase)

    override suspend fun loadAll(): LoadFavouriteResult {
        Timber.d("Add Sono in load all con questo Uid ${authManager.getUid()}")
        favouriteCollection = authManager.getFavouriteCollection(fireStoreDatabase)
        if (favouriteCollection != null) {
            val x = favouriteCollection!!
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
        } else {
            return LoadFavouriteResult.Success(emptyList())
        }
    }

    override suspend fun isFavourite(idMeal: Long): Boolean {
        favouriteCollection = authManager.getFavouriteCollection(fireStoreDatabase)
        return if (favouriteCollection != null) {
            val x = favouriteCollection!!
                .document(idMeal.toString())
                .get()
                .await()
            Timber.d("x is favourite --> $x")
            x.exists()
        } else {
            false
        }
    }

    override suspend fun add(favourite: RecipeDetails): Boolean {
        Timber.d("Add prima riga!")
        if (!authManager.isUserLoggedIn()) return false

        favouriteCollection = authManager.getFavouriteCollection(fireStoreDatabase)

        Timber.d("Add Dopo il guard!")
        val favouriteMap = hashMapOf(

            "id" to favourite.idMeal,
            "name" to favourite.name,
            "image" to favourite.image

        )
        return if (favouriteCollection != null) {
            Timber.d("Add sto settando la collection in add.")
            favouriteCollection!!
                .document(favourite.idMeal.toString())
                .set(favouriteMap)
                .await()
            true
        } else {
            false
        }

    }

    override suspend fun delete(idMeal: Long): Boolean {
        if (!authManager.isUserLoggedIn()) return false
        favouriteCollection = authManager.getFavouriteCollection(fireStoreDatabase)
        return if (favouriteCollection != null) {
            favouriteCollection!!
                .document(idMeal.toString())
                .delete()
                .await()
            true
        } else {
            false
        }

    }
}
