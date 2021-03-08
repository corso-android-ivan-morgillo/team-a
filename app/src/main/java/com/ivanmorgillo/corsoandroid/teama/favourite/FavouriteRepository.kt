package com.ivanmorgillo.corsoandroid.teama.favourite

import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetails
import timber.log.Timber

interface FavouriteRepository {
    suspend fun loadFavourites(): LoadFavouriteResult
    suspend fun addFavourite(favourite: RecipeDetails): Boolean
    suspend fun deleteFavourite(favourite: RecipeDetails): Boolean
    suspend fun isFavourite(idMeal: Long): Boolean
}

class FavouriteRepositoryImpl : FavouriteRepository {
    private val favourites: MutableList<RecipeDetails> = mutableListOf()

    override suspend fun loadFavourites(): LoadFavouriteResult {
        Timber.d("loading favourites")
        return LoadFavouriteResult.Success(favourites)
    }

    override suspend fun isFavourite(idMeal: Long): Boolean {
        val recipeDetail = favourites.find {
            idMeal == it.idMeal
        }
        val isFavourite = recipeDetail != null
        Timber.d("is favourite: $isFavourite")
        return isFavourite
    }

    override suspend fun addFavourite(favourite: RecipeDetails): Boolean {
        Timber.d("added favourite")
        favourites.add(favourite)
        return true
    }

    override suspend fun deleteFavourite(favourite: RecipeDetails): Boolean {
        Timber.d("deleted favourite")
        favourites.remove(favourite)
        return true
    }
}
