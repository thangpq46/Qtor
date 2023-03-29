package com.example.qtor.data.repository

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import com.example.qtor.constant.IN_PAINTING_RADIUS
import com.example.qtor.constant.STORAGE_STICKERS
import com.example.qtor.data.model.Sticker
import com.example.qtor.util.getFolderInData
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import org.opencv.photo.Photo
import java.io.File

class LocalDataSource(private val context: Context) : DataSource {
    override suspend fun getStickers(
        application: Application,
        callback: DataSource.StickersCallback
    ) {
        val listStickers = mutableListOf<Sticker>()
        getFolderInData(application, STORAGE_STICKERS).listFiles()?.forEach {
            listStickers.add(
                Sticker(
                    bitmap = BitmapFactory.decodeFile(it.absolutePath).asImageBitmap()
                )
            )
        }
        callback.onLocalLoaded(listStickers)
    }

    override suspend fun removeObjects(
        image: ImageBitmap,
        mask: ImageBitmap,
        callback: DataSource.RemoveObjectsCallback
    ) {
        callback.onLocalSuccess(image)
    }

    override suspend fun cleanupBitmap(
        image: Bitmap,
        mask: Bitmap,
        callback: DataSource.EraserObjectCallback
    ) {
        val matImage = Mat()
        Utils.bitmapToMat(
            image, matImage
        )
        Imgproc.cvtColor(matImage, matImage, Imgproc.COLOR_RGBA2RGB)
        val matResult = Mat(matImage.rows(), matImage.cols(), matImage.type())
        val matMask = Mat()
        Utils.bitmapToMat(
            mask, matMask
        )
        Imgproc.cvtColor(matMask, matMask, Imgproc.COLOR_BGR2GRAY)
        Photo.inpaint(
            matImage,
            matMask,
            matResult,
            IN_PAINTING_RADIUS,
            Photo.INPAINT_TELEA
        )
        val output = Bitmap.createBitmap(
            matResult.cols(), matResult.rows(), Bitmap.Config.ARGB_8888
        )
        Utils.matToBitmap(matResult, output)
        callback.onLocalComplete(output.asImageBitmap())
    }

    override suspend fun getSticker(
        name: String,
        folderName: String,
        callBack: DataSource.StickerLoadCallBack
    ) {
        val appDir = ContextCompat.getExternalFilesDirs(context, null)[0]
        val filtersDir = File(appDir, folderName)
        if (!filtersDir.exists()) {
            filtersDir.mkdirs()
        }
        filtersDir.listFiles()?.let { files ->
            for (file in files) {
                if (file.name == name) {
                    callBack.onLocalLoad(BitmapFactory.decodeStream(file.inputStream()))
                }
            }
        }
    }

}