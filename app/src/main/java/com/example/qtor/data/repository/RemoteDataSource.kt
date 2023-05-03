package com.example.qtor.data.repository

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import com.example.qtor.constant.*
import com.example.qtor.data.model.Sticker
import com.example.qtor.util.getFolderInData
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RemoteDataSource(private val context: Context) : DataSource {
    override suspend fun getStickers(
        application: Application,
        callback: DataSource.StickersCallback
    ) {
        Firebase.storage.maxDownloadRetryTimeMillis = 5000L
        Firebase.storage.maxOperationRetryTimeMillis = 5000L
        Firebase.storage.getReference(STORAGE_STICKERS).listAll().addOnSuccessListener {
            it.items.forEach { item ->
                val itemFile = File(getFolderInData(application, STORAGE_STICKERS), item.name)
                if (!itemFile.exists()) {
                    itemFile.createNewFile()
                }
                item.getFile(itemFile).addOnSuccessListener {
                    val bit = BitmapFactory.decodeFile(itemFile.absolutePath).asImageBitmap()
                    callback.onFireBaseLoaded(
                        Sticker(
                            bitmap = bit,
                            rect = RectF(0f, 0f, bit.width.toFloat(), bit.height.toFloat())
                        )
                    )
                }.addOnFailureListener {
                    callback.onDataNotAvailable()
                }
            }
        }.addOnFailureListener {
            callback.onDataNotAvailable()
        }
    }

    override suspend fun cleanupBitmap(
        image: Bitmap,
        mask: Bitmap,
        callback: DataSource.EraserObjectCallback
    ) {
        val imageByteArray = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, IMG_QUALITY, imageByteArray)
        val maskByteArray = ByteArrayOutputStream()
        mask.compress(Bitmap.CompressFormat.PNG, IMG_QUALITY, maskByteArray)
        val requestBody =
            MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
                IMAGE_FILE, FILE_NAME, RequestBody.create(
                    MEDIA_PARSE_TYPE.toMediaTypeOrNull(), imageByteArray.toByteArray()
                )
            ).addFormDataPart(
                MASK_FILE, FILE_NAME_MASK, RequestBody.create(
                    MEDIA_PARSE_TYPE.toMediaTypeOrNull(), maskByteArray.toByteArray()
                )
            ).build()
        val client = OkHttpClient()
        val request = Request.Builder()
            .header(HEADER_AUTH_KEY, API_KEY)
            .url("https://clipdrop-api.co/cleanup/v1").post(requestBody).build()
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful && response.body != null) {
                callback.onCloudComplete(BitmapFactory.decodeStream(response.body!!.byteStream()).asImageBitmap())
            } else {
                callback.onFailed(response.code.toString())

            }
        } catch (e: IOException) {
            callback.onFailed(e.message.toString())

        }

    }

    override suspend fun getSticker(
        name: String,
        folderName: String,
        callBack: DataSource.StickerLoadCallBack
    ) {
        Firebase.storage.maxDownloadRetryTimeMillis = 3000
        Firebase.storage.maxOperationRetryTimeMillis = 5000
        val appDir =
            ContextCompat.getExternalFilesDirs(context, null)[0]
        val filtersDir = File(appDir, folderName)
        if (!filtersDir.exists()) {
            filtersDir.mkdirs()
        }
        val stickerFile = File(filtersDir, name)
        if (!stickerFile.exists()) {
            Firebase.storage.getReference("$folderName/")
                .child(name).downloadUrl.addOnSuccessListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = RetrofitClient.api.downloadImage(it.toString())
                            response.body()?.byteStream()?.let {
                                if (!stickerFile.exists()) {
                                    stickerFile.createNewFile()
                                }
                                val fos = FileOutputStream(stickerFile)
                                fos.write(it.readBytes())
                                CoroutineScope(Dispatchers.Main).launch {
                                    callBack.onFireBaseLoad(
                                        BitmapFactory.decodeStream(stickerFile.inputStream())
                                    )
                                }
                            }
                        } catch (e: IOException) {
                            CoroutineScope(Dispatchers.Main).launch {
                                callBack.onLoadFailed(e)
                            }
                        }
                    }
                }.addOnFailureListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        callBack.onLoadFailed(it)
                    }
                }
        }
    }

}