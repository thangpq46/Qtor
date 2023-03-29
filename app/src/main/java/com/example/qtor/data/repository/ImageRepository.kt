package com.example.qtor.data.repository

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap

class ImageRepository(
    private val remoteDataSource: RemoteDataSource ,
    private val localDataSource: LocalDataSource
) : DataSource {

    override suspend fun getStickers(
        application: Application,
        callback: DataSource.StickersCallback
    ) {
        localDataSource.getStickers(application,callback)
        remoteDataSource.getStickers(application,callback)
    }

    override suspend fun removeObjects(
        image: ImageBitmap,
        mask: ImageBitmap,
        callback: DataSource.RemoveObjectsCallback
    ) {
        localDataSource.removeObjects(image,mask,callback)
        remoteDataSource.removeObjects(image, mask, callback)
    }

    override suspend fun cleanupBitmap(
        image: Bitmap,
        mask: Bitmap,
        callback: DataSource.EraserObjectCallback
    ) {
        localDataSource.cleanupBitmap(image,mask,callback)
    }

    override suspend fun getSticker(
        name: String,
        folderName: String,
        callBack: DataSource.StickerLoadCallBack
    ) {
        localDataSource.getSticker(name,folderName,callBack)
        remoteDataSource.getSticker(name,folderName,callBack)
    }
}
