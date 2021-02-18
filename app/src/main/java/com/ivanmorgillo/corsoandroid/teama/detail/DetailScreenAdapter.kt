package com.ivanmorgillo.corsoandroid.teama.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenViewHolder.ImageViewHolder
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenViewHolder.IngredientListViewHolder
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenViewHolder.InstructionViewHolder
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenViewHolder.TitleViewHolder

// gli oggetti dentro questa sealed li stiamo aggiungendo a seconda dell'ordine della nostra schermata
// io seguo un pò anche il discorso di ivan perchè la nostra schermata è diversa
sealed class DetailScreenItems {

    data class Title(val title: String) : DetailScreenItems()
    data class Image(val image: String) : DetailScreenItems()
    data class IngredientsList(val ingredients: List<IngredientUI>) : DetailScreenItems()
    data class Instruction(val instruction: String) : DetailScreenItems()
}

private const val IMAGEVAL_VIEWTYPE = 1
private const val INGREDIENTLIST_VIEWTYPE = 2
private const val INSTRUCTION_VIEWTYPE = 3
private const val TITLE_VIEWTYPE = 4

class DetailScreenAdapter : RecyclerView.Adapter<DetailScreenViewHolder>() {
    var items: List<DetailScreenItems> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**In base all'elemento che stiamo utilizzando ci ritorna un intero
     * che rappresenta il viewType.
     * */
    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item) {
            is DetailScreenItems.Image -> IMAGEVAL_VIEWTYPE
            is DetailScreenItems.IngredientsList -> INGREDIENTLIST_VIEWTYPE
            is DetailScreenItems.Instruction -> INSTRUCTION_VIEWTYPE
            is DetailScreenItems.Title -> TITLE_VIEWTYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailScreenViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            IMAGEVAL_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_screen_image, parent, false)
                ImageViewHolder(view)
            }
            INGREDIENTLIST_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_screen_ingredient, parent, false)
                IngredientListViewHolder(view)
            }
            INSTRUCTION_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_screen_instruction, parent, false)
                InstructionViewHolder(view)
            }
            TITLE_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_screen_title, parent, false)
                TitleViewHolder(view)
            }
            else -> error("ViewTypeNotValid!")
        }
    }

    override fun onBindViewHolder(holder: DetailScreenViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> holder.bind(items[position] as DetailScreenItems.Image)
            is IngredientListViewHolder -> holder.bind(items[position] as DetailScreenItems.IngredientsList)
            is InstructionViewHolder -> holder.bind(items[position] as DetailScreenItems.Instruction)
            is TitleViewHolder -> holder.bind(items[position] as DetailScreenItems.Title)
        }
    }

    override fun getItemCount(): Int = items.size
}

sealed class DetailScreenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // view holder per il titolo
    class TitleViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {
        private val titleDetail = itemView.findViewById<TextView>(R.id.detail_screen_title)
        fun bind(title: DetailScreenItems.Title) {

            titleDetail.text = title.title
        }
    }

    class ImageViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {
        private val imageDetail = itemView.findViewById<ImageView>(R.id.detail_screen_image)
        fun bind(image: DetailScreenItems.Image) {

            imageDetail.load(image.image)
        }
    }

    class IngredientListViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {

        private val ingredientDetail = itemView.findViewById<RecyclerView>(R.id.detail_screen_ingredient_list)
        fun bind(ingredient: DetailScreenItems.IngredientsList) {
            // questa striscia contiene una recyclerview quindi a questa lista serve:
            // - un adapter e una lista di elem da passare all'adapter.
            val adapter = ListIngredientAdapter()
            ingredientDetail.adapter = adapter
            adapter.setIngredients(ingredient.ingredients)
        }
    }

    class InstructionViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {

        private val instructionDetail = itemView.findViewById<TextView>(R.id.detail_screen_instruction)
        fun bind(method: DetailScreenItems.Instruction) {
            instructionDetail.text = method.instruction
        }
    }
}
