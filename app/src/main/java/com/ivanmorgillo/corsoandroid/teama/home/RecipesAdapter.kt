package com.ivanmorgillo.corsoandroid.teama.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.card.MaterialCardView
import com.ivanmorgillo.corsoandroid.teama.R

class RecipesAdapter(private val onclick: (RecipeUI, View) -> Unit) : RecyclerView.Adapter<RecipeViewHolder>() {
    private var recipes = emptyList<RecipeUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position], onclick)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setRecipes(items: List<RecipeUI>) {
        recipes = items
        notifyDataSetChanged()
    }
}

/** Qui è dove tocchiamo veramente l'xml della card, item view identifica la vera e propria view della card.
 *
 *
 * */
class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title = itemView.findViewById<TextView>(R.id.recipe_title)
    private val image = itemView.findViewById<ImageView>(R.id.recipe_image)
    private val recipeCardView = itemView.findViewById<MaterialCardView>(R.id.recipe_root)

    /**@param onclick: è la funzione che riceverà in ingresso il parametro
     *  di tipo RecipeUi e ritornerà unit. Questo pezzo di funzionalità ci serve per
     * far funzionare il click.
     *
     * In Kotlin le funzioni possono essere delle variabili.
     * Possono essere messe dentro una variabile ed essere passate come parametro. In questo caso la
     * nostra funzione accetta come secondo parametro in
     * ingresso una funzione anonima (senza nome specifico/signature).*/
    fun bind(item: RecipeUI, onclick: (RecipeUI, View) -> Unit) {
        title.text = item.title
        image.load(item.image)
        image.contentDescription = item.title
        /** Il click deve essere gestito inviando indietro al viewModel
         * il click dell'utente e l'oggetto che è stato cliccato */
        recipeCardView.setOnClickListener {
            onclick(item, it)
        }
        recipeCardView.transitionName = "recipe_transition_item${item.id}"
    }
}
