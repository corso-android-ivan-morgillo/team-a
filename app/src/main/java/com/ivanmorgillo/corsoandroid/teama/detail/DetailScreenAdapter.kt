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
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenViewHolder.MethodViewHolder
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenViewHolder.TitleViewHolder

data class IngredientUI(
    val ingredientName: String,
    val ingredientQuantity: String
)

// gli oggetti dentro questa sealed li stiamo aggiungendo a seconda dell'ordine della nostra schermata
// io seguo un pò anche il discorso di ivan perchè la nostra schermata è diversa
sealed class DetailScreenItems {

    data class Title(val title: String) : DetailScreenItems()
    data class Image(val image: String) : DetailScreenItems()
    data class IngredientList(val ingredient: List<IngredientUI>) : DetailScreenItems()
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
            is DetailScreenItems.IngredientList -> INGREDIENTLIST_VIEWTYPE
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
                val view = layoutInflater.inflate(R.layout.detail_screen_method, parent, false)
                MethodViewHolder(view)
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
            is IngredientListViewHolder -> holder.bind(items[position] as DetailScreenItems.IngredientList)
            is MethodViewHolder -> holder.bind(items[position] as DetailScreenItems.Instruction)
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
        fun bind(ingredient: DetailScreenItems.IngredientList) {
            // questa striscia contiene una recyclerview quindi a questa lista serve:
            // - un adapter e una lista di elem da passare all'adapter.
            val adapter = ListIngredientAdapter()
            ingredientDetail.adapter = adapter
            adapter.setIngredients(ingredient.ingredient)
        }
    }

    class MethodViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {

        private val methodDetail = itemView.findViewById<TextView>(R.id.detail_screen_method)
        fun bind(method: DetailScreenItems.Instruction) {
            methodDetail.text = method.instruction
        }
    }
}

class ListIngredientAdapter : RecyclerView.Adapter<ListIngredientViewHolder>() {

    private var ingredients = emptyList<IngredientUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListIngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_detail_item, parent, false)
        return ListIngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListIngredientViewHolder, position: Int) {
        holder.bind(ingredients[position])
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    fun setIngredients(items: List<IngredientUI>) {
        ingredients = items
        notifyDataSetChanged()
    }
}

/** Qui è dove tocchiamo veramente l'xml della card, item view identifica la vera e propria view della card.
 *
 *
 * */
class ListIngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ingredientDetails = itemView.findViewById<TextView>(R.id.ingredient_details)

    fun bind(item: IngredientUI) {
        ingredientDetails.text = item.ingredientName + "," + item.ingredientQuantity
    }
}
