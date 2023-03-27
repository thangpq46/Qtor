package com.example.qtor.data.repository

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.qtor.constant.*
import com.example.qtor.data.model.Sticker
import com.example.qtor.util.getFolderInData
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

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

    override suspend fun removeObjects(
        image: ImageBitmap,
        mask: ImageBitmap,
        callback: DataSource.RemoveObjectsCallback
    ) {

    }

    override suspend fun cleanupBitmap(
        image: Bitmap,
        mask: Bitmap,
        callback: DataSource.EraserObjectCallback
    ) {
//            val imageByteArray = ByteArrayOutputStream()
//            image.compress(Bitmap.CompressFormat.PNG, IMG_QUALITY, imageByteArray)
//            val maskByteArray = ByteArrayOutputStream()
//            mask.compress(Bitmap.CompressFormat.PNG, IMG_QUALITY, maskByteArray)
//            val requestBody =
//                MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
//                    IMAGE_FILE, FILE_NAME, RequestBody.create(
//                        MediaType.parse(MEDIA_PARSE_TYPE), imageByteArray.toByteArray()
//                    )
//                ).addFormDataPart(
//                    MASK_FILE, FILE_NAME_MASK, RequestBody.create(
//                        MediaType.parse(MEDIA_PARSE_TYPE), maskByteArray.toByteArray()
//                    )
//                ).build()
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .header(HEADER_AUTH_KEY, "Firebase.remoteConfig.getString(CONFIG_API_KEY)")
//            .url("https://clipdrop-api.co/cleanup/v1").post(requestBody).build()
//        try {
//            val response = client.newCall(request).execute()
//             if (response.isSuccessful && response.body() != null) {
//                callback.onCloudComplete(BitmapFactory.decodeStream(response.body().byteStream()).asImageBitmap())
//            } else {
//                callback.onFailed("no internet")
//            }
//        } catch (e: IOException) {
//            callback.onFailed("no internet")
//        }

    }

}