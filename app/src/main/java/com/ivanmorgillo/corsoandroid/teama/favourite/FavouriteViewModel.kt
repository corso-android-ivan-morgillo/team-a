package com.ivanmorgillo.corsoandroid.teama.favourite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teama.Tracking
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.exhaustive
import kotlinx.coroutines.launch

class FavouriteViewModel(
    private val repository: FavouriteRepository,
    private val tracking: Tracking,
) : ViewModel() {
    val states = MutableLiveData<FavouriteScreenStates>() // potremmo passarci direttamente loading
    val actions = SingleLiveEvent<FavouriteScreenAction>()

    fun send(event: FavouriteScreenEvent) {
        when (event) {
            // deve ricevere la lista delle ricette. La view deve ricevere eventi e reagire a stati.
            is FavouriteScreenEvent.OnReady -> {
                loadContent() //  carica i preferiti
            }
            is FavouriteScreenEvent.OnFavouriteClick -> {
                onFavouriteClick(event)
            }
        }
    }

    private fun loadContent() {
        states.postValue(FavouriteScreenStates.Loading)
        viewModelScope.launch {
            val result = repository.loadFavourites()
            when (result) {
                is LoadFavouriteResult.Failure -> onFailure(result)
                is LoadFavouriteResult.Success -> onSuccess(result)
            }.exhaustive
        }
    }

    private fun onFavouriteClick(event: FavouriteScreenEvent.OnFavouriteClick) {
        // Log.d("RECIPE", event.recipe.toString())
        tracking.logEvent("favourite_clicked")
        actions.postValue(FavouriteScreenAction.NavigateToDetail(event.favourite))
    }

    private fun onFailure(result: LoadFavouriteResult.Failure) {
        states.postValue(FavouriteScreenStates.Error)
        when (result.error) {
            LoadFavouriteError.NoFavouriteFound -> actions.postValue(FavouriteScreenAction.ShowNoFavouriteFoundMessage)
        }.exhaustive
    }

    private fun onSuccess(result: LoadFavouriteResult.Success) {
        val favourites = result.favourites.map {
            FavouriteUI(
                title = it.name,
                image = it.image,
                id = it.idMeal,
                notes = it.notes,
                video = it.video,
                ingredients = it.ingredients,
                instructions = it.instructions
            )
        }
        states.postValue(FavouriteScreenStates.Content(favourites))
    }
}

sealed class FavouriteScreenAction {
    data class NavigateToDetail(val favourite: FavouriteUI) : FavouriteScreenAction()
    object ShowNoFavouriteFoundMessage : FavouriteScreenAction()
}

sealed class FavouriteScreenStates {
    // questi oggetti rappresentano la nostra schermata inequivocabilmente
    object Loading : FavouriteScreenStates()
    object Error : FavouriteScreenStates()

    // se la lista cambia dobbiamo usare una 'data class' quindi non usiamo 'object'
    data class Content(val favourites: List<FavouriteUI>) : FavouriteScreenStates()
}

sealed class FavouriteScreenEvent {
    /** Usiamo la dataclass perchè abbiamo bisogno di passare un parametro */
    data class OnFavouriteClick(val favourite: FavouriteUI) : FavouriteScreenEvent()

    object OnReady : FavouriteScreenEvent()
}

sealed class LoadFavouriteResult {
    data class Success(val favourites: List<Favourite>) : LoadFavouriteResult()
    data class Failure(val error: LoadFavouriteError) : LoadFavouriteResult()
}

sealed class LoadFavouriteError {
    object NoFavouriteFound : LoadFavouriteError()
}
