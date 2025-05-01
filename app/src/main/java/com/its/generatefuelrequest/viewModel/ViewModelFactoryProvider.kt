package com.its.generatefuelrequest.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.its.generatefuelrequest.repositories.Repository

class ViewModelFactoryProvider(private val loginData: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
            return MyViewModel(loginData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}