package com.ivanmorgillo.corsoandroid.teama.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import com.ivanmorgillo.corsoandroid.teama.extension.exhaustive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface SettingsRepository {

    suspend fun setDarkTheme(darkEnable:Boolean): Boolean
    suspend fun setLanguage(langChoose:Languages):Boolean
    suspend fun isDarkThemeEnabled():Boolean
    suspend fun getLanguage():Languages
}

class SettingsRepositoryImpl(val context: Context) : SettingsRepository{

    private val storage by lazy { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    override suspend fun setDarkTheme(darkEnable: Boolean): Boolean = withContext(Dispatchers.IO) {
       storage.edit().putBoolean("dark_theme", darkEnable).commit()
    }

    @SuppressLint("ApplySharedPref")
    override suspend fun setLanguage(langChoose: Languages): Boolean = withContext(Dispatchers.IO){
        when(langChoose){
            Languages.English -> storage.edit().putString("language", "en").commit()
            Languages.Italian -> storage.edit().putString("language", "it").commit()
        }.exhaustive
    }

    override suspend fun isDarkThemeEnabled(): Boolean = withContext(Dispatchers.IO){
       storage.getBoolean("dark_theme", isNightModeEnabled())
    }

    private fun isNightModeEnabled(): Boolean {
        val mode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mode == Configuration.UI_MODE_NIGHT_YES
    }

    override suspend fun getLanguage(): Languages = withContext(Dispatchers.IO) {
        when(storage.getString("language","it")){
             "it" -> Languages.Italian
             "en" -> Languages.English
             else ->  Languages.Italian
        }
    }
}
