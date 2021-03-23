package com.ivanmorgillo.corsoandroid.teama.recipe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ateam.delicious.domain.error.LoadRecipeError
import com.ateam.delicious.domain.repository.RecipesRepository
import com.ateam.delicious.domain.result.LoadRecipeResult.Failure
import com.ateam.delicious.domain.result.LoadRecipeResult.Success
import com.ivanmorgillo.corsoandroid.teama.Screens
import com.ivanmorgillo.corsoandroid.teama.Tracking
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenAction.NavigateToDetail
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenAction.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenStates.Content
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenStates.Error
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeScreenStates.Loading
import kotlinx.coroutines.launch
import java.util.*

class RecipeViewModel(private val repository: RecipesRepository, private val tracking: Tracking) : ViewModel() {
    private var recipes: List<RecipeUI>? = null
    val states = MutableLiveData<RecipeScreenStates>() // potremmo passarci direttamente loading
    val actions = SingleLiveEvent<RecipeScreenAction>()

    init {
        tracking.logScreen(Screens.Recipes)
    }

    fun send(event: RecipeScreenEvent) {
        when (event) {
            // deve ricevere la lista delle ricette. La view deve ricevere eventi e reagire a stati.
            is RecipeScreenEvent.OnReady -> loadContent(event.categoryName)
            is RecipeScreenEvent.OnRecipeClick -> onRecipeClick(event)
            is RecipeScreenEvent.OnRefresh -> loadContent(event.categoryName)
            is RecipeScreenEvent.OnRecipeSearch -> onRecipeSearch(event.query)
        }.exhaustive
    }

    private fun loadContent(categoryName: String) {
        states.postValue(Loading)
        viewModelScope.launch {
            val result = repository.loadRecipes(categoryName)
            when (result) {
                is Failure -> onFailure(result)
                is Success -> onSuccess(result)
            }.exhaustive
        }
    }

    private fun onSuccess(result: Success) {
        val recipes = result.recipes.map {
            RecipeUI(
                title = it.name,
                image = it.image,
                id = it.idMeal)
        }
        this.recipes = recipes
        states.postValue(Content(recipes))
    }

    private fun onRecipeClick(event: RecipeScreenEvent.OnRecipeClick) {
        tracking.logEvent("recipe_clicked")
        actions.postValue(NavigateToDetail(event.recipe))
    }

    private fun onRecipeSearch(query: String) {
        tracking.logEvent("recipe_search")
        val filteredRecipes = filter(recipes, query)
        states.postValue(Content(filteredRecipes))
    }

    private fun filter(originalList: List<RecipeUI>?, query: String): List<RecipeUI> {
        return originalList?.filter {
            it.title.toLowerCase(Locale.getDefault())
                .contains(query.toLowerCase(Locale.getDefault()).trim()) || query.isBlank()
        } ?: emptyList()
    }

    private fun onFailure(result: Failure) {
        states.postValue(Error)
        when (result.error) {
            LoadRecipeError.NoInternet -> actions.postValue(ShowNoInternetMessage)
            LoadRecipeError.NoRecipeFound -> actions.postValue(RecipeScreenAction.ShowNoRecipeFoundMessage)
            LoadRecipeError.ServerError -> actions.postValue(RecipeScreenAction.ShowServerErrorMessage)
            LoadRecipeError.SlowInternet -> actions.postValue(RecipeScreenAction.ShowSlowInternetMessage)
            LoadRecipeError.InterruptedRequest -> actions.postValue(RecipeScreenAction.ShowInterruptedRequestMessage)
        }.exhaustive
    }
}

sealed class RecipeScreenAction {
    data class NavigateToDetail(val recipe: RecipeUI) : RecipeScreenAction()
    object ShowNoInternetMessage : RecipeScreenAction()
    object ShowSlowInternetMessage : RecipeScreenAction()
    object ShowServerErrorMessage : RecipeScreenAction()
    object ShowInterruptedRequestMessage : RecipeScreenAction()
    object ShowNoRecipeFoundMessage : RecipeScreenAction()
}

sealed class RecipeScreenStates {
    // questi oggetti rappresentano la nostra schermata inequivocabilmente
    object Loading : RecipeScreenStates()
    object Error : RecipeScreenStates()

    // se la lista cambia dobbiamo usare una 'data class' quindi non usiamo 'object'
    data class Content(val recipes: List<RecipeUI>) : RecipeScreenStates()
}

sealed class RecipeScreenEvent {
    /** Usiamo la dataclass perchè abbiamo bisogno di passare un parametro */
    data class OnRecipeClick(val recipe: RecipeUI) : RecipeScreenEvent()
    data class OnReady(val categoryName: String) : RecipeScreenEvent()
    data class OnRefresh(val categoryName: String) : RecipeScreenEvent()
    data class OnRecipeSearch(val query: String) : RecipeScreenEvent()
}
