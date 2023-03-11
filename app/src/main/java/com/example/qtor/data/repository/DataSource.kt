package com.example.qtor.data.repository

import android.app.Application
import com.example.qtor.data.model.Sticker

interface DataSource {
    suspend fun getStickers(application: Application,callback: StickersCallback)
    interface StickersCallback{
        fun onLocalLoaded(stickers: MutableList<Sticker>)
        fun onFireBaseLoaded(stickerFB: Sticker)
        fun onDataNotAvailable()
    }
}