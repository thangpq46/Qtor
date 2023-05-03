package com.example.qtor.data.repository

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import com.example.qtor.data.model.Sticker

interface DataSource {
    suspend fun getStickers(application: Application, callback: StickersCallback)
    interface StickersCallback {
        fun onLocalLoaded(stickers: MutableList<Sticker>)
        fun onFireBaseLoaded(stickerFB: Sticker)
        fun onDataNotAvailable()
    }

//    suspend fun removeObjects(
//        image: ImageBitmap,
//        mask: ImageBitmap,
//        callback: RemoveObjectsCallback
//    )
//
//    interface RemoveObjectsCallback {
//        fun onLocalSuccess(result: ImageBitmap)
//        fun onCloudSuccess(result: ImageBitmap)
//        fun onFailed(error: String)
//    }

    suspend fun cleanupBitmap(image: Bitmap, mask: Bitmap, callback: EraserObjectCallback)

    interface EraserObjectCallback {
        fun onLocalComplete(result: ImageBitmap)
        fun onCloudComplete(result: ImageBitmap)
        fun onFailed(error: String)
    }

    suspend fun getSticker(name: String, folderName: String, callBack: StickerLoadCallBack)

    interface StickerLoadCallBack {
        fun onLocalLoad(bitmap: Bitmap)
        fun onFireBaseLoad(bitmap: Bitmap)
        fun onLoadFailed(e: Exception)
    }
}