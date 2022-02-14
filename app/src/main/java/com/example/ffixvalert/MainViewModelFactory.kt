package com.example.ffixvalert

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(private val app: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FFixvAlertViewModel::class.java)){
            return FFixvAlertViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}