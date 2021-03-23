package com.ivanmorgillo.corsoandroid.teama.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ateam.delicious.domain.error.LoadCategoryError.InterruptedRequest
import com.ateam.delicious.domain.error.LoadCategoryError.NoCategoryFound
import com.ateam.delicious.domain.error.LoadCategoryError.NoInternet
import com.ateam.delicious.domain.error.LoadCategoryError.ServerError
import com.ateam.delicious.domain.error.LoadCategoryError.SlowInternet
import com.ateam.delicious.domain.repository.CategoryRepository
import com.ateam.delicious.domain.result.LoadCategoryResult
import com.ivanmorgillo.corsoandroid.teama.Screens
import com.ivanmorgillo.corsoandroid.teama.Tracking
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenAction.ShowInterruptedRequestMessage
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenAction.ShowNoCategoryFoundMessage
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenAction.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenAction.ShowServerErrorMessage
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenAction.ShowSlowInternetMessage
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenStates.Content
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenStates.Error
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: CategoryRepository, private val tracking: Tracking) : ViewModel() {

    val states = MutableLiveData<CategoryScreenStates>() // potremmo passarci direttamente loading
    val actions = SingleLiveEvent<CategoryScreenAction>()

    init {
        tracking.logScreen(Screens.Category)
    }

    fun send(event: CategoryScreenEvent) {
        when (event) {
            // deve ricevere la lista delle ricette. La view deve ricevere eventi e reagire a stati.
            CategoryScreenEvent.OnReady -> loadContent(false)
            is CategoryScreenEvent.OnCategoryClick -> {
                tracking.logEvent("category_clicked")
                onCategoryClick(event)
            }
            CategoryScreenEvent.OnRefresh -> {
                tracking.logEvent("category_refresh_clicked")
                loadContent(true)
            }
        }.exhaustive
    }

    private fun loadContent(forced: Boolean) {
        states.postValue(Loading)
        viewModelScope.launch {
            val result = repository.loadCategories(forced)
            when (result) {
                is LoadCategoryResult.Failure -> onFailure(result)
                is LoadCategoryResult.Success -> onSuccess(result)
            }.exhaustive
        }
    }

    private fun onCategoryClick(event: CategoryScreenEvent.OnCategoryClick) {
        actions.postValue(CategoryScreenAction.NavigateToRecipes(event.category))
    }

    private fun onFailure(result: LoadCategoryResult.Failure) {
        states.postValue(Error)
        when (result.error) {
            NoInternet -> actions.postValue(ShowNoInternetMessage)
            NoCategoryFound -> actions.postValue(ShowNoCategoryFoundMessage)
            ServerError -> actions.postValue(ShowServerErrorMessage)
            SlowInternet -> actions.postValue(ShowSlowInternetMessage)
            InterruptedRequest -> actions.postValue(ShowInterruptedRequestMessage)
        }.exhaustive
    }

    private fun onSuccess(result: LoadCategoryResult.Success) {
        val categories = result.categories.map { it ->
            CategoryUI(
                title = it.name,
                image = it.image,
                id = it.id.toLong(),
                recipesCount = it.recipeAmount
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
    object ShowNoCategoryFoundMessage : CategoryScreenAction()
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
    object OnRefresh : CategoryScreenEvent()
}
