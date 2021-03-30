package com.ivanmorgillo.corsoandroid.teama.shoppinglist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ivanmorgillo.corsoandroid.teama.databinding.ShoppingListItemBinding

class ShoppingListAdapter : RecyclerView.Adapter<ShoppingListViewHolder>() {
    private var shoppingList = emptyList<ShoppingListUI>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val binding = ShoppingListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        holder.bind(shoppingList[position])
    }

    override fun getItemCount(): Int {
        return shoppingList.size
    }

    fun setShoppingList(items: List<ShoppingListUI>) {
        shoppingList = items
        notifyDataSetChanged()
    }
}

class ShoppingListViewHolder(private val binding: ShoppingListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ShoppingListUI) {
        binding.ingredientName.text = item.ingredientName
        binding.ingredientQuantity.text = item.ingredientQuantity
        binding.ingredientName.isChecked = item.isChecked
    }
}
