package com.ivanmorgillo.corsoandroid.teama.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teama.databinding.RecipeItemBinding

class IngredientAdapter(private val onClick: (RecipeByIngredientUI, View) -> Unit) : RecyclerView.Adapter<RecipeByIngredientViewHolder>() {

    var recipes = emptyList<RecipeByIngredientUI>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeByIngredientViewHolder {
        val binding = RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeByIngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeByIngredientViewHolder, position: Int) {
        holder.bind(recipes[position], onClick)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setRecipesByIngredient(items: List<RecipeByIngredientUI>) {
        recipes = items
        notifyDataSetChanged()
    }
}

class RecipeByIngredientViewHolder(private val binding: RecipeItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: RecipeByIngredientUI, onClick: (RecipeByIngredientUI, View) -> Unit) {
        binding.recipeTitle.text = item.title
        binding.recipeImage.load(item.image)
        binding.root.setOnClickListener {
            onClick(item, it)
        }
    }

}
