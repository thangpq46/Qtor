package com.example.qtor.util

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicConvolve3x3

class SharpenImageFilter(val context:Context) {
    private val renderScrip = RenderScript.create(context)
    val convolution = ScriptIntrinsicConvolve3x3.create(renderScrip, Element.U8_4(renderScrip))

    fun applyFilter(bitmap: Bitmap, multiplier:Float) : Bitmap{
        val matrixSharpen = floatArrayOf(
            0f,-multiplier,0f,-multiplier,1+4*multiplier,-multiplier,0f,-multiplier,0f
        )
        return applyFilterConvolve(bitmap,matrixSharpen)
    }
    fun applyFilterConvolve(bitmapIn:Bitmap,coefficients:FloatArray):Bitmap{
        val bitmapOut:Bitmap = Bitmap.createBitmap(bitmapIn.width,bitmapIn.height,bitmapIn.config)
        val allocationIn  = Allocation.createFromBitmap(renderScrip,bitmapIn)
        val allocationOut  = Allocation.createFromBitmap(renderScrip,bitmapOut)
        convolution.setInput(allocationIn)
        convolution.setCoefficients(coefficients)
         convolution.forEach(allocationOut)
        allocationOut.copyTo(bitmapOut)
        allocationIn.destroy()
        allocationOut.destroy()
        return bitmapOut
    }
    fun cleanUp(){
        renderScrip.destroy()
    }
}