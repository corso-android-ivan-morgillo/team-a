package com.ivanmorgillo.corsoandroid.teama.shoppinglist

import timber.log.Timber

interface ShoppingListRepository {
    suspend fun loadAll(): List<ShoppingListUI>
}

class ShoppingListRepositoryImpl : ShoppingListRepository {
    override suspend fun loadAll(): List<ShoppingListUI> {
//        val shoppingList: MutableList<ShoppingListUI>
        val shoppingList = (1..10)
            .map {
                ShoppingListUI(
                    ingredientName = "patate$it",
                    ingredientQuantity = "500gr",
                    isChecked = true
                )
            }
        Timber.e("Lista $shoppingList")
        return shoppingList
    }
}

data class ShoppingListUI(
    val isChecked: Boolean,
    val ingredientName: String,
    var ingredientQuantity: String
)
