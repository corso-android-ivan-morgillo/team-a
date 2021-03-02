package com.ivanmorgillo.corsoandroid.teama.favourite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.ivanmorgillo.corsoandroid.teama.R

class FavouriteAdapter(
    private val onclick: (FavouriteUI, View) -> Unit,
    private val recyclerView: RecyclerView,
) : RecyclerView.Adapter<RecipeViewHolder>() {
    private var favourites = mutableListOf<FavouriteUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favourite_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(favourites[position], onclick)
    }

    override fun getItemCount(): Int {
        return favourites.size
    }

    fun setFavourites(items: List<FavouriteUI>) {
        favourites = items.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(mList: List<FavouriteUI>, query: String): List<FavouriteUI> {
        val filteredList: MutableList<FavouriteUI> = ArrayList<FavouriteUI>()
        for (item in mList) {
            // condizione = titolo della ricetta
            if (item.title.toLowerCase().contains(query.toLowerCase().trim()) || query.isBlank()) {
                filteredList.add(item)
            }
        }
        return filteredList
    }

    fun deleteItem(position: Int, view: View) {
        val deletedItem: FavouriteUI = favourites[position]
        favourites.removeAt(position)
        notifyItemRemoved(position)
        val snackbar: Snackbar =
            Snackbar.make(view,
                recyclerView.context.resources.getString(R.string.favourite_deleted),
                Snackbar.LENGTH_LONG)
        snackbar.setAction(recyclerView.context.resources.getString(R.string.undo)) { v ->
            favourites.add(position, deletedItem)
            notifyItemInserted(position)
            recyclerView.scrollToPosition(position)
        }
        snackbar.show()
    }
}

/** Qui è dove tocchiamo veramente l'xml della card, item view identifica la vera e propria view della card.
 *
 *
 * */
class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title = itemView.findViewById<TextView>(R.id.favourite_title)
    private val image = itemView.findViewById<ImageView>(R.id.favourite_image)
    private val favouriteCardView = itemView.findViewById<MaterialCardView>(R.id.favourite_root)

    /**@param onclick: è la funzione che riceverà in ingresso il parametro
     *  di tipo FavouriteUI e ritornerà unit. Questo pezzo di funzionalità ci serve per
     * far funzionare il click.
     *
     * In Kotlin le funzioni possono essere delle variabili.
     * Possono essere messe dentro una variabile ed essere passate come parametro. In questo caso la
     * nostra funzione accetta come secondo parametro in
     * ingresso una funzione anonima (senza nome specifico/signature).*/
    fun bind(item: FavouriteUI, onclick: (FavouriteUI, View) -> Unit) {
        title.text = item.title
        image.load(item.image)
        image.contentDescription = item.title
        /** Il click deve essere gestito inviando indietro al viewModel
         * il click dell'utente e l'oggetto che è stato cliccato */
        favouriteCardView.setOnClickListener {
            onclick(item, it)
        }
    }
}
