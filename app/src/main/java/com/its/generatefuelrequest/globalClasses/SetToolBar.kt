package com.its.generatefuelrequest.globalClasses

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SetToolBar {
    companion object {
        fun setToolbar(
            activity: AppCompatActivity,
            toolbar: Toolbar?,
            title: String?,
            homeIndicatorDrawableId: Int
        ) {
            activity.setSupportActionBar(toolbar)
            val actionBar = activity.supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.title = title
                actionBar.setHomeAsUpIndicator(homeIndicatorDrawableId)
            }
        }
    }
}