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
import com.ivanmorgillo.corsoandroid.teama.category.AreaScreenAction.ShowInterruptedRequestMessage
import com.ivanmorgillo.corsoandroid.teama.category.AreaScreenAction.ShowNoCategoryFoundMessage
import com.ivanmorgillo.corsoandroid.teama.category.AreaScreenAction.ShowNoInternetMessage
import com.ivanmorgillo.corsoandroid.teama.category.AreaScreenAction.ShowServerErrorMessage
import com.ivanmorgillo.corsoandroid.teama.category.AreaScreenAction.ShowSlowInternetMessage
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenStates.Content
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenStates.Error
import com.ivanmorgillo.corsoandroid.teama.category.CategoryScreenStates.Loading
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: CategoryRepository, private val tracking: Tracking) : ViewModel() {

    val states = MutableLiveData<CategoryScreenStates>() // potremmo passarci direttamente loading
    val actions = SingleLiveEvent<AreaScreenAction>()

    init {
        tracking.logScreen(Screens.Category)
    }

    fun send(event: AreaScreenEvent) {
        when (event) {
            // deve ricevere la lista delle ricette. La view deve ricevere eventi e reagire a stati.
            AreaScreenEvent.OnReady -> loadContent(false)
            is AreaScreenEvent.OnCategoryClick -> {
                tracking.logEvent("category_clicked")
                onCategoryClick(event)
            }
            AreaScreenEvent.OnRefresh -> {
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

    private fun onCategoryClick(event: AreaScreenEvent.OnCategoryClick) {
        actions.postValue(AreaScreenAction.NavigateToRecipes(event.category))
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

sealed class AreaScreenAction {
    data class NavigateToRecipes(val category: CategoryUI) : AreaScreenAction()
    object ShowNoInternetMessage : AreaScreenAction()
    object ShowSlowInternetMessage : AreaScreenAction()
    object ShowServerErrorMessage : AreaScreenAction()
    object ShowInterruptedRequestMessage : AreaScreenAction()
    object ShowNoCategoryFoundMessage : AreaScreenAction()
}

sealed class CategoryScreenStates {
    // questi oggetti rappresentano la nostra schermata inequivocabilmente
    object Loading : CategoryScreenStates()
    object Error : CategoryScreenStates()

    // se la lista cambia dobbiamo usare una 'data class' quindi non usiamo 'object'
    data class Content(val categories: List<CategoryUI>) : CategoryScreenStates()
}

sealed class AreaScreenEvent {
    /** Usiamo la dataclass perchè abbiamo bisogno di passare un parametro */
    data class OnCategoryClick(val category: CategoryUI) : AreaScreenEvent()
    object OnReady : AreaScreenEvent()
    object OnRefresh : AreaScreenEvent()
}
