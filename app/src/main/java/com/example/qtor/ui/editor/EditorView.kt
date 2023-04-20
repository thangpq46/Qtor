package com.example.qtor.ui.editor

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
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
import com.example.qtor.util.*


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditorView(
    context: Context,
    viewModel: EditorViewModel
) {
    val imageBitmaps = viewModel.imageBitmaps
    val currentBitmapIndex by viewModel.currentBitmapIndex.collectAsState()
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
//    var scaleF by remember {
//        mutableStateOf(1f)
//    }
    val scaleF by viewModel.scaleF.collectAsState()
    var moving by remember {
        mutableStateOf(false)
    }
    val screenState by viewModel.stateScreen.collectAsState()
    //Adjust
    val brightness by viewModel.brightness.collectAsState()
    val saturation by viewModel.saturation.collectAsState()
    val contrast by viewModel.contrast.collectAsState()
    val warmth by viewModel.warmth.collectAsState()
    val mainToolActive by viewModel.mainToolActive.collectAsState()
    val removeObjectToolActive by viewModel.removeObjectToolActive.collectAsState()
    //icon
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
    //
    var path by remember {
        mutableStateOf<Path?>(Path())
    }
    val drawX by viewModel.drawX.collectAsState()
    val drawY by viewModel.drawY.collectAsState()
    val imageMatrix by remember {
        mutableStateOf(ImageMatrix())
    }
    LaunchedEffect(Pair(brightness, contrast)) {
        imageMatrix.mBrightness = brightness
        imageMatrix.mContrast = contrast
        imageMatrix.mSaturation = saturation
        imageMatrix.mWarmth = warmth
        imageMatrix.updateMatrix()
    }
    LaunchedEffect(Pair(saturation, warmth)) {
        imageMatrix.mBrightness = brightness
        imageMatrix.mContrast = contrast
        imageMatrix.mSaturation = saturation
        imageMatrix.mWarmth = warmth
        imageMatrix.updateMatrix()
    }
    val filter by viewModel.filter.collectAsState()

    val drawColor = MaterialTheme.colors.primary

    fun processScaleAndMoveView(event: MotionEvent): Boolean {
        viewModel.moveImage((event.getX(0) - downX) * .8f, (event.getY(0) - downY) * .8f)
        currentDistance = getDistance(event)
        val s = (currentDistance / preDistance)
        if (s in 0.9f..1.1f) {
            viewModel.updateScale((currentDistance / preDistance))
        }
        downX = event.getX(0)
        downY = event.getY(0)
        preDistance = currentDistance
        return true
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
                            downX = event.x / scaleF
                            downY = event.y / scaleF
                            path?.moveTo(downX, downY)
                        } else {
                            preDistance = getDistance(event)
                        }
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (event.pointerCount == 1) {
                            downX = event.x / scaleF
                            downY = event.y / scaleF
                            path?.lineTo(downX, downY)
                            val temp = path
                            path = null
                            path = temp
                        } else if (event.pointerCount == 2) {
                            processScaleAndMoveView(event)
                        }
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        viewModel.removeObject(path) {
                            path = Path()
                        }
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
            LASSO_MODE -> {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (event.pointerCount == 1) {
                            downX = event.x / scaleF
                            downY = event.y / scaleF
                            path?.moveTo(downX, downY)
                        }
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (event.pointerCount == 1) {
                            path?.lineTo(event.x / scaleF, event.y / scaleF)
                            val temp = path
                            path = null
                            path = temp
                        } else if (event.pointerCount == 2) {
                            processScaleAndMoveView(event)
                        }
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        path?.lineTo(downX, downY)
                        viewModel.removeObject(path, mode = LASSO_MODE) {
                            path = Path()
                        }
                        true
                    }
                    else -> {
                        true
                    }
                }
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
                    Log.d("DOWNX", downX.toString())
                    Log.d("DOWNY", downY.toString())
                    if (itemActive != -1) {
                        var found = false
                        val currentItem = stickers[itemActive]
                        Log.d(
                            "currentItem",
                            "L ${currentItem.rect.left}  T ${currentItem.rect.top} R ${currentItem.rect.right} B ${currentItem.rect.bottom} "
                        )
                        if (isInsideRotatedRect(
                                downX / scaleF,
                                downY / scaleF,
                                currentItem.rect.centerX(),
                                currentItem.rect.centerY(),
                                rectDelete,
                                currentItem.angle
                            )
                        ) {
                            viewModel.removeSticker()
                        }
                        if (isInsideRotatedRect(
                                downX / scaleF,
                                downY / scaleF,
                                currentItem.rect.centerX(),
                                currentItem.rect.centerY(),
                                rectCopy,
                                currentItem.angle
                            )
                        ) {
                            viewModel.addSticker(stickers[itemActive])
                        }
                        if (isInsideRotatedRect(
                                downX / scaleF,
                                downY / scaleF,
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
                                downX / scaleF,
                                downY / scaleF,
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
                                downX / scaleF,
                                downY / scaleF,
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
            MotionEvent.ACTION_MOVE -> {
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
                    processScaleAndMoveView(event)
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

    Canvas(modifier = Modifier
        .width(with(LocalDensity.current) { viewWidth.toDp() })
        .height(with(LocalDensity.current) { viewHeight.toDp() })
        .graphicsLayer {
            this.translationX = drawX
            this.translationY = drawY
            this.scaleX = scaleF
            this.scaleY = scaleF
        }
        .drawBehind {
            if (imageBitmaps.isNotEmpty()) {
                for (i in imageMatrix.mColorMatrix.array) {
                    Log.d("AAA", i.toString())
                }
                drawImage(
                    imageBitmaps[currentBitmapIndex].image,
                    dstOffset = IntOffset.Zero,
                    dstSize = IntSize(viewWidth, viewHeight),
                    colorFilter = ColorFilter.colorMatrix(ColorMatrix(imageMatrix.mColorMatrix.array))
                )
            }
        }
        .pointerInteropFilter { event ->
            when (mainToolActive) {
                MAIN_TOOl_REMOVE_OBJECT -> {
                    processActionToolRemove(event)
                }

                MAIN_TOOL_STICKERS -> {
                    processActionToolStickers(event)
                }
                MAIN_TOOL_TEXT -> {
                    processActionToolStickers(event)
                }
                else -> {
                    true
                }
            }
        }

    ) {

        if (mainToolActive != MAIN_TOOl_REMOVE_OBJECT) {
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
            filter?.let {
                drawImage(
                    it,
                    dstOffset = IntOffset.Zero,
                    alpha = FILTER_ALPHA,
                    dstSize = IntSize(viewWidth, viewHeight)
                )
            }
        }
        when (mainToolActive) {
            MAIN_TOOl_REMOVE_OBJECT -> {
                when (removeObjectToolActive) {
                    DETECT_OBJECT_MODE -> {
                        if (imageBitmaps.isNotEmpty()) {
                            for (obj in imageBitmaps[currentBitmapIndex].AIObj) {
                                drawRect(
                                    Color.Green, obj.box.offset(), obj.box.Size(), style = Stroke(
                                        width = 5f
                                    )
                                )
                            }
                        }
                    }
                }
                path?.let {
                    drawPath(
                        it, drawColor, style = Stroke(
                            pathEffect = if (removeObjectToolActive == BRUSH_MODE) null else PathEffect.dashPathEffect(
                                floatArrayOf(10f, 10f),
                                0f
                            ),
                            width = if (removeObjectToolActive == BRUSH_MODE) 10.dp.toPx() else 2.dp.toPx(),
                            cap = StrokeCap.Round
                        ), alpha = .6f
                    )
                    if (removeObjectToolActive == LASSO_MODE && screenState == LOADING) {
                        drawPath(
                            it, drawColor, style = Fill, alpha = .6f
                        )
                    }
                }
            }
            else -> {
            }
        }

    }

}
