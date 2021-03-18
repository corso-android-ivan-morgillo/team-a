package com.ateam.delicious.domain.repository

import android.content.Context
import android.content.res.Configuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface SettingsRepository {
    suspend fun setDarkTheme(darkEnable: Boolean): Boolean
    suspend fun isDarkThemeEnabled(): Boolean
    suspend fun setFavouriteMessageShown(shown: Boolean): Boolean
    suspend fun isFavouriteMessageShown(): Boolean
    suspend fun setUserLogged(logged: Boolean): Boolean
    suspend fun isUserLogged(): Boolean
}

class SettingsRepositoryImpl(val context: Context) : SettingsRepository {

    private val storage by lazy { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    override suspend fun setDarkTheme(darkEnable: Boolean): Boolean = withContext(Dispatchers.IO) {
        storage.edit().putBoolean("dark_theme", darkEnable).commit()
    }

    override suspend fun isDarkThemeEnabled(): Boolean = withContext(Dispatchers.IO) {
        storage.getBoolean("dark_theme", isNightModeEnabled())
    }

    override suspend fun setFavouriteMessageShown(shown: Boolean): Boolean = withContext(Dispatchers.IO) {
        storage.edit().putBoolean("favourite_message_shown", shown).commit()
    }

    override suspend fun isFavouriteMessageShown(): Boolean = withContext(Dispatchers.IO) {
        storage.getBoolean("favourite_message_shown", false)
    }

    private fun isNightModeEnabled(): Boolean {
        val mode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mode == Configuration.UI_MODE_NIGHT_YES
    }

    override suspend fun setUserLogged(logged: Boolean): Boolean = withContext(Dispatchers.IO) {
        storage.edit().putBoolean("user_logged", logged).commit()
    }

    override suspend fun isUserLogged(): Boolean = withContext(Dispatchers.IO) {
        storage.getBoolean("user_logged", false)
    }

}
