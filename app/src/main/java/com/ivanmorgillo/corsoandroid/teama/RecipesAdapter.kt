package com.ivanmorgillo.corsoandroid.teama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class RecipesAdapter : RecyclerView.Adapter<RecipeViewHolder>() {
    private var recipes = emptyList<RecipeUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setRecipes(items: List<RecipeUI>) {
        recipes = items
        notifyDataSetChanged()
    }
}

class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title = itemView.findViewById<TextView>(R.id.recipe_title)
    private val image = itemView.findViewById<ImageView>(R.id.recipe_image)

    fun bind(item: RecipeUI) {
        title.text = item.title
        image.load(item.image)
    }
}
