package com.ivanmorgillo.corsoandroid.teama.category

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import coil.bitmap.BitmapPool
import coil.load
import coil.size.Size
import coil.transform.Transformation
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.databinding.CategoryItemBinding

class CategoryAdapter(private val onclick: (CategoryUI, View) -> Unit) : RecyclerView.Adapter<CategoryViewHolder>() {
    private var categories = emptyList<CategoryUI>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], onclick)
        //holder.setIsRecyclable(false)
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

        val recipesCounterText = item.recipesCount + " " + binding.root.resources.getString(R.string.recipes)
        binding.recipeCounter.text = recipesCounterText
        binding.categoryTitle.text = item.title
        binding.categoryImageCollapsed.load(item.image) {
            transformations(object : Transformation {
                override fun key() = "paletteTransformer"
                override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap {
                    val p = Palette.from(input).generate()
                    val color = p.getLightVibrantColor(R.color.colorPrimary)
                    binding.imageViewConstraint.background.setTint(color)
                    return input
                }
            })
        }

        binding.categoryRoot.setOnClickListener { // per aprire il dettaglio della ricetta
            onclick(item, it)
        }
    }
}
