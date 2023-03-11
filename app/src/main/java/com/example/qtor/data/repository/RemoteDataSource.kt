package com.example.qtor.data.repository

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.RectF
import androidx.compose.ui.graphics.asImageBitmap
import com.example.qtor.constant.STORAGE_STICKERS
import com.example.qtor.data.model.Sticker
import com.example.qtor.util.getFolderInData
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class RemoteDataSource:DataSource {
    override suspend fun getStickers(application: Application,callback: DataSource.StickersCallback) {
        Firebase.storage.getReference(STORAGE_STICKERS).listAll().addOnSuccessListener {
            it.items.forEach { item ->
                val itemFile = File(getFolderInData(application, STORAGE_STICKERS),item.name)
                if (!itemFile.exists()){
                    itemFile.createNewFile()
                }
                item.getFile(itemFile).addOnSuccessListener {
                    val bit = BitmapFactory.decodeFile(itemFile.absolutePath).asImageBitmap()
                    callback.onFireBaseLoaded(Sticker(bitmap = bit, rect = RectF(0f,0f,bit.width.toFloat(),bit.height.toFloat())))
                }.addOnFailureListener {

                }
            }
        }.addOnFailureListener {

        }
    }
}