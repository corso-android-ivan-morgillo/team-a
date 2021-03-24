package com.ateam.delicious.domain.repository

import com.ateam.delicious.domain.NetworkAPI
import com.ateam.delicious.domain.result.LoadAreaResult

interface AreaRepository {
    suspend fun loadAreas(forced: Boolean): LoadAreaResult
}

class AreaRepositoryImpl(private val api: NetworkAPI) : AreaRepository {
    private var cache: LoadAreaResult? = null
    override suspend fun loadAreas(forced: Boolean): LoadAreaResult {
        return if (cache == null || forced) {
            val result = api.loadAreas()
            if (result is LoadAreaResult.Success) { // se il nuovo caricamento è avvenuto con successo
                cache = result // aggiorna la cache
                result // e ritorna il nuovo risultato
            } else {
                cache = null // necessario affinchè la cache venga azzerata nel caso in cui ricarico senza internet
                result // e ritorna l'errore
            }
        } else {
            cache!!
        }
    }
}
