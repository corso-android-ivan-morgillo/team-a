package com.ivanmorgillo.corsoandroid.teama.extension

import android.content.DialogInterface
import android.view.View
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.showAlertDialog(
    title: String,
    message: String,
    icon: Int,
    positiveButtonText: String,
    onPositiveButtonClick: () -> Unit,
    neutralButtonText: String,
    onNeutralButtonClick: () -> Unit,
) {
    MaterialAlertDialogBuilder(this.context)
        .setTitle(title)
        .setMessage(message)
        .setIcon(icon)
        .setPositiveButton(positiveButtonText) { dialog, which ->
            onPositiveButtonClick()
        }
        .setNeutralButton(neutralButtonText) { dialogInterface: DialogInterface, i: Int ->
            onNeutralButtonClick()
        }
        .setCancelable(false)
        .show()
}
