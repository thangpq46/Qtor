package com.example.qtor.data.repository

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.example.qtor.util.pingGoogle

class ImageRepository(
    private val context: Context
) : DataSource {
    private val remoteDataSource: RemoteDataSource = RemoteDataSource(context)

    private val localDataSource: LocalDataSource = LocalDataSource(context)
    override suspend fun getStickers(
        application: Application,
        callback: DataSource.StickersCallback
    ) {
        localDataSource.getStickers(application, callback)
        remoteDataSource.getStickers(application, callback)
    }

    override suspend fun cleanupBitmap(
        image: Bitmap,
        mask: Bitmap,
        callback: DataSource.EraserObjectCallback
    ) {
        if (context.pingGoogle()) {
            remoteDataSource.cleanupBitmap(image, mask, callback)
        } else {
            localDataSource.cleanupBitmap(image, mask, callback)
        }
    }

    override suspend fun getSticker(
        name: String,
        folderName: String,
        callBack: DataSource.StickerLoadCallBack
    ) {
        localDataSource.getSticker(name, folderName, callBack)
        remoteDataSource.getSticker(name, folderName, callBack)
    }
}
