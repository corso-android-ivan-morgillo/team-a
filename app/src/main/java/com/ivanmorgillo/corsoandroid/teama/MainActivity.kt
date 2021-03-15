package com.ivanmorgillo.corsoandroid.teama

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import com.google.android.material.internal.ContextUtils
import java.util.*
import android.os.Build

import android.util.DisplayMetrics
import androidx.appcompat.view.ContextThemeWrapper
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.datatransport.runtime.logging.Logging.e
import com.google.firebase.auth.FirebaseAuth
private const val RC_SIGN_IN: Int = 1234
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
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.categoryFragment, R.id.favouriteFragment, R.id.settingsFragment, R.id.nav_feedback
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.categoryFragment -> viewModel.send(MainScreenEvent.OnCategoryClick)
                R.id.detailFragment -> viewModel.send(MainScreenEvent.OnRandomRecipeClick)
                R.id.favouriteFragment -> viewModel.send(MainScreenEvent.OnFavouritesClick)
                R.id.nav_feedback -> viewModel.send(MainScreenEvent.OnFeedbackClick)
                R.id.settingsFragment ->{
                    //activity firebaase LOGIN

                    val providers = arrayListOf(
                        AuthUI.IdpConfig.GoogleBuilder().build() )
                    startActivityForResult(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                        RC_SIGN_IN)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    viewModel.send(MainScreenEvent.OnSettingsClick)

                }

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
                is MainScreenAction.ChangeTheme -> {
                    val darkEnabled = action.darkEnabled
                    if (darkEnabled) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }.exhaustive
        })
        viewModel.send(MainScreenEvent.OnInitTheme)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Timber.e("User:" , "$user")
                Toast.makeText(this,"Welcome, ${user.displayName}",Toast.LENGTH_LONG).show()
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Timber.e("User:", "${response?.error?.errorCode}")
            }
        }
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
