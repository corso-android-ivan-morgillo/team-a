package com.ivanmorgillo.corsoandroid.teama.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ivanmorgillo.corsoandroid.teama.databinding.RecipeItemBinding
import java.util.*
import kotlin.collections.ArrayList

class RecipesAdapter(private val onclick: (RecipeUI, View) -> Unit) : RecyclerView.Adapter<RecipeViewHolder>() {
    private var recipes = emptyList<RecipeUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
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
class RecipeViewHolder(private val binding: RecipeItemBinding) : RecyclerView.ViewHolder(binding.root) {
    /**@param onclick: è la funzione che riceverà in ingresso il parametro
     *  di tipo RecipeUi e ritornerà unit. Questo pezzo di funzionalità ci serve per
     * far funzionare il click.
     *
     * In Kotlin le funzioni possono essere delle variabili.
     * Possono essere messe dentro una variabile ed essere passate come parametro. In questo caso la
     * nostra funzione accetta come secondo parametro in
     * ingresso una funzione anonima (senza nome specifico/signature).*/
    fun bind(item: RecipeUI, onclick: (RecipeUI, View) -> Unit) {
        binding.recipeTitle.text = item.title
        binding.recipeImage.load(item.image)
        binding.recipeImage.contentDescription = item.title
        /** Il click deve essere gestito inviando indietro al viewModel
         * il click dell'utente e l'oggetto che è stato cliccato */
        binding.recipeRoot.setOnClickListener { onclick(item, it) }
        binding.recipeRoot.transitionName = "recipe_transition_item${item.id}"
    }
}
