package com.ateam.delicious.domain.result

import com.ateam.delicious.domain.RecipeDetails
import com.ateam.delicious.domain.error.LoadFavouriteError

sealed class LoadFavouriteResult {
    data class Success(val favourites: List<RecipeDetails>) : LoadFavouriteResult()
    data class Failure(val error: LoadFavouriteError) : LoadFavouriteResult()
}
