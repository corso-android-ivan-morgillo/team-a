package com.ivanmorgillo.corsoandroid.teama

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

sealed class Screens {
    abstract val className: String
    abstract val name: String

    object Category : Screens() {
        override val name: String = "Category"
        override val className: String = "Category Fragment"
    }

    object Recipes : Screens() {
        override val name: String = "Recipes"
        override val className: String = "Recipe Fragment"
    }

    object Details : Screens() {
        override val name: String = "Details"
        override val className = "Recipe Detail Fragment"
    }

    object Favourites : Screens() {
        override val name: String = "Favourites"
        override val className = "Favourite Fragment"
    }

    object Area : Screens() {
        override val name: String = "Area"
        override val className = "Area Fragment"
    }

    object AreaRecipes : Screens() {
        override val name: String = "Area Recipes"
        override val className = "Area Recipe Fragment"
    }
}

interface Tracking {
    fun logEvent(eventName: String)
    fun logScreen(screen: Screens)
}

class TrackingImpl : Tracking {
    override fun logEvent(eventName: String) {
        Firebase.analytics.logEvent(eventName, null)
    }

    override fun logScreen(screen: Screens) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screen.name)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screen.className)
        }
    }
}
