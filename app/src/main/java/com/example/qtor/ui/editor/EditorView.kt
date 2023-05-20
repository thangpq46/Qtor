package com.example.qtor.ui.editor

import android.content.Context
import android.graphics.RectF
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toSize
import androidx.core.graphics.drawable.toBitmap
import com.example.qtor.R
import com.example.qtor.constant.ADJUST_TOOL
import com.example.qtor.constant.BRUSH_MODE
import com.example.qtor.constant.DETECT_OBJECT_MODE
import com.example.qtor.constant.DRAW_ALPHA
import com.example.qtor.constant.ITEM_ACTIVE_NULL
import com.example.qtor.constant.LASSO_MODE
import com.example.qtor.constant.LOADING
import com.example.qtor.constant.MAIN_TOOL_FILTER
import com.example.qtor.constant.MAIN_TOOL_STICKERS
import com.example.qtor.constant.MAIN_TOOL_TEXT
import com.example.qtor.constant.MAIN_TOOL_TIMESTAMP
import com.example.qtor.constant.MAIN_TOOl_REMOVE_OBJECT
import com.example.qtor.util.Size
import com.example.qtor.util.dpToPx
import com.example.qtor.util.getIntOffset
import com.example.qtor.util.getIntSize
import com.example.qtor.util.isInsideRotatedRect
import com.example.qtor.util.offset


