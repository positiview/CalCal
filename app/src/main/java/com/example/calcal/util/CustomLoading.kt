package com.example.calcal.util

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.fragment.app.Fragment
import com.example.calcal.R

object CustomLoading {
    fun showLoading(fragment: Fragment) {
        val dialog = Dialog(fragment.requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun stopDialog(fragment: Fragment?) {
        val dialog = Dialog(fragment!!.requireContext())
        dialog.dismiss()
    }
}
