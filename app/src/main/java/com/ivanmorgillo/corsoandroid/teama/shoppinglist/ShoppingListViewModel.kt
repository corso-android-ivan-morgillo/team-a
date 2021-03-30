package com.ivanmorgillo.corsoandroid.teama.shoppinglist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.shoppinglist.ShoppingListStates.Content
import timber.log.Timber

class ShoppingListViewModel : ViewModel() {
    // private var shoppingList: List<IngredientUI>? = null
    val state = MutableLiveData<ShoppingListStates>()
    val actions = MutableLiveData<ShoppingListActions>()

    fun send(event: ShoppingListEvent) {
        when (event) {
            ShoppingListEvent.OnReady -> {
                Timber.d("sono nella onready")
                state.postValue(Content(shoppingList))
            }
        }.exhaustive
    }

    private val shoppingList = (1..10)
        .map {
            ShoppingListUI(
                ingredientName = "patate$it",
                ingredientQuantity = "500gr",
                isChecked = true
            )
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
