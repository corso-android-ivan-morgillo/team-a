package com.ateam.delicious.domain.error

sealed class LoadFavouriteError {
    object NoFavouriteFound : LoadFavouriteError()
}
