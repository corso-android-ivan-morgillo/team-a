package com.ivanmorgillo.corsoandroid.teama.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teama.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.Tracking
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenStates.Content
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenStates.Error
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teama.exhaustive
import com.ivanmorgillo.corsoandroid.teama.network.LoadCategoryError
import com.ivanmorgillo.corsoandroid.teama.network.LoadCategoryResult
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepository,
    private val tracking: Tracking,
) : ViewModel() {

    val states = MutableLiveData<CategoryScreenStates>() // potremmo passarci direttamente loading
    val actions = SingleLiveEvent<CategoryScreenAction>()


    fun send(event: CategoryScreenEvent) {
        when (event) {
            // deve ricevere la lista delle ricette. La view deve ricevere eventi e reagire a stati.
            CategoryScreenEvent.OnReady -> {
                loadContent()
            }
            /*is CategoryScreenEvent.OnRecipeClick -> {
                onRecipeClick(event)
            }*/
        }
    }

    private fun loadContent() {
        states.postValue(Loading)
        viewModelScope.launch {
            val result = repository.loadCategories()
            when (result) {
                is LoadCategoryResult.Failure -> onFailure(result)
                is LoadCategoryResult.Success -> onSuccess(result)
            }.exhaustive
        }
    }

    /*
        private fun onRecipeClick(event: CategoryScreenEvent.OnRecipeClick) {
            // Log.d("RECIPE", event.recipe.toString())
            tracking.logEvent("recipe_clicked")
            actions.postValue(NavigateToDetail(event.recipe))
        }
    */
    private fun onFailure(result: LoadCategoryResult.Failure) {
        states.postValue(Error)
        when (result.error) {
            LoadCategoryError.NoInternet -> actions.postValue(CategoryScreenAction.ShowNoInternetMessage)
            LoadCategoryError.NoCategoryFound -> actions.postValue(CategoryScreenAction.ShowNoRecipeFoundMessage)
            LoadCategoryError.ServerError -> actions.postValue(CategoryScreenAction.ShowServerErrorMessage)
            LoadCategoryError.SlowInternet -> actions.postValue(CategoryScreenAction.ShowSlowInternetMessage)
            LoadCategoryError.InterruptedRequest -> actions.postValue(CategoryScreenAction.ShowInterruptedRequestMessage)
        }.exhaustive
    }

    private fun onSuccess(result: LoadCategoryResult.Success) {
        val categories = result.categories.map {
            CategoryUI(
                title = it.name,
                image = it.image,
                id = it.id.toLong()
            )
        }
        states.postValue(Content(categories))
    }
}

sealed class CategoryScreenAction {
    data class NavigateToRecipes(val category: CategoryUI) : CategoryScreenAction()
    object ShowNoInternetMessage : CategoryScreenAction()
    object ShowSlowInternetMessage : CategoryScreenAction()
    object ShowServerErrorMessage : CategoryScreenAction()
    object ShowInterruptedRequestMessage : CategoryScreenAction()
    object ShowNoRecipeFoundMessage : CategoryScreenAction()
}

sealed class CategoryScreenStates {
    // questi oggetti rappresentano la nostra schermata inequivocabilmente
    object Loading : CategoryScreenStates()
    object Error : CategoryScreenStates()

    // se la lista cambia dobbiamo usare una 'data class' quindi non usiamo 'object'
    data class Content(val categories: List<CategoryUI>) : CategoryScreenStates()
}

sealed class CategoryScreenEvent {
    /** Usiamo la dataclass perchè abbiamo bisogno di passare un parametro */
    data class OnCategoryClick(val category: CategoryUI) : CategoryScreenEvent()

    object OnReady : CategoryScreenEvent()
}
