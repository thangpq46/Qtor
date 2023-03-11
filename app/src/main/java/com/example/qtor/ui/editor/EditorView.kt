package com.example.qtor.ui.editor

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.core.graphics.drawable.toBitmap
import com.example.qtor.R
import com.example.qtor.constant.FIRST_INDEX
import com.example.qtor.constant.ITEM_ACTIVE_NULL
import com.example.qtor.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditorView(
    context: Context,
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel
) {
    val imagePreview by viewModel.bitmap.collectAsState()
    val imageOffset by viewModel.imageOffset.collectAsState()
    val imageSize by viewModel.imageSize.collectAsState()
    var downX by remember {
        mutableStateOf(0f)
    }
    var downY by remember {
        mutableStateOf(0f)
    }
    val itemActive by viewModel.itemActive.collectAsState()
    val stickers = viewModel.stickers
    var preDistance by remember {
        mutableStateOf(0f)
    }
    var currentDistance by remember {
        mutableStateOf(0f)
    }
    var scaling by remember {
        mutableStateOf(false)
    }
    val drawColor = MaterialTheme.colors.primary
    val icDelete = context.getDrawable(R.drawable.ic_delete)
        ?.toBitmap(dpToPx(context, 10).toInt(), dpToPx(context, 10).toInt())
        ?.asImageBitmap()
    val icCopy = context.getDrawable(R.drawable.ic_copy)
        ?.toBitmap(dpToPx(context, 10).toInt(), dpToPx(context, 10).toInt())
        ?.asImageBitmap()
    val icFlip = context.getDrawable(R.drawable.ic_flip)
        ?.toBitmap(dpToPx(context, 10).toInt(), dpToPx(context, 10).toInt())
        ?.asImageBitmap()
    val icScale = context.getDrawable(R.drawable.ic_scale)
        ?.toBitmap(dpToPx(context, 10).toInt(), dpToPx(context, 10).toInt())
        ?.asImageBitmap()
    val rectDelete by viewModel.rectDelete.collectAsState()
    val rectCopy by viewModel.rectCopy.collectAsState()
    val rectFlip by viewModel.rectFlip.collectAsState()
    val rectScale by viewModel.rectScale.collectAsState()
    Canvas(modifier = modifier
        .pointerInteropFilter { event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x;
                    downY = event.y;
                    if (event.pointerCount == 2) {
                        downX = event.getX(FIRST_INDEX)
                        downX = event.getY(FIRST_INDEX)
                        preDistance = getDistance(event)
                    } else if (event.pointerCount == 1) {
                        downX = event.x
                        downY = event.y
                        if (itemActive != -1) {
                            var found = false
                            val currentItem = stickers[itemActive]
                            if (isInsideRotatedRect(
                                    downX,
                                    downY,
                                    currentItem.rect.centerX(),
                                    currentItem.rect.centerY(),
                                    rectDelete,
                                    currentItem.angle
                                )
                            ) {
                                viewModel.removeSticker()
                            }
                            if (isInsideRotatedRect(
                                    downX,
                                    downY,
                                    currentItem.rect.centerX(),
                                    currentItem.rect.centerY(),
                                    rectCopy,
                                    currentItem.angle
                                )
                            ) {
                                viewModel.addSticker(stickers[itemActive])
                            }
                            if (isInsideRotatedRect(
                                    downX,
                                    downY,
                                    currentItem.rect.centerX(),
                                    currentItem.rect.centerY(),
                                    rectFlip,
                                    currentItem.angle
                                )
                            ) {
                                found = true
                                viewModel.flipItemActiveHorizontally()
                            }
                            if (isInsideRotatedRect(
                                    downX,
                                    downY,
                                    currentItem.rect.centerX(),
                                    currentItem.rect.centerY(),
                                    rectScale,
                                    currentItem.angle
                                )
                            ) {
                                found = true
                                scaling = true
                            }
                            if (!found) {
                                viewModel.setItemActive(ITEM_ACTIVE_NULL)
                            }
                        }
                        for (i in stickers.indices) {
                            val currentItem = stickers[i]
                            if (isInsideRotatedRect(
                                    downX,
                                    downY,
                                    currentItem.rect.centerX(),
                                    currentItem.rect.centerY(),
                                    currentItem.rect,
                                    currentItem.angle
                                )
                            ) {
                                viewModel.setItemActive(i)
                            }
                        }
                        viewModel.updateStickerEx()
                    }
                    true
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    preDistance = getDistance(event)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount == 2) {
                        viewModel.moveImage(Offset(event.x - downX, event.y - downY))
                        currentDistance = getDistance(event)
                        viewModel.scaleImage(currentDistance / preDistance)
                    } else {
                        if (itemActive != -1) {
                            if (scaling) {
                                viewModel.scaleItemActive(event.x, event.y)
                            } else {
                                val moveX = (event.x - downX)
                                val moveY = (event.y - downY)
                                viewModel.moveSticker(itemActive, moveX, moveY)
                            }
                            viewModel.updateStickerEx()
                        }
                        downX = event.x
                        downY = event.y
                    }
                    downX = event.x
                    downY = event.y
                    preDistance = currentDistance
                    true
                }
                MotionEvent.ACTION_UP -> {
                    scaling = false
                    true
                }
                else -> {
                    true
                }
            }
        }
        .onGloballyPositioned { coordinates ->
            viewModel.setViewHeight(coordinates.size.height)
        }) {
        drawImage(imagePreview, dstOffset = imageOffset, dstSize = imageSize)
        for (i in stickers.indices) {
            rotate(
                stickers[i].angle,
                Offset(stickers[i].rect.centerX(), stickers[i].rect.centerY())
            ) {
                drawImage(
                    stickers[i].bitmap,
                    dstOffset = stickers[i].rect.getIntOffset(),
                    dstSize = stickers[i].rect.getIntSize()
                )
                if (itemActive == i) {
                    drawRect(
                        drawColor,
                        stickers[i].rect.offset(),
                        stickers[i].rect.getIntSize().toSize(),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    if (icDelete != null && icCopy != null && icFlip != null && icScale != null) {
                        drawImage(
                            icDelete,
                            dstOffset = rectDelete.getIntOffset(),
                            dstSize = rectDelete.getIntSize(),
                            colorFilter = ColorFilter.tint(drawColor)
                        )
                        drawImage(
                            icCopy,
                            dstOffset = rectCopy.getIntOffset(),
                            dstSize = rectCopy.getIntSize(),
                            colorFilter = ColorFilter.tint(drawColor)
                        )
                        drawImage(
                            icFlip,
                            dstOffset = rectFlip.getIntOffset(),
                            dstSize = rectFlip.getIntSize(),
                            colorFilter = ColorFilter.tint(drawColor)
                        )
                        drawImage(
                            icScale,
                            dstOffset = rectScale.getIntOffset(),
                            dstSize = rectScale.getIntSize(),
                            colorFilter = ColorFilter.tint(drawColor)
                        )
                    }
                }
            }
        }
    }
}
