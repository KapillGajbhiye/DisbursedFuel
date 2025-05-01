package com.its.generatefuelrequest.view.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.its.generatefuelrequest.R

class CustomProgressBar(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_layout)
    }
}

