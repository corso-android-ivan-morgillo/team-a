package com.ivanmorgillo.corsoandroid.teama.settings

import android.content.Context
import android.content.res.Configuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface SettingsRepository {

    suspend fun setDarkTheme(darkEnable:Boolean): Boolean
    suspend fun isDarkThemeEnabled():Boolean
}

class SettingsRepositoryImpl(val context: Context) : SettingsRepository{

    private val storage by lazy { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    override suspend fun setDarkTheme(darkEnable: Boolean): Boolean = withContext(Dispatchers.IO) {
       storage.edit().putBoolean("dark_theme", darkEnable).commit()
    }

    override suspend fun isDarkThemeEnabled(): Boolean = withContext(Dispatchers.IO){
       storage.getBoolean("dark_theme", isNightModeEnabled())
    }

    private fun isNightModeEnabled(): Boolean {
        val mode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mode == Configuration.UI_MODE_NIGHT_YES
    }

}
