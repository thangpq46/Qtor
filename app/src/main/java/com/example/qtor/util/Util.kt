package com.example.qtor.util

import android.app.Application
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import java.io.File
import kotlin.math.sqrt

fun getFolderInData(application: Application, folderName: String): File {
    val folderDir = File(application.getExternalFilesDirs(null)[0], folderName)
    if (!folderDir.exists()) {
        folderDir.mkdirs()
    }
    return folderDir
}

fun dpToPx(context: Context, dp: Int): Float {
    return context.resources.displayMetrics.density * dp
}

fun Offset.set(x: Float, y: Float) {
    this.minus(this)
    this.plus(Offset(x, y))
}

fun isInsideRotatedRect(
    x: Float, y: Float, centerX: Float, centerY: Float, rect: RectF, angle: Float
): Boolean {
    //  Transform touch coordinates to canvas coordinates
    var x1 = x
    var y1 = y
    val pts = floatArrayOf(x1, y1)
    val matrix = Matrix()
    matrix.postRotate(-angle, centerX, centerY)
    matrix.mapPoints(pts)
    x1 = pts[0]
    y1 = pts[1]

    //  Rotate the canvas back
    matrix.reset()
    matrix.postRotate(angle, centerX, centerY)

    //  Check if the touch is inside the unrotated rect
    return rect.contains(x1, y1)
}

fun getDistance(event: MotionEvent): Float {
    val dx = event.getX(1) - event.getX(0)
    val dy = event.getY(1) - event.getY(0)
    return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
}

fun RectF.getIntSize():IntSize{
    return IntSize(this.width().toInt(), this.height().toInt())
}

fun RectF.scale(factor: Float) {
    val oldWidth = width()
    val oldHeight = height()
    val rectCenterX = left + oldWidth / 2F
    val rectCenterY = top + oldHeight / 2F
    val newWidth = oldWidth * factor
    val newHeight = oldHeight * factor
    left = rectCenterX - newWidth / 2F
    right = rectCenterX + newWidth / 2F
    top = rectCenterY - newHeight / 2F
    bottom = rectCenterY + newHeight / 2F
}

fun Int.toDp(context: Context):Dp{
    return (this/context.resources.displayMetrics.density).dp
}

fun removeFolderAndEx(string: String):String{
    return string.substringAfterLast("/") .substringBeforeLast(".").uppercase()
}