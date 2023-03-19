package com.example.qtor.ui.editor

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.core.graphics.drawable.toBitmap
import com.example.qtor.R
import com.example.qtor.constant.*
import com.example.qtor.data.model.AITarget
import com.example.qtor.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditorView(
    context: Context,
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel
) {
//    viewModel.initStickerS()
    val imagePreview by viewModel.bitmap.collectAsState()
    val viewWidth by viewModel.editorWidth.collectAsState()
    val viewHeight by viewModel.editorHeight.collectAsState()
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
    var scaleF by remember {
        mutableStateOf(1f)
    }
    val mainToolActive by viewModel.mainToolActive.collectAsState()
    val removeObjectToolActive by viewModel.removeObjectToolActive.collectAsState()
    //icon
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
    //rect delete
    val rectDelete by viewModel.rectDelete.collectAsState()
    val rectCopy by viewModel.rectCopy.collectAsState()
    val rectFlip by viewModel.rectFlip.collectAsState()
    val rectScale by viewModel.rectScale.collectAsState()
    val actions = viewModel.removeObjectActions
    //
    var path by remember {
        mutableStateOf<Path?>(Path())
    }
    val d = LocalDensity.current
    var drawX by remember {
        mutableStateOf(0f)
    }
    var drawY by remember {
        mutableStateOf(0f)
    }
    fun moveImage() {

    }

    fun processActionToolRemove(event: MotionEvent): Boolean {
        return when (removeObjectToolActive) {
            DETECT_OBJECT_MODE -> {
                true
            }
            BRUSH_MODE -> {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (event.pointerCount == 1) {
                            downX = event.x
                            downY = event.y
                            path?.moveTo(downX, downY)
                        }
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (event.pointerCount == 1) {
                            downX = event.x
                            downY = event.y
                            path?.lineTo(downX, downY)
                            val temp = path
                            path=null
                            path=temp
                        } else if (event.pointerCount == 2) {
                            moveImage()
                        }
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        viewModel.removeObject(path)
                        path = Path()
                        true
                    }
                    else -> {true}
                }
            }
            LASSO_MODE -> {
                true
            }
            else -> {
                true
            }
        }
    }

    fun processActionToolStickers(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.pointerCount == 2) { 
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
                            viewModel.setStickerActive(ITEM_ACTIVE_NULL)
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
                            viewModel.setStickerActive(i)
                        }
                    }
                    viewModel.updateStickerEx()
                }
                true
            }
            MotionEvent.ACTION_MOVE->{
                if (event.pointerCount == 1) {
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
                } else {
                    //TODO ("Move image")
                }
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

    fun processScaleAndMoveView(event: MotionEvent):Boolean{
        drawX += (event.getX(0) - downX) / d.density
        drawY += (event.getY(0) - downY) / d.density
        currentDistance = getDistance(event)
        val s = (currentDistance / preDistance)
        if (s in 0.9f..1.1f) {
            scaleF *= (currentDistance / preDistance)
        }
        downX = event.getX(0)
        downY = event.getY(0)
        preDistance = currentDistance
        return true
    }

    Canvas(modifier = Modifier
        .offset((drawX / d.density).dp, (drawY / d.density).dp)
        .width(with(LocalDensity.current) { viewWidth.toDp() })
        .height(with(LocalDensity.current) { viewHeight.toDp() })
        .scale(scaleF)
        .drawBehind {
            drawImage(
                imagePreview,
                dstOffset = IntOffset.Zero,
                dstSize = IntSize(viewWidth, viewHeight)
            )
        }
        .pointerInteropFilter { event ->
            when (mainToolActive) {
                MAIN_TOOl_REMOVE_OBJECT -> {
                    processActionToolRemove(event)
                }
                MAIN_TOOL_STICKERS->{
                    processActionToolStickers(event)
                }
                else->{true}
            }
        }) {
//        drawRect(Color.Green)
        if (mainToolActive!= MAIN_TOOl_REMOVE_OBJECT){
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
        when(mainToolActive){
            MAIN_TOOl_REMOVE_OBJECT->{
                actions.lastOrNull()?.let {
                    for (obj in it.aiObjects) {
                        if (obj.isSelected) {
                            drawImage(obj.mask, topLeft = obj.box.offset(), alpha = DRAW_ALPHA)
                        }
                    }
                }
                path?.let { drawPath(it, Color.White , style = Stroke(
                    width = 10.dp.toPx()
                ), alpha = .6f) }
            }
            else->{
            }
        }
    }

}
