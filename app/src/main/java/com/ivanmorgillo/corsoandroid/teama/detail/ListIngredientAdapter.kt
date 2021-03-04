package com.ivanmorgillo.corsoandroid.teama.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teama.databinding.IngredientDetailItemBinding

data class IngredientUI(
    val ingredientName: String,
    val ingredientQuantity: String,
    val ingredientImage: String,
)

class ListIngredientAdapter : RecyclerView.Adapter<ListIngredientViewHolder>() {
    private var ingredients = emptyList<IngredientUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListIngredientViewHolder {
        val binding = IngredientDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListIngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListIngredientViewHolder, position: Int) = holder.bind(ingredients[position])

    override fun getItemCount(): Int = ingredients.size

    fun setIngredients(items: List<IngredientUI>) {
        ingredients = items
        notifyDataSetChanged()
    }
}

/** Qui è dove tocchiamo veramente l'xml della card, item view identifica la vera e propria view della card.
 *
 * */
class ListIngredientViewHolder(private val binding: IngredientDetailItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: IngredientUI) {
        binding.iconIngredients.load(item.ingredientImage)
        val text = item.ingredientName + ": " + item.ingredientQuantity
        binding.ingredientDetails.text = text
    }
}
