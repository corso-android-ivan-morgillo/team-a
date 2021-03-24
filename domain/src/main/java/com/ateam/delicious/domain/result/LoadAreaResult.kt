package com.ateam.delicious.domain.result

import com.ateam.delicious.domain.Area
import com.ateam.delicious.domain.error.LoadAreaError

sealed class LoadAreaResult {
    data class Success(val areas: List<Area>) : LoadAreaResult()
    data class Failure(val error: LoadAreaError) : LoadAreaResult()
}
