package com.ivanmorgillo.corsoandroid.teama.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teama.R

class CategoryFlagAdapter :
    RecyclerView.Adapter<CategoryFlagViewHolder>() {
    private var flagcategories = emptyList<FlagUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryFlagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_flag_country, parent, false)
        return CategoryFlagViewHolder(view)
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

/** Qui è dove tocchiamo veramente l'xml della card, item view identifica la vera e propria view della card.
 *
 *
 * */
class CategoryFlagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val image = itemView.findViewById<ImageView>(R.id.flag_country)
    private val flagUrl = ""
    fun bind(item: FlagUI) {
        image.load(item.flag)
        image.contentDescription = item.flag

        // categoryCardView.transitionName = "category_transition_item${item.id}"
    }
}
