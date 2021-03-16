package com.ivanmorgillo.corsoandroid.teama

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ivanmorgillo.corsoandroid.teama.databinding.ActivityMainBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

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
                R.id.categoryFragment, R.id.favouriteFragment,
                R.id.settingsFragment, R.id.nav_feedback,
                R.id.login, R.id.logout
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
                R.id.settingsFragment -> viewModel.send(MainScreenEvent.OnSettingsClick)
                R.id.login -> firebaseLogin()
                R.id.logout -> firebaseLogout()
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


    private fun firebaseLogout() {
        //Sign out da firebase google.
        /**Per questo va  aggiunto il pulsante apposito logout*/
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                Toast.makeText(this, "Logout effettuato!", Toast.LENGTH_LONG).show()
            }
    }

    private fun firebaseLogin() {
        //activity firebaase LOGIN
        /**Per questo va  aggiunto il pulsante apposito login*/
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .enableAnonymousUsersAutoUpgrade()
            .build()
        firebaseAuthenticationResultLauncher.launch(intent)
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private var firebaseAuthenticationResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            val response = IdpResponse.fromResultIntent(result.data)
            if (result.resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser

                Timber.e("Anon Uid google:${user.uid}")
                Toast.makeText(this, "Welcome, ${user?.displayName}", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(this, "ERROR LOGIN", Toast.LENGTH_LONG).show()
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                // Timber.e("User:", "${result.response?.error?.errorCode}")

                // Sign in failed
                if (response?.error?.errorCode == ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT) {
                    // Store relevant anonymous user data
                    // Get the non-anoymous credential from the response
                    val nonAnonymousCredential = response.credentialForLinking;
                    // Sign in with credential
                    FirebaseAuth.getInstance().signInWithCredential(nonAnonymousCredential)
                        .addOnSuccessListener {
                            Timber.d("merge user ${it.user.uid}")
                        }


                }
            }
        }

    /**In questa funzione andremo a capire se esiste un user/vediamo l'anonym-user ?? */
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = Firebase.auth.currentUser

        if (currentUser == null) {
            //Sign in anonymously ?
            signInAnonymously()
        } else {
            Timber.d("User is logged, welcome back!")
        }

    }

    private fun signInAnonymously() {
        lifecycleScope.launch {
            val x = Firebase.auth.signInAnonymously().await()
            Timber.d("User anon is: ${x.user}")
            Timber.d("User Anon UID is: ${x.user.uid}")

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
