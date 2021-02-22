package com.ivanmorgillo.corsoandroid.teama.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.card.MaterialCardView
import com.ivanmorgillo.corsoandroid.teama.R

// private val onclick: (CategoryUI, View) -> Unit
class CategoryAdapter : RecyclerView.Adapter<CategoryViewHolder>() {
    private var categories = emptyList<CategoryUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
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
class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title = itemView.findViewById<TextView>(R.id.category_title)
    private val image = itemView.findViewById<ImageView>(R.id.category_image)
    private val categoryCardView = itemView.findViewById<MaterialCardView>(R.id.category_root)

    //fun bind(item: CategoryUI, onclick: (CategoryUI, View) -> Unit)

    fun bind(item: CategoryUI) {
        title.text = item.title
        image.load(item.image)
        image.contentDescription = item.title

        categoryCardView.transitionName = "category_transition_item${item.id}"
    }
}
