package com.example.qtor.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.qtor.data.repository.ImageRepository

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected var storageRepository: ImageRepository

    init {
        storageRepository = ImageRepository()
    }
}
