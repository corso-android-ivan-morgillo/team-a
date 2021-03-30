package com.ivanmorgillo.corsoandroid.teama.shoppinglist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ivanmorgillo.corsoandroid.teama.R
import com.ivanmorgillo.corsoandroid.teama.databinding.FragmentShoppingListBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShoppingListFragment : Fragment(R.layout.fragment_shopping_list) {
    private val binding by viewBinding(FragmentShoppingListBinding::bind)
    private val adapter = ShoppingListAdapter()

    private val viewModel: ShoppingListViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ShoppingListStates.Content -> {
                    adapter.setShoppingList(state.shoppingList)
                    binding.shoppingList.adapter = adapter
                }
            }.exhaustive
        }
        viewModel.send(ShoppingListEvent.OnReady)
    }
}
