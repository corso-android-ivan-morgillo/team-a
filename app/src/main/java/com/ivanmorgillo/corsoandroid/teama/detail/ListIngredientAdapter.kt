package com.ivanmorgillo.corsoandroid.teama.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.ivanmorgillo.corsoandroid.teama.R

data class IngredientUI(
    val ingredientName: String,
    val ingredientQuantity: String
)

class ListIngredientAdapter : RecyclerView.Adapter<ListIngredientViewHolder>() {

    private var ingredients = emptyList<IngredientUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListIngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_detail_item, parent, false)
        return ListIngredientViewHolder(view)
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
class ListIngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ingredientDetails = itemView.findViewById<CheckBox>(R.id.ingredient_details)

    fun bind(item: IngredientUI) {
        ingredientDetails.text = item.ingredientName + "," + item.ingredientQuantity
    }
}
