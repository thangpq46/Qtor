package com.example.qtor.util
import android.content.Context
import android.graphics.*

fun createImageBitmapFromText(context: Context,text: String,fontName:String?): Bitmap {
    // Create a new Bitmap with a transparent background
    val paint = Paint().apply {

        fontName?.let {
            this.typeface = Typeface.create(Typeface.createFromAsset(context.assets,fontName), Typeface.NORMAL)
        }
        this.textSize = 72f
        this.color = Color.BLACK // Set the text color
    }
    val textWidth = paint.measureText(text)
    val textHeight = paint.fontMetrics.bottom - paint.fontMetrics.top
    val bitmap = Bitmap.createBitmap(textWidth.toInt(), textHeight.toInt(), Bitmap.Config.ARGB_8888)

    // Create a Canvas object to draw the text onto the Bitmap
    val canvas = Canvas(bitmap)

    // Set the Typeface and text size for the paint object


    // Draw the text onto the Bitmap
    canvas.drawText(text, 1f, textHeight - paint.fontMetrics.bottom + 1f, paint)

    // Convert the Bitmap to an ImageBitmap and return it
    return bitmap
}
