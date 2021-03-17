package com.ateam.delicious.domain.result

import com.ateam.delicious.domain.Category
import com.ateam.delicious.domain.error.LoadCategoryError

sealed class LoadCategoryResult {
    data class Success(val categories: List<Category>) : LoadCategoryResult()
    data class Failure(val error: LoadCategoryError) : LoadCategoryResult()
}
