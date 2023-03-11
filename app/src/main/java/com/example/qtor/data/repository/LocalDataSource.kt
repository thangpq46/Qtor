package com.example.qtor.data.repository

import android.app.Application
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import com.example.qtor.constant.STORAGE_STICKERS
import com.example.qtor.data.model.Sticker
import com.example.qtor.util.getFolderInData

class LocalDataSource:DataSource {
    override suspend fun getStickers(
        application: Application,
        callback: DataSource.StickersCallback
    ) {
        val listStickers = mutableListOf<Sticker>()
        getFolderInData(application, STORAGE_STICKERS).listFiles()?.forEach {
            listStickers.add(Sticker(bitmap =BitmapFactory.decodeFile(it.absolutePath).asImageBitmap()))
        }
        callback.onLocalLoaded(listStickers)
    }
}