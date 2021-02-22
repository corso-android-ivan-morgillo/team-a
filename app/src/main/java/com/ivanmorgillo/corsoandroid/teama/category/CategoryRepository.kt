package com.ivanmorgillo.corsoandroid.teama.category

import com.ivanmorgillo.corsoandroid.teama.network.LoadCategoryResult
import com.ivanmorgillo.corsoandroid.teama.network.NetworkAPI

interface CategoryRepository {
    suspend fun loadCategories(): LoadCategoryResult
}

class CategoryRepositoryImpl(private val api: NetworkAPI) : CategoryRepository {
    override suspend fun loadCategories(): LoadCategoryResult {
        return api.loadCategories()
    }
}
