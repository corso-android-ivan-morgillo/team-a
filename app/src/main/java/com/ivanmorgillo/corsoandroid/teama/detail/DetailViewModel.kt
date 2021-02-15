package com.ivanmorgillo.corsoandroid.teama

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetails
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetailsRepository
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetailsUI
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeDetailsResult
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: RecipeDetailsRepository) : ViewModel() {

    val states = MutableLiveData<DetailScreenStates>()

    fun send(event: DetailScreenEvent) {
        when (event) {
            is DetailScreenEvent.OnReady -> {
                loadContent(event.idMeal)
            }
        }
    }

    private fun loadContent(idMeal: Long) {
        states.postValue(DetailScreenStates.Loading)
        viewModelScope.launch {
            val result = repository.loadRecipeDetails(idMeal)
            when (result) {
                is LoadRecipeDetailsResult.Failure -> TODO() //onFailure(result)
                is LoadRecipeDetailsResult.Success -> onSuccess(result)
            }.exhaustive
        }
    }

    private fun onSuccess(result: LoadRecipeDetailsResult.Success) {
        val details: RecipeDetails = result.details
        val detailUI: RecipeDetailsUI = RecipeDetailsUI(
            details.idMeal,
            details.name,
            details.image,
            details.ingredients,
            details.measures,
            details.instructions
        )
        states.postValue(DetailScreenStates.Content(detailUI))
    }
}

sealed class DetailScreenStates {
    // questi oggetti rappresentano la nostra schermata inequivocabilmente
    object Loading : DetailScreenStates()
    object Error : DetailScreenStates()

    // se la lista cambia dobbiamo usare una 'data class' quindi non usiamo 'object'
    data class Content(val recipes: RecipeDetailsUI) : DetailScreenStates()
}

sealed class DetailScreenEvent {
    data class OnReady(val idMeal: Long) : DetailScreenEvent()
}
