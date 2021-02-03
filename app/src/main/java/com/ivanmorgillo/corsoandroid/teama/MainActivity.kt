package com.ivanmorgillo.corsoandroid.teama

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // dobbiamo creare un binding alla UI
        val adapter = RecipesAdapter()
        recipe_list.adapter = adapter

        val recipesList = viewModel.getRecipes()
        adapter.setRecipes(recipesList)

    }
}

data class RecipeUI(
    val title: String,
    val image: String
)