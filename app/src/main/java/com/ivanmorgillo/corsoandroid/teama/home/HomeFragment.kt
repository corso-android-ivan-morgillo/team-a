package com.ivanmorgillo.corsoandroid.teama.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ivanmorgillo.corsoandroid.teama.R

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryButton = view.findViewById<Button>(R.id.category_button)
        val areaButton = view.findViewById<Button>(R.id.area_button)
        val ingredientsButton = view.findViewById<Button>(R.id.ingredient_button)
        val randomButton = view.findViewById<Button>(R.id.random_button)
        categoryButton.setOnClickListener {

            val directions =
                HomeFragmentDirections.actionHomeFragmentToCategoryFragment()
            findNavController().navigate(directions)

        }
        areaButton.setOnClickListener {

            Toast.makeText(categoryButton.context, getString(R.string.work_in_progress), Toast.LENGTH_LONG).show()


        }
        ingredientsButton.setOnClickListener {

            Toast.makeText(categoryButton.context, getString(R.string.work_in_progress), Toast.LENGTH_LONG).show()

        }
        randomButton.setOnClickListener {

            Toast.makeText(categoryButton.context, getString(R.string.work_in_progress), Toast.LENGTH_LONG).show()

        }


    }

}
