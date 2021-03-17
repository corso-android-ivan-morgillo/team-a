package com.ateam.delicious.domain

sealed class LoadCategoryError {
    object NoCategoryFound : LoadCategoryError()
    object NoInternet : LoadCategoryError()
    object InterruptedRequest : LoadCategoryError()
    object SlowInternet : LoadCategoryError()
    object ServerError : LoadCategoryError()
}
