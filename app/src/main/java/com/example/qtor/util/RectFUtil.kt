package com.example.qtor.util

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import kotlin.math.atan2
import kotlin.math.sqrt


fun RectF.move(offset: Offset) {
    this.left += offset.x
    this.right += offset.x
    this.top += offset.y
    this.bottom += offset.y
}

fun RectF.move(x: Float, y: Float) {
    this.left += x
    this.right += x
    this.top += y
    this.bottom += y
}

fun RectF.offset(): Offset = Offset(this.left, this.top)

fun RectF.getIntOffset() = IntOffset(this.left.toInt(), this.top.toInt())

fun getDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    return sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)).toDouble()).toFloat()
}
fun getAngle(pointBefore: PointF, pointAfter: PointF, rotationPoint: PointF): Double {
    return Math.toDegrees(
        atan2(
            ((pointAfter.y - rotationPoint.y).toDouble()),
            (pointAfter.x - rotationPoint.x).toDouble()
        ) - atan2(
            (pointBefore.y - rotationPoint.y).toDouble(),
            (pointBefore.x - rotationPoint.x).toDouble()
        )
    )
}

fun Rect.offset(): Offset = Offset(this.left.toFloat(), this.top.toFloat())

fun Rect.Size(): Size= Size(this.width().toFloat(), this.height().toFloat())