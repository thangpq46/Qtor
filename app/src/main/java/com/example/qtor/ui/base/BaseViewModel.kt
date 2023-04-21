package com.example.qtor.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.qtor.data.repository.ImageRepository
import com.example.qtor.data.repository.LocalDataSource
import com.example.qtor.data.repository.RemoteDataSource

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected var repository: ImageRepository

    init {
        repository = ImageRepository(application)
    }
}
