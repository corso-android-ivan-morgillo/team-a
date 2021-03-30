package com.ivanmorgillo.corsoandroid.teama.shoppinglist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ivanmorgillo.corsoandroid.teama.detail.IngredientUI
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import timber.log.Timber

class ShoppingListViewModel : ViewModel() {
    // private var shoppingList: List<IngredientUI>? = null
    val state = MutableLiveData<ShoppingListStates>()
    val actions = MutableLiveData<ShoppingListActions>()

    fun send(event: ShoppingListEvent) {
        when (event) {
            ShoppingListEvent.OnReady -> {
                Timber.d("sono nella onready")
            }
        }.exhaustive
    }
}

sealed class ShoppingListActions {}

sealed class ShoppingListEvent {
    object OnReady : ShoppingListEvent()
}

sealed class ShoppingListStates {
    data class Content(
        val shoppingList: List<IngredientUI>,
        val isChecked: Boolean,
        val deletedIngredient: IngredientUI? = null
    ) : ShoppingListStates()
}
