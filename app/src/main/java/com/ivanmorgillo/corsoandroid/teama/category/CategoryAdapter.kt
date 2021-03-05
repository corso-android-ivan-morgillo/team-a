package com.ivanmorgillo.corsoandroid.teama.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import coil.load
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.databinding.CategoryItemBinding
import com.ivanmorgillo.corsoandroid.teama.extension.gone
import com.ivanmorgillo.corsoandroid.teama.extension.visible

class CategoryAdapter(private val onclick: (CategoryUI, View) -> Unit) : RecyclerView.Adapter<CategoryViewHolder>() {
    private var categories = emptyList<CategoryUI>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
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
class CategoryViewHolder(private val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: CategoryUI, onclick: (CategoryUI, View) -> Unit) {
        val categoryFlagAdapter = CategoryFlagAdapter()
        binding.flagList.adapter = categoryFlagAdapter
        categoryFlagAdapter.setFlagCategories(item.flags)
        val recipesCounterText = item.recipesCount + " " + binding.root.resources.getString(R.string.recipes)
        binding.recipeCounter.text = recipesCounterText
        binding.categoryTitle.text = item.title
        binding.categoryDescription.text = item.description
        // binding.categoryImage.load(item.image)
        binding.categoryImageCollapsed.load(item.image)
        //  binding.categoryImage.contentDescription = item.title
        binding.categoryRoot.setOnClickListener { // per aprire il dettaglio della ricetta
            onclick(item, it)
        }
        binding.categoryInfo.setOnClickListener { // per espandere o collassare la card
            expandOrCollapse()
        }
        /*     binding.arrowButton.setOnClickListener { // per espandere o collassare la card
             expandOrCollapse()
         } */
        // categoryCardView.transitionName = "category_transition_item${item.id}"
    }

    private fun expandOrCollapse() {
        if (binding.actionsCategoryExpanded.isVisible) {
            binding.actionsCategoryExpanded.gone()
            // binding.actionsCategoryCollapsed.visible()
            TransitionManager.beginDelayedTransition(
                binding.categoryRoot,
                AutoTransition()
            )
        } else {
            binding.actionsCategoryExpanded.visible()
            TransitionManager.beginDelayedTransition(
                binding.categoryRoot,
                AutoTransition()
            )
        }
    }
}
