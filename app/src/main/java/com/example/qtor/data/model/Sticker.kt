package com.example.qtor.data.model

import android.graphics.RectF
import androidx.compose.ui.graphics.ImageBitmap

data class Sticker(
    var rect: RectF = RectF(),
    var bitmap: ImageBitmap,
    var angle: Float = 0f,
    var scale: Float = 1f
) {
    override fun equals(other: Any?): Boolean {
        val a = other as Sticker
        return rect == a.rect && angle == a.angle
    }
}

fun Sticker.contains(downX: Float, downY: Float): Boolean {
    if (downX >= this.rect.left && downX <= this.rect.right && downY >= this.rect.top && downY <= this.rect.bottom) {
        return true
    }
    return false
}