package com.example.qtor.data.model

import android.graphics.RectF
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig

open class Sticker(
) {
     var rect: RectF = RectF()
    var bitmap: ImageBitmap = ImageBitmap(1,1, ImageBitmapConfig.Argb8888)
    var angle: Float = 0f
    var scale: Float = 1f

    constructor(rectF: RectF= RectF(), bitmap: ImageBitmap, angle:Float=0f, scale:Float=1f) : this() {
        this.rect=rectF
        this.bitmap=bitmap
        this.angle=angle
        this.scale=scale
    }


    override fun equals(other: Any?): Boolean {
        val a = other as Sticker
        return rect == a.rect && angle == a.angle
    }
    open fun copy(rect: RectF=this.rect,bitmap: ImageBitmap=this.bitmap,angle:Float=this.angle,scale:Float=this.scale):Sticker{
        return Sticker(RectF(rect), bitmap,angle,scale )
    }
}

fun Sticker.contains(downX: Float, downY: Float): Boolean {
    if (downX >= this.rect.left && downX <= this.rect.right && downY >= this.rect.top && downY <= this.rect.bottom) {
        return true
    }
    return false
}