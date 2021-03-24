package com.ivanmorgillo.corsoandroid.teama.recipe.area

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teama.databinding.RecipeAreaItemBinding
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeUI

class RecipesAreaAdapter(private val onclick: (RecipeUI, View) -> Unit) : RecyclerView.Adapter<RecipeAreaViewHolder>() {
    private var areaRecipes = emptyList<RecipeUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeAreaViewHolder {
        val binding = RecipeAreaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeAreaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeAreaViewHolder, position: Int) {
        holder.bind(areaRecipes[position], onclick)
    }

    override fun getItemCount(): Int {
        return areaRecipes.size
    }

    fun setRecipes(items: List<RecipeUI>) {
        areaRecipes = items
        notifyDataSetChanged()
    }
}

class RecipeAreaViewHolder(private val binding: RecipeAreaItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: RecipeUI, onclick: (RecipeUI, View) -> Unit) {
        binding.recipeAreaTitle.text = item.title
        binding.recipeAreaImage.load(item.image)
        binding.recipeAreaImage.contentDescription = item.title
        binding.recipeAreaRoot.setOnClickListener { onclick(item, it) }
        // binding.recipeAreaRoot.transitionName = "recipe_transition_item${item.id}"
    }
}
