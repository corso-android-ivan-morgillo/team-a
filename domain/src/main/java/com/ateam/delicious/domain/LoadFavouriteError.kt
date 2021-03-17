package com.ateam.delicious.domain

sealed class LoadFavouriteError {
    object NoFavouriteFound : LoadFavouriteError()
}
