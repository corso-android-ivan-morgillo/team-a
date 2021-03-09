package com.ivanmorgillo.corsoandroid.teama.utils

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import com.ivanmorgillo.corsoandroid.teama.R

class Util {
    fun createSearchManager(
        activity: Activity?,
        searchMenuItem: MenuItem,
        onQueryTextListener: SearchView.OnQueryTextListener
    ) {
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView.queryHint = activity.resources.getString(R.string.search_hint)
        // Se il testo cercato è vuoto e non c'è focus nella casella di testo, chiudi la barra di ricerca
        searchView.setOnQueryTextFocusChangeListener { _ , hasFocus ->
            if (!hasFocus && searchView.query.toString().trim().isEmpty()) {
                searchView.isIconified = true
            }
        }
        searchView.setOnQueryTextListener(onQueryTextListener)
    }
}
