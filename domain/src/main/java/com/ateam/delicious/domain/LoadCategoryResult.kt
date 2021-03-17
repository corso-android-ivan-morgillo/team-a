package com.ateam.delicious.domain

sealed class LoadCategoryResult {
    data class Success(val categories: List<Category>) : LoadCategoryResult()
    data class Failure(val error: LoadCategoryError) : LoadCategoryResult()
}
