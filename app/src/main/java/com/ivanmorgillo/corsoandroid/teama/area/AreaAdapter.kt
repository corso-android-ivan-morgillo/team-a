package com.ivanmorgillo.corsoandroid.teama.area

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ivanmorgillo.corsoandroid.teama.databinding.AreaItemBinding

class AreaAdapter (private val onclick: (AreaUI, View) -> Unit) : RecyclerView.Adapter<AreaViewHolder>() {
    private var areas = emptyList<AreaUI>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        val binding = AreaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AreaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {
        holder.bind(areas[position], onclick)
    }

    override fun getItemCount(): Int {
        return areas.size
    }

    fun setAreas(items: List<AreaUI>) {
        areas = items
        notifyDataSetChanged()
    }
}

class AreaViewHolder(private val binding: AreaItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind (item: AreaUI, onclick: (AreaUI, View) -> Unit) {
        binding.areaName.text = item.name
        binding.areaRoot.setOnClickListener {
            onclick(item, it)
        }
    }
}
