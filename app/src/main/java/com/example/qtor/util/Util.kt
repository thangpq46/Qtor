package com.example.qtor.util

import android.app.Application
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.qtor.data.repository.RetrofitClient
import java.io.File
import java.io.IOException
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

fun RectF.getIntSize(): IntSize {
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

fun Int.toDp(context: Context): Dp {
    return (this / context.resources.displayMetrics.density).dp
}

fun removeFolderAndEx(string: String): String {
    return string.substringAfterLast("/").substringBeforeLast(".").uppercase()
}

suspend fun Context.pingGoogle(): Boolean {
    return try {
        RetrofitClient.api.pingGoogle()
        true
    } catch (e: IOException) {
        false
    }

}

fun RectF.getSize(): Size {
    return Size(this.width(), this.height())
}

fun Size.toIntSize(): IntSize {
    return IntSize(this.width.toInt(), this.height.toInt())
}

private fun ccw(a: org.opencv.core.Point, b: org.opencv.core.Point, c: org.opencv.core.Point) =
    ((b.x - a.x) * (c.y - a.y)) > ((b.y - a.y) * (c.x - a.x))

 fun convexHull(p: MutableList<org.opencv.core.Point>): List<org.opencv.core.Point> {
    if (p.isEmpty()) return emptyList()
    p.sortBy {
        it.x
    }
    val h = mutableListOf<org.opencv.core.Point>()

    // lower hull
    for (pt in p) {
        while (h.size >= 2 && !ccw(h[h.size - 2], h.last(), pt)) {
            h.removeAt(h.lastIndex)
        }
        h.add(pt)
    }

    // upper hull
    val t = h.size + 1
    for (i in p.size - 2 downTo 0) {
        val pt = p[i]
        while (h.size >= t && !ccw(h[h.size - 2], h.last(), pt)) {
            h.removeAt(h.lastIndex)
        }
        h.add(pt)
    }

    h.removeAt(h.lastIndex)
    return h
}