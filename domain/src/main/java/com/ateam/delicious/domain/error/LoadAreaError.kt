package com.ateam.delicious.domain.error

sealed class LoadAreaError {
    object NoAreaFound : LoadAreaError()
    object NoInternet : LoadAreaError()
    object InterruptedRequest : LoadAreaError()
    object SlowInternet : LoadAreaError()
    object ServerError : LoadAreaError()
}
