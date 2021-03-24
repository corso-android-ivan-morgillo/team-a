package com.ateam.delicious.domain.repository

import com.ateam.delicious.domain.NetworkAPI
import com.ateam.delicious.domain.result.LoadRecipeResult

interface RecipeByArea {

    suspend fun loadRecipesByArea(areaName: String): LoadRecipeResult
}

class RecipeByAreaImpl(private val api: NetworkAPI) : RecipeByArea {
    override suspend fun loadRecipesByArea(areaName: String): LoadRecipeResult {
        return api.loadRecipesByArea(areaName)
    }


}
