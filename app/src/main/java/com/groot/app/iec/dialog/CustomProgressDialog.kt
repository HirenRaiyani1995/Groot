package com.groot.app.iec.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.groot.app.iec.R

class CustomProgressDialog(context: Context?) : Dialog(
    context!!
) {
    public override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.custom_progress_dialog)
    }
}