package com.example.qtor.data.repository

import android.app.Application

class ImageRepository(
    private val remoteDataSource: RemoteDataSource = RemoteDataSource(),
    private val localDataSource: LocalDataSource = LocalDataSource()
) : DataSource {

    override suspend fun getStickers(
        application: Application,
        callback: DataSource.StickersCallback
    ) {
        localDataSource.getStickers(application,callback)
        remoteDataSource.getStickers(application,callback)
    }
}
