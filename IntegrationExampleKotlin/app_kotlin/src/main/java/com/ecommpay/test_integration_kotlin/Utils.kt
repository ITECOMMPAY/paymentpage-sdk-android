package com.ecommpay.test_integration_kotlin

import android.app.AlertDialog
import android.content.Context

object Utils {
    internal fun showMessage(context: Context, message: String){
        val builder = AlertDialog.Builder(context)
        with(builder) {
            setMessage(message)
            setPositiveButton(R.string.ok_text) { _, _ ->  }
        }
        builder.create().show()
    }
}