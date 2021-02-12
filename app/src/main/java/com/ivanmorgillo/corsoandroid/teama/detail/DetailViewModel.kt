package com.ivanmorgillo.corsoandroid.teama

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetailsRepository
import com.ivanmorgillo.corsoandroid.teama.home.RecipeUI
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
            /*when (result) {
                is LoadRecipeResult.Failure -> onFailure(result)
                is LoadRecipeResult.Success -> onSuccess(result)
            }.exhaustive*/
        }
    }
}

sealed class DetailScreenStates {
    // questi oggetti rappresentano la nostra schermata inequivocabilmente
    object Loading : DetailScreenStates()
    object Error : DetailScreenStates()

    // se la lista cambia dobbiamo usare una 'data class' quindi non usiamo 'object'
    data class Content(val recipes: List<RecipeUI>) : DetailScreenStates()
}

sealed class DetailScreenEvent {
    data class OnReady(val idMeal: Long) : DetailScreenEvent()
}
