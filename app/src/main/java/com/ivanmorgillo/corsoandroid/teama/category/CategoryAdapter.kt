package com.ivanmorgillo.corsoandroid.teama.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import coil.load
import com.google.android.material.card.MaterialCardView
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.gone
import com.ivanmorgillo.corsoandroid.teama.visible

class CategoryAdapter(
    private val onclick: (CategoryUI, View) -> Unit
) : RecyclerView.Adapter<CategoryViewHolder>() {
    private var categories = emptyList<CategoryUI>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], onclick)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun setCategories(items: List<CategoryUI>) {
        categories = items
        notifyDataSetChanged()
    }
}

/** Qui è dove tocchiamo veramente l'xml della card, item view identifica la vera e propria view della card.
 *
 *
 * */
class CategoryViewHolder(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {
    private val title = itemView.findViewById<TextView>(R.id.category_title)
    private val image = itemView.findViewById<ImageView>(R.id.category_image)
    private val categoryCardView = itemView.findViewById<MaterialCardView>(R.id.category_root)
    private val flagList = itemView.findViewById<RecyclerView>(R.id.flag_list)
    private val flagCounter = itemView.findViewById<TextView>(R.id.recipe_counter)
    private val hiddenConstraintLayout = itemView.findViewById<ConstraintLayout>(R.id.category_item_expanded)
    private val goToRecipes = itemView.findViewById<Button>(R.id.category_to_recipes)
    private val arrowForExpand = itemView.findViewById<ImageButton>(R.id.arrow_button)

    fun bind(item: CategoryUI, onclick: (CategoryUI, View) -> Unit) {
        val categoryFlagAdapter = CategoryFlagAdapter()

        flagList.adapter = categoryFlagAdapter
        categoryFlagAdapter.setFlagCategories(item.flags)
        flagCounter.text = "${item.recipesCount} recipes"
        title.text = item.title
        image.load(item.image)
        image.contentDescription = item.title
        goToRecipes.setOnClickListener {
            onclick(item, it)
        }

        goToRecipes.setOnClickListener {
            onclick(item, it)
        }

        arrowForExpand.setOnClickListener {
            if (hiddenConstraintLayout.isVisible) {
                TransitionManager.beginDelayedTransition(
                    categoryCardView,
                    AutoTransition()
                )
                arrowForExpand.setImageResource(R.drawable.arrow_down)
                hiddenConstraintLayout.gone()
            } else {
                TransitionManager.beginDelayedTransition(
                    categoryCardView,
                    AutoTransition()
                )
                arrowForExpand.setImageResource(R.drawable.arrow_up)
                hiddenConstraintLayout.visible()
            }
        }
    }

    // categoryCardView.transitionName = "category_transition_item${item.id}"
}
