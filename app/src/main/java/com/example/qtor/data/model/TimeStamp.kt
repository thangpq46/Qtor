package com.example.qtor.data.model

import android.content.Context
import android.graphics.RectF
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import com.example.qtor.util.dpToPx
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.max

class TimeStamp(
    private val context: Context,
    private val formatterFirstLine:  DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm"),
    private val formatterSecondLine:  DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd"),
    private val paintSecondLine: android.graphics.Paint,
    private val paintFirstLine: android.graphics.Paint,
    private val padding: Float,
    private val lineSpacing: Float = padding,
    private val singleLine: Boolean = false,
    private var time:  LocalDateTime = LocalDateTime.now()
) : Sticker() {
    private val boundsSecondLine = android.graphics.Rect()
    private val boundsFirstLine = android.graphics.Rect()
    private var canvas: androidx.compose.ui.graphics.Canvas

    init {
        val strFirstLine = time.format(formatterFirstLine)
        val strSecondLine = time.format(formatterSecondLine)
        if (singleLine) {
            paintFirstLine.getTextBounds(
                "$strFirstLine $strSecondLine",
                0,
                "$strFirstLine $strSecondLine".length,
                boundsFirstLine
            )
        } else {
            paintSecondLine.getTextBounds(strSecondLine, 0, strSecondLine.length, boundsSecondLine)
            paintFirstLine.getTextBounds(strFirstLine, 0, strFirstLine.length, boundsFirstLine)
        }

        val paddingPx = dpToPx(context, padding).toInt()
        if (singleLine) {
            bitmap = ImageBitmap(
                boundsFirstLine.width() + 2 * paddingPx,
                (boundsFirstLine.height() + 2 * paddingPx)
            )
        } else {
            bitmap = ImageBitmap(
                max(
                    boundsFirstLine.width() + 2 * paddingPx,
                    boundsSecondLine.width() + 2 * paddingPx
                ),
                (boundsFirstLine.height() + boundsSecondLine.height() + 2 * paddingPx)
            )
        }


        canvas = androidx.compose.ui.graphics.Canvas(bitmap)
//        canvas.drawRect(
//            Rect(
//                0f,
//                0f,
//                bitmap.width.toFloat(),
//                bitmap.height.toFloat()
//            ), Paint().apply {
//                this.color = Color.Gray
//                style = PaintingStyle.Fill
//            })
        if (singleLine) {
            canvas.nativeCanvas.drawText(
                "$strFirstLine  $strSecondLine",
                (bitmap.width / 2 - boundsFirstLine.width() / 2f),
                ((bitmap.height / 2) + (boundsFirstLine.height() / 2f)),
                paintFirstLine
            )
        } else {
            canvas.nativeCanvas.drawText(
                strFirstLine,
                (bitmap.width / 2 - boundsFirstLine.width() / 2f),
                (bitmap.height / 2 + boundsFirstLine.height() / 2f - boundsSecondLine.height()),
                paintFirstLine
            )
            canvas.nativeCanvas.drawText(
                strSecondLine,
                (bitmap.width / 2 - boundsSecondLine.width() / 2f),
                (bitmap.height / 2 + boundsFirstLine.height() / 2f + 2 * lineSpacing),
                paintSecondLine
            )
        }
    }

    fun copy(rectF: RectF ):TimeStamp{
        return TimeStamp( context,
        formatterFirstLine,
        formatterSecondLine,
        paintSecondLine,
        paintFirstLine,
        padding,
        lineSpacing,
        singleLine).apply {
            this.rect=rectF
        }
    }

    fun updateTime(time: LocalDateTime){
        this.time=time
        val strFirstLine = time.format(formatterFirstLine)
        val strSecondLine = time.format(formatterSecondLine)
        if (singleLine) {
            paintFirstLine.getTextBounds(
                "$strFirstLine $strSecondLine",
                0,
                "$strFirstLine $strSecondLine".length,
                boundsFirstLine
            )
        } else {
            paintSecondLine.getTextBounds(strSecondLine, 0, strSecondLine.length, boundsSecondLine)
            paintFirstLine.getTextBounds(strFirstLine, 0, strFirstLine.length, boundsFirstLine)
        }

        val paddingPx = dpToPx(context, padding).toInt()
        if (singleLine) {
            bitmap = ImageBitmap(
                boundsFirstLine.width() + 2 * paddingPx,
                (boundsFirstLine.height() + 2 * paddingPx)
            )
        } else {
            bitmap = ImageBitmap(
                max(
                    boundsFirstLine.width() + 2 * paddingPx,
                    boundsSecondLine.width() + 2 * paddingPx
                ),
                (boundsFirstLine.height() + boundsSecondLine.height() + 2 * paddingPx)
            )
        }


        canvas = androidx.compose.ui.graphics.Canvas(bitmap)
//        canvas.drawRect(
//            Rect(
//                0f,
//                0f,
//                bitmap.width.toFloat(),
//                bitmap.height.toFloat()
//            ), Paint().apply {
//                this.color = Color.Gray
//                style = PaintingStyle.Fill
//            })
        if (singleLine) {
            canvas.nativeCanvas.drawText(
                "$strFirstLine  $strSecondLine",
                (bitmap.width / 2 - boundsFirstLine.width() / 2f),
                ((bitmap.height / 2) + (boundsFirstLine.height() / 2f)),
                paintFirstLine
            )
        } else {
            canvas.nativeCanvas.drawText(
                strFirstLine,
                (bitmap.width / 2 - boundsFirstLine.width() / 2f),
                (bitmap.height / 2 + boundsFirstLine.height() / 2f - boundsSecondLine.height()),
                paintFirstLine
            )
            canvas.nativeCanvas.drawText(
                strSecondLine,
                (bitmap.width / 2 - boundsSecondLine.width() / 2f),
                (bitmap.height / 2 + boundsFirstLine.height() / 2f + 2 * lineSpacing),
                paintSecondLine
            )
        }
    }
}

