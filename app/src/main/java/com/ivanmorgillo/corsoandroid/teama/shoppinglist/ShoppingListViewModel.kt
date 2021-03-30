package com.ivanmorgillo.corsoandroid.teama.shoppinglist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.shoppinglist.ShoppingListStates.Content
import kotlinx.coroutines.launch
import timber.log.Timber

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {
    // private var shoppingList: List<IngredientUI>? = null
    val state = MutableLiveData<ShoppingListStates>()
    val actions = MutableLiveData<ShoppingListActions>()

    fun send(event: ShoppingListEvent) {
        when (event) {
            ShoppingListEvent.OnReady -> {
                Timber.d("sono nella onready")
                viewModelScope.launch {
                    val shoppingList = repository.loadAll()
                    state.postValue(Content(shoppingList))
                }
            }
        }.exhaustive
    }
}

sealed class ShoppingListActions
sealed class ShoppingListEvent {
    object OnReady : ShoppingListEvent()
}

sealed class ShoppingListStates {
    data class Content(
        val shoppingList: List<ShoppingListUI>,
    ) : ShoppingListStates()
}
