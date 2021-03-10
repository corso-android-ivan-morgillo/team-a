package com.ivanmorgillo.corsoandroid.teama.extension

import android.view.MenuItem

fun MenuItem.visible() {
    isVisible = true
}

fun MenuItem.gone() {
    isVisible = false
}
