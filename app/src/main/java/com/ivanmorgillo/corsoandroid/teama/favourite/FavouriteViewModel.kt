package com.ivanmorgillo.corsoandroid.teama.favourite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teama.Screens
import com.ivanmorgillo.corsoandroid.teama.Tracking
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetails
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import kotlinx.coroutines.launch
import timber.log.Timber

class FavouriteViewModel(private val repository: FavouriteRepository, private val tracking: Tracking) : ViewModel() {
    private var favourites: List<FavouriteUI>? = null
    val states = MutableLiveData<FavouriteScreenStates>() // potremmo passarci direttamente loading
    val actions = SingleLiveEvent<FavouriteScreenAction>()

    init {
        tracking.logScreen(Screens.Favourites)
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun send(event: FavouriteScreenEvent) {
        when (event) {
            // deve ricevere la lista delle ricette. La view deve ricevere eventi e reagire a stati.
            is FavouriteScreenEvent.OnReady -> loadContent() // carica i preferiti
            is FavouriteScreenEvent.OnFavouriteClick -> onFavouriteClick(event) // apri dettaglio ricetta
            is FavouriteScreenEvent.OnFavouriteSwiped -> onFavouriteSwiped(event.position) // elimina preferito
            is FavouriteScreenEvent.OnUndoDeleteFavourite -> onUndoDeleteFavourite(event.deletedFavourite)
        }.exhaustive
    }

    private fun loadContent() {
        states.postValue(FavouriteScreenStates.Loading)
        viewModelScope.launch {
            val result = repository.loadAll()
            when (result) {
                is LoadFavouriteResult.Failure -> onFailure(result)
                is LoadFavouriteResult.Success -> onSuccess(result.favourites)
            }.exhaustive
        }
    }

    private fun onSuccess(details: List<RecipeDetails>) {
        val favourites = details.map {
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
        this.favourites = favourites
        states.postValue(FavouriteScreenStates.Content(favourites, null))
    }

    private fun onFavouriteSwiped(position: Int) {
        val recipeToDelete = favourites?.get(position)
        if (recipeToDelete == null) {
            Timber.d("recipe null")
            return
        }
        else {
            tracking.logEvent("favourite_deleted")
            viewModelScope.launch {
                repository.delete(recipeToDelete.id)
                val updatedFavourites = favourites?.minus(recipeToDelete)
                if (updatedFavourites != null) {
                    favourites = updatedFavourites
                    states.postValue(FavouriteScreenStates.Content(updatedFavourites, recipeToDelete))
                } else {
                    Timber.e("updatedFavourites was null")
                }
            }
        }
    }

    private fun onUndoDeleteFavourite(removedFavourite: FavouriteUI) {
        viewModelScope.launch {
            val newFavourite = RecipeDetails(
                name = removedFavourite.title,
                image = removedFavourite.image,
                video = removedFavourite.video,
                idMeal = removedFavourite.id,
                ingredients = removedFavourite.ingredients,
                instructions = removedFavourite.instructions,
                area = "",
                notes = removedFavourite.notes
            )
            repository.add(newFavourite)
            val currentState = states.value
            if (currentState != null && currentState is FavouriteScreenStates.Content) {
                val updatedFavourites = favourites?.plus(removedFavourite)
                if (updatedFavourites != null) {
                    favourites = updatedFavourites
                    states.postValue(FavouriteScreenStates.Content(updatedFavourites, null))
                } else {
                    Timber.e("updatedFavourites was null")
                }
            }
        }
    }

    private fun onFavouriteClick(event: FavouriteScreenEvent.OnFavouriteClick) {
        tracking.logEvent("favourite_clicked")
        actions.postValue(FavouriteScreenAction.NavigateToDetail(event.favourite))
    }

    private fun onFailure(result: LoadFavouriteResult.Failure) {
        states.postValue(FavouriteScreenStates.Error)
        when (result.error) {
            LoadFavouriteError.NoFavouriteFound -> actions.postValue(FavouriteScreenAction.ShowNoFavouriteFoundMessage)
        }.exhaustive
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
    data class Content(val favourites: List<FavouriteUI>, val deletedFavourite: FavouriteUI? = null) :
        FavouriteScreenStates()
}

sealed class FavouriteScreenEvent {
    /** Usiamo la dataclass perchè abbiamo bisogno di passare un parametro */
    data class OnFavouriteClick(val favourite: FavouriteUI) : FavouriteScreenEvent()
    data class OnFavouriteSwiped(val position: Int) : FavouriteScreenEvent()
    data class OnUndoDeleteFavourite(val deletedFavourite: FavouriteUI) : FavouriteScreenEvent()

    object OnReady : FavouriteScreenEvent()
}

sealed class LoadFavouriteResult {
    data class Success(val favourites: List<RecipeDetails>) : LoadFavouriteResult()
    data class Failure(val error: LoadFavouriteError) : LoadFavouriteResult()
}

sealed class LoadFavouriteError {
    object NoFavouriteFound : LoadFavouriteError()
}
