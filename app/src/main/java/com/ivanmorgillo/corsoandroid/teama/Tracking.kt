package com.ivanmorgillo.corsoandroid.teama

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

interface Tracking {
    fun logEvent(eventName: String)
}

class TrackingImpl : Tracking {
    override fun logEvent(eventName: String) {
        Firebase.analytics.logEvent(eventName, null)
    }
}