@Composable
fun EditorView(
    context: Context,
    viewModel: EditorViewModel
) {
    val imageBitmaps = viewModel.imageBitmaps
    val currentBitmapIndex by viewModel.currentBitmapIndex.collectAsState()
    val viewWidth by viewModel.editorWidth.collectAsState()
    val viewHeight by viewModel.editorHeight.collectAsState()
    val frame by viewModel.frame.collectAsState()
    var downX by remember {
        mutableStateOf(0f)
    }
    var downY by remember {
        mutableStateOf(0f)
    }
    val itemActive by viewModel.itemActive.collectAsState()
    val stickers = viewModel.stickers
    var scaling by remember {
        mutableStateOf(false)
    }
    val selectedObj by viewModel.selectedObj.collectAsState()
    val a =
        ColorFilter.tint(color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary)
//    var scaleF by remember {
//        mutableStateOf(1f)
//    }
    val scaleF by viewModel.scaleF.collectAsState()
    val screenState by viewModel.stateScreen.collectAsState()
    //Adjust
    val brightness by viewModel.brightness.collectAsState()
    val saturation by viewModel.saturation.collectAsState()
    val contrast by viewModel.contrast.collectAsState()
    val warmth by viewModel.warmth.collectAsState()
    val mainToolActive by viewModel.mainToolActive.collectAsState()
    val removeObjectToolActive by viewModel.removeObjectToolActive.collectAsState()
    //icon
    val icDelete = AppCompatResources.getDrawable(context, R.drawable.ic_delete)
        ?.toBitmap(dpToPx(context, 10).toInt(), dpToPx(context, 10).toInt())
        ?.asImageBitmap()

    val icCopy = AppCompatResources.getDrawable(context, R.drawable.ic_copy)
        ?.toBitmap(dpToPx(context, 10).toInt(), dpToPx(context, 10).toInt())
        ?.asImageBitmap()

    val icFlip = AppCompatResources.getDrawable(context, R.drawable.ic_flip)
        ?.toBitmap(dpToPx(context, 10).toInt(), dpToPx(context, 10).toInt())
        ?.asImageBitmap()

    val icScale = AppCompatResources.getDrawable(context, R.drawable.ic_scale)
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
//    var drawX by remember {
//        mutableStateOf(0f)
//    }
//    var drawY by remember {
//        mutableStateOf(0f)
//    }
    val drawX by viewModel.drawX.collectAsState()
    val drawY by viewModel.drawY.collectAsState()
    val imageMatrix by viewModel.imageMatrix.collectAsState()

    LaunchedEffect(Pair(brightness, contrast)) {
        viewModel.updateImageMatrix()
    }
    LaunchedEffect(Pair(saturation, warmth)) {
        viewModel.updateImageMatrix()
    }

    val drawColor = androidx.compose.material3.MaterialTheme.colorScheme.primary

    fun processScaleAndMoveView(event: PointerEvent) {
//        drawX +=
//        drawY +=
        viewModel.moveImage(
            event.changes[0].positionChange().x,
            event.changes[0].positionChange().y
        )
        viewModel.updateScale(event.calculateZoom())
//        scaleF *= event.calculateZoom()
    }

    fun processToolRemoveObj(event: PointerEvent) {
        if (event.changes.size == 1) {
            when (removeObjectToolActive) {
                BRUSH_MODE, LASSO_MODE -> {
                    path?.lineTo(event.changes.first().position.x, event.changes.first().position.y)
                    val temp = path
                    path = null
                    path = temp
                }

                else -> {}
            }
        } else if (event.changes.size == 2) {
            processScaleAndMoveView(event)
        }
    }

    fun processActionToolSticker(event: PointerEvent) {
        if (event.changes.size == 1) {
            if (itemActive != -1) {
                if (scaling) {
                    viewModel.scaleItemActive(
                        event.changes[0].position.x,
                        event.changes[0].position.y
                    )
                } else {
                    viewModel.moveSticker(itemActive, event.calculatePan())
                }
                viewModel.updateStickerEx()
            }
        } else if (event.changes.size == 2) {
            if (!scaling) {
                if (itemActive != -1 && isInsideRotatedRect(
                        event.changes[0].position.x / scaleF,
                        event.changes[0].position.y / scaleF,
                        stickers[itemActive].rect.centerX(),
                        stickers[itemActive].rect.centerY(),
                        stickers[itemActive].rect,
                        stickers[itemActive].angle
                    ) && isInsideRotatedRect(
                        event.changes[1].position.x / scaleF,
                        event.changes[1].position.y / scaleF,
                        stickers[itemActive].rect.centerX(),
                        stickers[itemActive].rect.centerY(),
                        stickers[itemActive].rect,
                        stickers[itemActive].angle
                    )
                ) {
                    scaling = true
                } else {
                    processScaleAndMoveView(event)
                }
            } else {
//                viewModel.moveSticker(itemActive, event.calculatePan())
                viewModel.tranformItemActive(
                    event.calculateZoom(),
                    event.calculateRotation(),
                    event.calculatePan()
                )
            }


        }
    }

    fun processActionOnDownEvent(event: PointerEvent) {
        path?.moveTo(
            event.changes[0].position.x,
            event.changes[0].position.y
        )
        when (mainToolActive) {
            MAIN_TOOL_STICKERS -> {
                downX = event.changes[0].position.x
                downY = event.changes[0].position.y
                if (itemActive != -1) {
                    var found = false
                    val currentItem = stickers[itemActive]
                    if (isInsideRotatedRect(
                            downX ,
                            downY ,
                            currentItem.rect.centerX(),
                            currentItem.rect.centerY(),
                            rectDelete,
                            currentItem.angle
                        )
                    ) {
                        viewModel.removeSticker()
                    } else if (isInsideRotatedRect(
                            downX ,
                            downY ,
                            currentItem.rect.centerX(),
                            currentItem.rect.centerY(),
                            rectCopy,
                            currentItem.angle
                        )
                    ) {
                        viewModel.addSticker(stickers[itemActive])
                    } else if (isInsideRotatedRect(
                            downX ,
                            downY ,
                            currentItem.rect.centerX(),
                            currentItem.rect.centerY(),
                            rectFlip,
                            currentItem.angle
                        )
                    ) {
                        found = true
                        viewModel.flipItemActiveHorizontally()
                    } else if (isInsideRotatedRect(
                            downX ,
                            downY ,
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
                            downX ,
                            downY ,
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
                drawImage(
                    imageBitmaps[currentBitmapIndex].image,
                    dstOffset = IntOffset.Zero,
                    dstSize = IntSize(viewWidth, viewHeight),
                    colorFilter = ColorFilter.colorMatrix(ColorMatrix(imageMatrix.mColorMatrix.array))
                )
            }
        }
        .pointerInput(Unit) {
            awaitEachGesture {
                this.awaitFirstDown()
                processActionOnDownEvent(currentEvent)
                do {
                    val event = awaitPointerEvent()
                    when (mainToolActive) {
                        MAIN_TOOl_REMOVE_OBJECT -> {
                            processToolRemoveObj(event)
                        }

                        MAIN_TOOL_STICKERS, MAIN_TOOL_FILTER, ADJUST_TOOL, MAIN_TOOL_TEXT, MAIN_TOOL_TIMESTAMP -> {
                            processActionToolSticker(event)
                        }

                        else -> {}
                    }
                    event.changes.forEach { pointerInputChange: PointerInputChange ->
                        if (pointerInputChange.positionChange() != Offset.Zero) pointerInputChange.consume()
                    }
                } while (event.changes.any { it.pressed })
                if (mainToolActive == MAIN_TOOl_REMOVE_OBJECT) {
                    viewModel.removeObject(path) {
                        path = Path()
                    }
                }
                scaling = false
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
            frame?.let {
                drawImage(
                    it,
                    dstOffset = IntOffset.Zero,
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
                                    drawColor, obj.box.offset(), obj.box.Size(), style = Stroke(
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
                selectedObj?.let {
                    drawImage(
                        image = it.mask,
                        dstOffset = it.box.offset().round(),
                        dstSize = IntSize(it.box.width(), it.box.height()),
                        alpha = DRAW_ALPHA,
                        colorFilter = a
                    )
                }
            }

            else -> {
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun TestTimeStamp(){
//    val witdh = with(LocalDensity.current){
//        300.dp.toPx()
//    }.toInt()
//    val height = with(LocalDensity.current){
//        200.dp.toPx()
//    }.toInt()
//    val bit = ImageBitmap(witdh, height, config = ImageBitmapConfig.Argb8888)
//    val c = androidx.compose.ui.graphics.Canvas(bit)
//    c.drawRect(Rect(0f,0f,witdh.toFloat(),height.toFloat()), Paint().apply {
//        this.color= Color.Gray
//        style= PaintingStyle.Fill
//    })
//    val path = android.graphics.Path()
//            path.addArc(RectF(witdh/2f, 100f, witdh.toFloat(), height.toFloat()), 230f, 260f)
//    c.nativeCanvas.drawTextOnPath("Phan Quang Thang",path,0f,0f,android.graphics.Paint().apply {
//        textSize=40f
//    })

    Canvas(modifier = Modifier.fillMaxSize(), onDraw ={
//        drawImage(bit)
//        drawIntoCanvas {
//            val path = android.graphics.Path()
//            path.addArc(Rect(0f, 100f, 200f, 300f), 270f, 180f)
//            it.nativeCanvas.drawTextOnPath("Hello World Example", path, 0f, 0f, paint)
//        }
    } )
}