/*
package com.ivanmorgillo.corsoandroid.teama.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teama.databinding.CategoryFlagCountryBinding

class CategoryFlagAdapter : RecyclerView.Adapter<CategoryFlagViewHolder>() {
    private var flagcategories = emptyList<FlagUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryFlagViewHolder {
        val binding = CategoryFlagCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryFlagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryFlagViewHolder, position: Int) {
        holder.bind(flagcategories[position])
    }

    override fun getItemCount(): Int {
        return flagcategories.size
    }

    fun setFlagCategories(items: List<FlagUI>) {
        flagcategories = items
        notifyDataSetChanged()
    }
}

class CategoryFlagViewHolder(private val binding: CategoryFlagCountryBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: FlagUI) {
        binding.flagCountry.load(item.flag)
        binding.flagCountry.contentDescription = item.flag
        // categoryCardView.transitionName = "category_transition_item${item.id}"
    }
}
*/
