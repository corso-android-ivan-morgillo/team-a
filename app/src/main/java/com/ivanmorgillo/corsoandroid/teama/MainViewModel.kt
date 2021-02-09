package com.ivanmorgillo.corsoandroid.teama

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(val repository: RecipesRepository) : ViewModel() {
    val states = MutableLiveData<MainScreenStates>()
    val actions = SingleLiveEvent<MainScreenAction>()

    fun send(event: MainScreenEvent) {
        when (event) {
            // deve ricevere la lista delle ricette. La view deve ricevere eventi e reagire a stati
            MainScreenEvent.OnReady -> {
                viewModelScope.launch {
                    val recipes = repository.loadRecipes().map {
                        RecipeUI(
                            title = it.name,
                            image = it.image
                        )
                    }
                    states.postValue(MainScreenStates.Content(recipes))
                }
            }
            is MainScreenEvent.OnRecipeClick -> {
                Log.d("RECIPE", event.recipe.toString())
                actions.postValue(MainScreenAction.NavigateToDetail(event.recipe))
            }
        }
    }
}

sealed class MainScreenAction {
    data class NavigateToDetail(val recipe: RecipeUI) : MainScreenAction()
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
