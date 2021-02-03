package com.ivanmorgillo.corsoandroid.teama

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // dobbiamo creare un binding alla UI
        val adapter : RecipesAdapter = RecipesAdapter()
        recipe_list.adapter = adapter
        adapter.setRecipes(recipes)

    }
}

class RecipesAdapter : RecyclerView.Adapter<RecipeViewHolder>() {
    private var recipes: List<RecipeUI> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
    
    fun setRecipes(items : List<RecipeUI>){
        recipes = items
        notifyDataSetChanged()

    }
}

class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title = itemView.findViewById<TextView>(R.id.recipe_title)
    val image = itemView.findViewById<ImageView>(R.id.recipe_image)

    fun bind(item: RecipeUI) {
        title.text = item.title
        image.load(item.image)
    }
}

data class RecipeUI(
    val title: String,
    val image: String
)

val recipes = listOf<RecipeUI>(
    RecipeUI(title = "Spaghetti1", image = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(title = "Spaghetti2", image = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(title = "Spaghetti3", image = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(title = "Spaghetti4", image = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(title = "Spaghetti5", image = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(title = "Spaghetti6", image = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(title = "Spaghetti7", image = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(title = "Spaghetti8", image = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(title = "Spaghetti9", image = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(title = "Spaghetti10", image = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(title = "Spaghetti11", image = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
    RecipeUI(title = "Spaghetti12", image = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg"),
)
