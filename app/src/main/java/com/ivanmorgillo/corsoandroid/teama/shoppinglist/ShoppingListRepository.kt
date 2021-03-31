package com.ivanmorgillo.corsoandroid.teama.shoppinglist

import com.ateam.delicious.domain.repository.AuthenticationManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface ShoppingListRepository {
    suspend fun loadAll(): List<ShoppingListUI>
}

class ShoppingListRepositoryImpl(
    private val fireStoreDatabase: FirebaseFirestore,
    private val authManager: AuthenticationManager
) : ShoppingListRepository {
    private var shoppinglistCollection: CollectionReference? = authManager.getShoppingListCollection(fireStoreDatabase)
    override suspend fun loadAll(): List<ShoppingListUI> {
        Timber.d("sono nella loadAll")

/*        val shoppingList: MutableList<ShoppingListUI>
        val shoppingList = (1..20)
            .map {
                ShoppingListUI(
                    ingredientName = "patate$it",
                    ingredientQuantity = "500gr",
                    isChecked = false
                )
            }*/
        shoppinglistCollection = authManager.getShoppingListCollection(fireStoreDatabase)
        if (shoppinglistCollection != null) {
            Timber.d("non sono null")
            val x = shoppinglistCollection!!
                .get()
                .await()
                .documents
                .map {
                    Timber.d("IT: $it")
                    Timber.d("ITData: ${it.data}")
                    it.data?.map {
                        val ingredientName = it.key
                        val ingredientValues = it.value
                        
                        Timber.d("ingredient quantity ${it.value}")
                        ShoppingListUI(
                            ingredientName = ingredientName,
                            ingredientQuantity = "",
                            isChecked = false
                        )
                    }
                }
            Timber.d("sono dopo la x: ${x}")
        } else {
            Timber.d("sono null")
        }
        return emptyList()
    }
}

data class ShoppingListUI(
    val isChecked: Boolean,
    val ingredientName: String,
    var ingredientQuantity: String
)
