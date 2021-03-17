package com.ateam.delicious.domain

sealed class LoadRecipeDetailError {
    object NoDetailFound : LoadRecipeDetailError()
    object NoInternet : LoadRecipeDetailError()
    object InterruptedRequest : LoadRecipeDetailError()
    object SlowInternet : LoadRecipeDetailError()
    object ServerError : LoadRecipeDetailError()
}
