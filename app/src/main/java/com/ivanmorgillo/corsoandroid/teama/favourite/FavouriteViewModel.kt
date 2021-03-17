package com.ivanmorgillo.corsoandroid.teama.favourite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ateam.delicious.domain.RecipeDetails
import com.ateam.delicious.domain.error.LoadFavouriteError
import com.ateam.delicious.domain.repository.FavouriteRepository
import com.ateam.delicious.domain.repository.SettingsRepository
import com.ateam.delicious.domain.result.LoadFavouriteResult
import com.ivanmorgillo.corsoandroid.teama.Screens
import com.ivanmorgillo.corsoandroid.teama.Tracking
import com.ivanmorgillo.corsoandroid.teama.crashlytics.SingleLiveEvent
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class FavouriteViewModel(
    private val repository: FavouriteRepository,
    private val tracking: Tracking,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private var favourites: List<FavouriteUI>? = null
    val states = MutableLiveData<FavouriteScreenStates>()
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
            is FavouriteScreenEvent.OnFavouriteSearch -> onFavouriteSearch(event.query)
            FavouriteScreenEvent.OnRefresh -> { // ricarica i preferiti
                tracking.logEvent("favourite_refresh_clicked")
                loadContent()
            }
            FavouriteScreenEvent.OnDeleteMessageRead -> onDeleteMessageRead()
        }.exhaustive
    }

    private fun onDeleteMessageRead() {
        viewModelScope.launch {
            settingsRepository.setFavouriteMessageShown(true)
        }
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
                video = it.video,
                ingredients = it.ingredients,
                instructions = it.instructions,
                area = it.area
            )
        }
        this.favourites = favourites
        viewModelScope.launch {
            val isFavouriteMessageShown = settingsRepository.isFavouriteMessageShown()
            states.postValue(FavouriteScreenStates.Content(favourites, null, isFavouriteMessageShown))
        }
    }

    private fun onFavouriteClick(event: FavouriteScreenEvent.OnFavouriteClick) {
        tracking.logEvent("favourite_clicked")
        actions.postValue(FavouriteScreenAction.NavigateToDetail(event.favourite))
    }

    private fun onFavouriteSwiped(position: Int) {
        val favouriteToDelete = favourites?.get(position) ?: return
        tracking.logEvent("favourite_deleted")
        viewModelScope.launch {
            repository.delete(favouriteToDelete.id)
            val updatedFavourites = favourites?.minus(favouriteToDelete)
            if (updatedFavourites != null) {
                favourites = updatedFavourites
                states.postValue(
                    FavouriteScreenStates.Content(
                        updatedFavourites,
                        favouriteToDelete,
                        true
                    )
                )
            } else {
                Timber.e("updatedFavourites was null")
            }
        }
    }

    private fun onUndoDeleteFavourite(removedFavourite: FavouriteUI) {
        tracking.logEvent("favourite_undo_delete")
        viewModelScope.launch {
            val newFavourite = RecipeDetails(
                name = removedFavourite.title,
                image = removedFavourite.image,
                video = removedFavourite.video,
                idMeal = removedFavourite.id,
                ingredients = removedFavourite.ingredients,
                instructions = removedFavourite.instructions,
                area = removedFavourite.area,

                )
            repository.add(newFavourite)
            val currentState = states.value
            if (currentState != null && currentState is FavouriteScreenStates.Content) {
                val updatedFavourites = favourites?.plus(removedFavourite)
                if (updatedFavourites != null) {
                    favourites = updatedFavourites
                    states.postValue(FavouriteScreenStates.Content(updatedFavourites, null, true))
                } else {
                    Timber.e("updatedFavourites was null")
                }
            }
        }
    }

    private fun onFavouriteSearch(query: String) {
        tracking.logEvent("favourite_search_clicked")
        val filteredFavourites = filter(favourites, query)
        states.postValue(FavouriteScreenStates.Content(filteredFavourites, null, true))
    }

    private fun filter(originalList: List<FavouriteUI>?, query: String): List<FavouriteUI> {
        return originalList?.filter {
            it.title.toLowerCase(Locale.getDefault())
                .contains(query.toLowerCase(Locale.getDefault()).trim()) || query.isBlank()
        } ?: emptyList()
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
    data class Content(
        val favourites: List<FavouriteUI>,
        val deletedFavourite: FavouriteUI? = null,
        val isFavouriteMessageShown: Boolean
    ) : FavouriteScreenStates()
}

sealed class FavouriteScreenEvent {
    /** Usiamo la dataclass perchè abbiamo bisogno di passare un parametro */
    data class OnFavouriteClick(val favourite: FavouriteUI) : FavouriteScreenEvent()
    data class OnFavouriteSwiped(val position: Int) : FavouriteScreenEvent()
    data class OnUndoDeleteFavourite(val deletedFavourite: FavouriteUI) : FavouriteScreenEvent()
    data class OnFavouriteSearch(val query: String) : FavouriteScreenEvent()
    object OnDeleteMessageRead : FavouriteScreenEvent()

    object OnReady : FavouriteScreenEvent()
    object OnRefresh : FavouriteScreenEvent()
}
