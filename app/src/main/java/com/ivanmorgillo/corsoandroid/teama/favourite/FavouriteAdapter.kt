package com.ivanmorgillo.corsoandroid.teama.favourite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teama.databinding.FavouriteItemBinding
import java.util.*
import kotlin.collections.ArrayList

class FavouriteAdapter(
    private val onclick: (FavouriteUI, View) -> Unit,
) : RecyclerView.Adapter<RecipeViewHolder>() {
    private var favourites = mutableListOf<FavouriteUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = FavouriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(favourites[position], onclick)
    }

    override fun getItemCount(): Int {
        return favourites.size
    }

    fun setFavourites(items: List<FavouriteUI>) {
        val diffCallback = FavouritesDiffUtils(favourites, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        favourites = items.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    // per la ricerca
    fun filter(mList: List<FavouriteUI>, query: String): List<FavouriteUI> {
        val filteredList: MutableList<FavouriteUI> = ArrayList<FavouriteUI>()
        for (item in mList) {
            // condizione = titolo della ricetta
            if (item.title.toLowerCase(Locale.getDefault())
                    .contains(query.toLowerCase(Locale.getDefault()).trim()) || query.isBlank()
            ) {
                filteredList.add(item)
            }
        }
        return filteredList
    }
}

/** Qui è dove tocchiamo veramente l'xml della card, item view identifica la vera e propria view della card.
 *
 *
 * */
class RecipeViewHolder(private val binding: FavouriteItemBinding) : RecyclerView.ViewHolder(binding.root) {
    /**@param onclick: è la funzione che riceverà in ingresso il parametro
     *  di tipo FavouriteUI e ritornerà unit. Questo pezzo di funzionalità ci serve per
     * far funzionare il click.
     *
     * In Kotlin le funzioni possono essere delle variabili.
     * Possono essere messe dentro una variabile ed essere passate come parametro. In questo caso la
     * nostra funzione accetta come secondo parametro in
     * ingresso una funzione anonima (senza nome specifico/signature).*/
    fun bind(item: FavouriteUI, onclick: (FavouriteUI, View) -> Unit) {
        binding.favouriteTitle.text = item.title
        binding.favouriteImage.load(item.image)
        binding.favouriteImage.contentDescription = item.title
        /** Il click deve essere gestito inviando indietro al viewModel
         * il click dell'utente e l'oggetto che è stato cliccato */
        binding.favouriteRoot.setOnClickListener {
            onclick(item, it)
        }
    }
}

class FavouritesDiffUtils(private val oldList: List<FavouriteUI>, private val newList: List<FavouriteUI>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.id == newItem.id && oldItem.title == newItem.title
    }
}
