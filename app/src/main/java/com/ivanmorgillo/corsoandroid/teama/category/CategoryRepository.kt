package com.ivanmorgillo.corsoandroid.teama.category

import com.ivanmorgillo.corsoandroid.teama.network.LoadCategoryResult
import com.ivanmorgillo.corsoandroid.teama.network.RecipeAPI

interface CategoryRepository {
    suspend fun loadCategories(): LoadCategoryResult
}

class CategoryRepositoryImpl(private val api: RecipeAPI) : CategoryRepository {
    override suspend fun loadCategories(): LoadCategoryResult {
        return api.loadCategories()
    }
}

data class Category(val name: String, val image: String, val id: String)
