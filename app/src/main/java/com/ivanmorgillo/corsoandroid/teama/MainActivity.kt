package com.ivanmorgillo.corsoandroid.teama

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ivanmorgillo.corsoandroid.teama.databinding.ActivityMainBinding
import com.ivanmorgillo.corsoandroid.teama.databinding.NavHeaderMainBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity(), GoogleLoginRequest {

    private val viewModel: MainViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding
    private lateinit var headerBinding: NavHeaderMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val headerView = binding.navView.getHeaderView(0)
        headerBinding = NavHeaderMainBinding.bind(headerView)
        val view = binding.root
        setContentView(view)
        val toolbar: Toolbar = binding.appBarMain.toolbar
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.categoryFragment, R.id.favouriteFragment,
                R.id.settingsFragment, R.id.nav_feedback,
                R.id.login, R.id.logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
            onItemSelected(it.itemId)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        observeStates()
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
                is MainScreenAction.ChangeTheme -> initTheme(action.darkEnabled)
                MainScreenAction.ShowLoginDialog -> firebaseLogin()
                MainScreenAction.ShowLogout -> firebaseLogout()
                is MainScreenAction.UserLogin -> {
                    if (action.userLogged) {
                        viewModel.send(MainScreenEvent.OnLogin)
                    } else {
                        Timber.d("L'utente non era loggato e quindi non richiedo il login allo startup")
                    }
                }
            }.exhaustive
        })
        viewModel.send(MainScreenEvent.OnInitTheme)
        viewModel.send(MainScreenEvent.OnInitUser)
    }

    private fun onItemSelected(itemId: Int) {
        when (itemId) {
            R.id.categoryFragment -> viewModel.send(MainScreenEvent.OnCategoryClick)
            R.id.detailFragment -> viewModel.send(MainScreenEvent.OnRandomRecipeClick)
            R.id.favouriteFragment -> viewModel.send(MainScreenEvent.OnFavouritesClick)
            R.id.nav_feedback -> viewModel.send(MainScreenEvent.OnFeedbackClick)
            R.id.settingsFragment -> viewModel.send(MainScreenEvent.OnSettingsClick)
            R.id.login -> viewModel.send(MainScreenEvent.OnLogin)
            R.id.logout -> viewModel.send(MainScreenEvent.OnLogout)
        }
    }

    private fun initTheme(darkEnabled: Boolean) {
        if (darkEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun observeStates() {
        viewModel.states.observe(this, { state ->
            when (state) {
                is MainScreenStates.LoggedIn -> {
                    val user = state.user
                    onUserLoggedIn(user)
                }
                MainScreenStates.LoginFailure -> {
                    onLoginFailure()
                }
                MainScreenStates.LoggedOut -> {
                    Toast.makeText(this, "Logout effettuato!", Toast.LENGTH_LONG).show()
                }
                MainScreenStates.LogoutFailure -> {
                    Toast.makeText(this, "Logout fallito!", Toast.LENGTH_LONG).show()
                }
            }.exhaustive
        })
    }

    private fun onLoginFailure() {
        val message = Toast.makeText(
            this,
            getString(R.string.failed_login),
            Toast.LENGTH_SHORT
        )
        message.setGravity(Gravity.CENTER, 0, 0)
        message.show()
    }

    private fun onUserLoggedIn(user: FirebaseUser?) {
        if (user != null) {
            val email = user.email ?: ""
            val message = Toast.makeText(
                this,
                getString(R.string.welcome) + email,
                Toast.LENGTH_SHORT
            )
            message.setGravity(Gravity.CENTER, 0, 0)
            message.show()
            val imageView = headerBinding.imageView
            val userTextView = headerBinding.userTextView
            userTextView.text = user.email
            imageView.load(user.photoUrl)
        } else {
            Timber.d("User logged in but user is null")
        }
    }

    override fun onGoogleLogin() {
        viewModel.send(MainScreenEvent.OnLogin)
    }

    private fun firebaseLogout() {
        //Sign out da firebase google.
        /**Per questo va  aggiunto il pulsante apposito logout*/
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                viewModel.send(MainScreenEvent.OnLogoutSuccessful)
            }.addOnFailureListener {
                viewModel.send(MainScreenEvent.OnLogoutFailed)
            }
    }

    private fun firebaseLogin() {
        //activity firebase LOGIN
        /**Per questo va  aggiunto il pulsante apposito login*/
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        firebaseAuthenticationResultLauncher.launch(intent)
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private val firebaseAuthenticationResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Timber.d("Login successful")
                viewModel.send(MainScreenEvent.OnLoginSuccessful(Firebase.auth.currentUser))
            } else {
                Timber.e("User login failed")
                viewModel.send(MainScreenEvent.OnLoginFailed)
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
