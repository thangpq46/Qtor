package com.example.qtor.data.model

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.nativeCanvas
import com.example.qtor.R
import com.example.qtor.constant.STORAGE_FONTS
import com.example.qtor.util.dpToPx

class StickerText(private val context:Context,private val font:Typeface,private val padding:Float=5f) : Sticker() {
    private var text = context.getString(R.string.text_placeholder)
    private var spacing = 1f
    private val paint = Paint()
    private var canvas= Canvas(bitmap)
    private val bounds = Rect()
    init {
        stickerType=StickerType.TEXT
        paint.apply {
            typeface=font
            textSize=40f
        }
        updateImage()
        this.rect= RectF(0f,0f,bitmap.width.toFloat(),bitmap.height.toFloat())
    }
    private fun updateImage(){
        paint.getTextBounds(text,0,text.length,bounds)
        val paddingPx = dpToPx(context, padding).toInt()
        bitmap= ImageBitmap(bounds.width()+2*paddingPx,bounds.height()+2*paddingPx,
            ImageBitmapConfig.Argb8888)
        canvas=Canvas(bitmap)
        canvas.nativeCanvas.drawText(text,bitmap.width/2-bounds.width()/2f,bitmap.height/2+bounds.height()/2f,paint)

    }
    fun updateText(text: String){
        this.text=text
        updateImage()
        this.rect.apply {
            this.right=this.left+bitmap.width*(this.height()/bitmap.height)
        }
    }
    fun updateFont(font: Font){
        val typeface = Typeface.createFromAsset(context.assets, font.fontName)
        paint.apply {
            this.typeface=typeface
        }
        updateImage()
        this.rect.apply {
            this.right=this.left+bitmap.width*(this.height()/bitmap.height)
        }
    }

    fun updateAlpha(alpha:Float){
        paint.apply {
            this.alpha=alpha.toInt()
        }
        updateImage()
    }

    fun updateColor(color: Int){
        paint.apply {
            this.color=color
        }
        updateImage()
    }
    fun getText() = text
}