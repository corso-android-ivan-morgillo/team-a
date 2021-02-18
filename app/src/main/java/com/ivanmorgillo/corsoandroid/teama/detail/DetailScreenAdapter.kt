package com.ivanmorgillo.corsoandroid.teama.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.tabs.TabLayout
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenViewHolder.ImageViewHolder
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenViewHolder.IngredientInstructionListViewHolder
import com.ivanmorgillo.corsoandroid.teama.detail.DetailScreenViewHolder.TitleViewHolder
import com.ivanmorgillo.corsoandroid.teama.exhaustive
import timber.log.Timber

// gli oggetti dentro questa sealed li stiamo aggiungendo a seconda dell'ordine della nostra schermata
// io seguo un pò anche il discorso di ivan perchè la nostra schermata è diversa
sealed class DetailScreenItems {

    data class Title(val title: String) : DetailScreenItems()
    data class Image(val image: String) : DetailScreenItems()
    data class IngredientsInstructionsList(val ingredients: List<IngredientUI>, val instruction: String) :
        DetailScreenItems()

    object TabLayout : DetailScreenItems()
}

private const val IMAGEVAL_VIEWTYPE = 1
private const val IGNREDIENTSINSTRUCTIONS_VIEWTYPE = 2
private const val TITLE_VIEWTYPE = 3
private const val TABLAYOUT_VIEWTYPE = 4

class DetailScreenAdapter(
    private val onIngredientsClick: () -> Unit,
    private val onInstructionsClick: () -> Unit
) : RecyclerView.Adapter<DetailScreenViewHolder>() {
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
            is DetailScreenItems.IngredientsInstructionsList -> IGNREDIENTSINSTRUCTIONS_VIEWTYPE
            is DetailScreenItems.Title -> TITLE_VIEWTYPE
            is DetailScreenItems.TabLayout -> TABLAYOUT_VIEWTYPE
        }.exhaustive
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailScreenViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            IMAGEVAL_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_screen_image, parent, false)
                ImageViewHolder(view)
            }
            IGNREDIENTSINSTRUCTIONS_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_ingredient_instruction, parent, false)
                IngredientInstructionListViewHolder(view)
            }
            TITLE_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.detail_screen_title, parent, false)
                TitleViewHolder(view)
            }
            TABLAYOUT_VIEWTYPE -> {
                val view = layoutInflater.inflate(R.layout.tab_button_details, parent, false)
                DetailScreenViewHolder.TabLayoutViewHolder(view)
            }
            else -> error("ViewTypeNotValid!")
        }
    }

    override fun onBindViewHolder(holder: DetailScreenViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> holder.bind(items[position] as DetailScreenItems.Image)
            is IngredientInstructionListViewHolder -> holder.bind(
                items[position] as DetailScreenItems.IngredientsInstructionsList

            )
            is TitleViewHolder -> holder.bind(items[position] as DetailScreenItems.Title)
            is DetailScreenViewHolder.TabLayoutViewHolder -> holder.bind(onIngredientsClick, onInstructionsClick)
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

    class IngredientInstructionListViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {

        private val ingredientDetail = itemView.findViewById<RecyclerView>(R.id.detail_screen_ingredient_list)
        private val instructionDetail = itemView.findViewById<TextView>(R.id.detail_screen_instruction)
        fun bind(
            ingredientInstructions: DetailScreenItems.IngredientsInstructionsList
        ) {
            // questa striscia contiene una recyclerview quindi a questa lista serve:
            // - un adapter e una lista di elem da passare all'adapter.
            val adapter = ListIngredientAdapter()
            ingredientDetail.adapter = adapter
            adapter.setIngredients(ingredientInstructions.ingredients)

            instructionDetail.text = ingredientInstructions.instruction
        }
    }

    class TabLayoutViewHolder(itemView: View) : DetailScreenViewHolder(itemView) {

        private val tabLayout = itemView.findViewById<TabLayout>(R.id.tab_layout_detail)

        fun bind(onIngredientsClick: () -> Unit, onInstructionsClick: () -> Unit) {
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    Timber.d("OnTabSelected: ${tab.position}")

                    if (tab.position == 0) {
                        onIngredientsClick()
                    } else {
                        onInstructionsClick()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

                override fun onTabReselected(tab: TabLayout.Tab?) = Unit
            })
        }
    }
}
