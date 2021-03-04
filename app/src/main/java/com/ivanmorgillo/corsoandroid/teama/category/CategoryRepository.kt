package com.ivanmorgillo.corsoandroid.teama.category

import com.ivanmorgillo.corsoandroid.teama.network.LoadCategoryResult
import com.ivanmorgillo.corsoandroid.teama.network.NetworkAPI

interface CategoryRepository {
    suspend fun loadCategories(forced: Boolean): LoadCategoryResult
}

class CategoryRepositoryImpl(private val api: NetworkAPI) : CategoryRepository {
    private var cache: LoadCategoryResult? = null
    override suspend fun loadCategories(forced: Boolean): LoadCategoryResult {
        return if (cache == null || forced) {
            val result = api.loadCategories()
            if (result is LoadCategoryResult.Success) { // se il nuovo caricamento è avvenuto con successo
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
