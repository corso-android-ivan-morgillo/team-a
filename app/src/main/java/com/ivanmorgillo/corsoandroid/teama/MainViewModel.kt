package com.ivanmorgillo.corsoandroid.teama

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teama.MainScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teama.MainScreenAction.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teama.MainScreenStates.Content
import com.ivanmorgillo.corsoandroid.teama.MainScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeError
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeError.NoInternet
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeError.NoRecipeFound
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeError.ServerError
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeError.SlowInternet
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeResult.Failure
import com.ivanmorgillo.corsoandroid.teama.network.LoadRecipeResult.Success
import kotlinx.coroutines.launch

class MainViewModel(val repository: RecipesRepository) : ViewModel() {
    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenAction>()

    fun send(event: MainScreenEvent) {
        when (event) {
            // deve ricevere la lista delle ricette. La view deve ricevere eventi e reagire a stati.
            MainScreenEvent.OnReady -> {
                states.postValue(Loading)
                viewModelScope.launch {
                    val result = repository.loadRecipes()
                    when (result) {
                        is Failure -> onFailure(result)
                        is Success -> onSuccess(result)
                    }.exhaustive
                }
            }
            is MainScreenEvent.OnRecipeClick -> {
                onRecipeClick(event)
            }
        }
    }

    private fun onRecipeClick(event: MainScreenEvent.OnRecipeClick) {
        // Log.d("RECIPE", event.recipe.toString())
        actions.postValue(NavigateToDetail(event.recipe))
    }

    private fun onFailure(result: Failure) {
        when (result.error) {
            NoInternet -> {
                actions.postValue(ShowNoInternetMessage)
            }
            NoRecipeFound -> TODO()
            ServerError -> actions.postValue(MainScreenAction.ShowServerErrorMessage)
            SlowInternet -> actions.postValue(MainScreenAction.ShowSlowInternetMessage)
            LoadRecipeError.InterruptedRequest -> actions.postValue(MainScreenAction.ShowInterruptedRequestMessage)
        }.exhaustive
    }

    private fun onSuccess(result: Success) {
        val recipes = result.recipes.map {
            RecipeUI(
                title = it.name,
                image = it.image
            )
        }
        states.postValue(Content(recipes))
    }
}

sealed class MainScreenAction {
    data class NavigateToDetail(val recipe: RecipeUI) : MainScreenAction()
    object ShowNoInternetMessage : MainScreenAction()
    object ShowSlowInternetMessage : MainScreenAction()
    object ShowServerErrorMessage : MainScreenAction()
    object ShowInterruptedRequestMessage : MainScreenAction()
}

sealed class MainScreenStates {
    // questi oggetti rappresentano la nostra schermata inequivocabilmente
    object Loading : MainScreenStates()
    object Error : MainScreenStates()

    // se la lista cambia dobbiamo usare una 'data class' quindi non usiamo 'object'
    data class Content(val recipes: List<RecipeUI>) : MainScreenStates()
}

sealed class MainScreenEvent {
    /** Usiamo la dataclass perchè abbiamo bisogno di passare un parametro */
    data class OnRecipeClick(val recipe: RecipeUI) : MainScreenEvent()

    object OnReady : MainScreenEvent()
}
