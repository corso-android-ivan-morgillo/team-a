package com.ivanmorgillo.corsoandroid.teama.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ateam.delicious.domain.NetworkAPI
import kotlinx.coroutines.launch

class IngredientViewModel(val api: NetworkAPI) : ViewModel() {

    val states = MutableLiveData<IngredientScreenEvent>()

    fun send(event: IngredientScreenEvent) {

        when (event) {
            IngredientScreenEvent.OnReady -> TODO()
            is IngredientScreenEvent.OnResearch -> {

                //Attenzione, se la ricetta/le ricette non esistono per quell ingrediente
                //Mostrate un alert / qualcosa che dica all'utente "NORESULTS"
                loadRecipesByIngredient(event.ingredientTextSearch)
            }
        }
    }

    private fun loadRecipesByIngredient(ingredient: String) {
        viewModelScope.launch { api.loadRecipesByIngredient(ingredient) }
    }

}

sealed class IngredientScreenEvent {

    object OnReady : IngredientScreenEvent()
    data class OnResearch(val ingredientTextSearch: String) : IngredientScreenEvent()
}
