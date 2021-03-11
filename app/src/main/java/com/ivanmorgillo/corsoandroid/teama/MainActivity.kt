package com.ivanmorgillo.corsoandroid.teama

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.ivanmorgillo.corsoandroid.teama.category.CategoryFragmentDirections
import com.ivanmorgillo.corsoandroid.teama.databinding.ActivityMainBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val toolbar: Toolbar = binding.appBarMain.toolbar
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.categoryFragment, R.id.favouriteFragment, R.id.settingsFragment, R.id.nav_feedback), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.categoryFragment -> viewModel.send(MainScreenEvent.OnCategoryClick)
                R.id.detailFragment -> viewModel.send(MainScreenEvent.OnRandomRecipeClick)
                R.id.favouriteFragment -> viewModel.send(MainScreenEvent.OnFavouritesClick)
                R.id.nav_feedback -> viewModel.send(MainScreenEvent.OnFeedbackClick)
                R.id.settingsFragment -> viewModel.send(MainScreenEvent.OnSettingsClick)
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        viewModel.actions.observe(this, { action ->
            when (action) {
                MainScreenAction.NavigateToCategory -> navController.navigate(R.id.categoryFragment)
                MainScreenAction.NavigateToFavourites -> navController.navigate(R.id.favouriteFragment)
                MainScreenAction.NavigateToFeedback -> openUrl(getString(R.string.feedback_url))
                MainScreenAction.NavigateToRandomRecipe -> {
                    Toast.makeText(this, getString(R.string.loading_random_recipe), Toast.LENGTH_SHORT).show()
                    val bundle = bundleOf("recipe_id" to -1L)
                    navController.navigate(R.id.detailFragment, bundle)
                }
                MainScreenAction.NavigateToSettings -> navController.navigate(R.id.settingsFragment)
            }.exhaustive
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun AppCompatActivity.openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}
