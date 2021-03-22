package com.ivanmorgillo.corsoandroid.teama

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ivanmorgillo.corsoandroid.teama.databinding.ActivityMainBinding
import com.ivanmorgillo.corsoandroid.teama.databinding.NavHeaderMainBinding
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import com.ivanmorgillo.corsoandroid.teama.extension.gone
import com.ivanmorgillo.corsoandroid.teama.extension.visible
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

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
                R.id.homeFragment, R.id.favouriteFragment,
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
                MainScreenAction.NavigateToHome -> navController.navigate(R.id.homeFragment)
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
                    onUserLoggedIn(action.user)
                }
            }.exhaustive
        })
        viewModel.send(MainScreenEvent.OnInitTheme)
        viewModel.send(MainScreenEvent.OnInitUser)

        //Google Ads Sdk Initializazion

        setupAds()
    }

    // Determine the screen width (less decorations) to use for the ad width.
    // If the ad hasn't been laid out, default to the full screen width.
    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = binding.appBarMain.contentMain.adViewContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    private fun setupAds() {
        MobileAds.initialize(this@MainActivity) {
        }

        val adView = AdView(this)
        binding.appBarMain.contentMain.adViewContainer.addView(adView)

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() = Unit

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Timber.e(Throwable("Cannot Load Ad: ${adError.code}"))
            }

            override fun onAdOpened() = Unit

            override fun onAdClicked() = Unit

            override fun onAdLeftApplication() = Unit

            override fun onAdClosed() = Unit
        }

        //adView.adUnitId = "ca-app-pub-6515754135994712/3247134737"
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"

        adView.adSize = adSize


        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        val requestConfig = RequestConfiguration.Builder().setTestDeviceIds(listOf("20D62A23A1F0F7AD88E710CB35E343FF")).build()
        MobileAds.setRequestConfiguration(requestConfig)
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)

    }

    private fun onItemSelected(itemId: Int) {
        when (itemId) {
            R.id.homeFragment -> viewModel.send(MainScreenEvent.OnHomeClick)
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
                    headerBinding.imageView.gone()
                    headerBinding.userTextView.gone()
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
            imageView.visible()
            userTextView.visible()
            imageView.load(user.photoUrl)
        } else {
            Timber.d("L'utente non era loggato e quindi non richiedo il login allo startup")
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
