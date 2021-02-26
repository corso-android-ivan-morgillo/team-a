package com.ivanmorgillo.corsoandroid.teama.category

import com.ivanmorgillo.corsoandroid.teama.network.LoadCategoryResult
import com.ivanmorgillo.corsoandroid.teama.network.NetworkAPI

interface CategoryRepository {
    suspend fun loadCategories(forced: Boolean): LoadCategoryResult
}

class CategoryRepositoryImpl(private val api: NetworkAPI) : CategoryRepository {
    private var cache: LoadCategoryResult? = null
    override suspend fun loadCategories(forced: Boolean): LoadCategoryResult {
        return if (cache == null || forced) {
            val result = api.loadCategories()
            cache = result
            result
        } else {
            cache!!
        }
    }
}
