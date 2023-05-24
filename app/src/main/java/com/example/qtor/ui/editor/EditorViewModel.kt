package com.example.qtor.ui.editor

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.rotate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.qtor.R
import com.example.qtor.constant.APP_NAME
import com.example.qtor.constant.ASSET_PATH
import com.example.qtor.constant.BLUR
import com.example.qtor.constant.BRUSH_MODE
import com.example.qtor.constant.CANNY_THRESHOLD_1
import com.example.qtor.constant.CANNY_THRESHOLD_2
import com.example.qtor.constant.FRAME_FOLDER
import com.example.qtor.constant.INDILE
import com.example.qtor.constant.ITEM_ACTIVE_NULL
import com.example.qtor.constant.LABEL_PERSON
import com.example.qtor.constant.LOADING
import com.example.qtor.constant.LOCAL_MODEL_FILE_PATH
import com.example.qtor.constant.NEW_EXTENSION
import com.example.qtor.constant.OLD_EXTENSION
import com.example.qtor.constant.PERCENT_INCREASE_HEIGHT_AI_PEOPLE
import com.example.qtor.constant.RECT_ITEM_EDIT_SIZE
import com.example.qtor.constant.STORAGE_FONTS
import com.example.qtor.constant.STORAGE_FRAMES
import com.example.qtor.constant.STORAGE_STICKERS
import com.example.qtor.constant.TYPE_OTHERS
import com.example.qtor.constant.TYPE_PEOPLE
import com.example.qtor.constant.ZERO
import com.example.qtor.data.model.AITarget
import com.example.qtor.data.model.AssetItem
import com.example.qtor.data.model.FilterObj
import com.example.qtor.data.model.Font
import com.example.qtor.data.model.ImageAction
import com.example.qtor.data.model.Sticker
import com.example.qtor.data.model.StickerType
import com.example.qtor.data.model.TimeStamp
import com.example.qtor.data.repository.DataSource
import com.example.qtor.ui.base.BaseViewModel
import com.example.qtor.util.SharpenImageFilter
import com.example.qtor.util.convexHull
import com.example.qtor.util.createImageBitmapFromText
import com.example.qtor.util.dpToPx
import com.example.qtor.util.getAngle
import com.example.qtor.util.getDistance
import com.example.qtor.util.getSize
import com.example.qtor.util.move
import com.example.qtor.util.offset
import com.example.qtor.util.toIntSize
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditorViewModel(private val application: Application) : BaseViewModel(application) {
    private val _imageActions = mutableStateListOf<ImageAction>()
    val imageBitmaps: List<ImageAction> = _imageActions
    private val _stickers = mutableStateListOf<Sticker>()
    val stickers: List<Sticker> = _stickers
    private val deviceWidth = application.resources.displayMetrics.widthPixels
    private val deviceHeight = application.resources.displayMetrics.heightPixels
    private val _rectDelete = MutableStateFlow(RectF(ZERO, ZERO, ZERO, ZERO))
    val rectDelete: StateFlow<RectF> get() = _rectDelete
    private val _notification = MutableStateFlow("")
    val notification: StateFlow<String> = _notification
    private val sharpenTool = SharpenImageFilter(application)

    fun sharpImage(image: ImageBitmap, multier: Float) {

    }

    private val _rectFlip = MutableStateFlow(RectF(ZERO, ZERO, ZERO, ZERO))
    val rectFlip: StateFlow<RectF> get() = _rectFlip

    private val _rectCopy = MutableStateFlow(RectF(ZERO, ZERO, ZERO, ZERO))
    val rectCopy: StateFlow<RectF> get() = _rectCopy

    private val _rectScale = MutableStateFlow(RectF(ZERO, ZERO, ZERO, ZERO))
    val rectScale: StateFlow<RectF> get() = _rectScale

    private val _currentBitmapIndex = MutableStateFlow(0)
    val currentBitmapIndex: StateFlow<Int> = _currentBitmapIndex

    private val _drawX = MutableStateFlow(0f)
    private val _drawY = MutableStateFlow(0f)
    val drawX: StateFlow<Float> = _drawX
    val drawY: StateFlow<Float> = _drawY

    fun moveImage(x: Float, y: Float) {
        _drawX.update {
            it + x
        }
        _drawY.update {
            it + y
        }
    }

    private val _scaleF = MutableStateFlow(1f)
    val scaleF: StateFlow<Float> = _scaleF
    fun resetDrawPos() {
        _drawX.value = 0f
        _drawY.value = 0f
        _scaleF.value = 1f
    }

    fun updateScale(f: Float) {
        _scaleF.getAndUpdate {
            it * f
        }
    }

    internal fun initBitmaps(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            Glide.with(application).asBitmap().load(uri).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    _imageActions.add(ImageAction(resource.asImageBitmap(), mutableListOf()))
                    initEditorSize(resource)
                    val scaleBitmap =
                        Bitmap.createScaledBitmap(
                            resource,
                            _editorWidth.value,
                            _editorHeight.value,
                            true
                        )
                    val image = InputImage.fromBitmap(scaleBitmap, 0)
                    segmenter.process(image).addOnSuccessListener { results ->
                        val mask = results.buffer
                        val maskWidth = results.width
                        val maskHeight = results.height
                        val colors = IntArray(maskWidth * maskHeight)
                        for (i in 0 until maskWidth * maskHeight) {
                            val backgroundLikelihood = 1 - mask.float

                            if (backgroundLikelihood > 0.9) {
                            } else if (backgroundLikelihood > 0.2) {
                                colors[i] = android.graphics.Color.WHITE
                            } else {
                                colors[i] = android.graphics.Color.WHITE
                            }
                        }
                        val maskAI = Bitmap.createBitmap(
                            colors, maskWidth, maskHeight, Bitmap.Config.ARGB_8888
                        )
                        objectDetector.process(image).addOnSuccessListener { detectedObjects ->
                            val list = mutableListOf<AITarget>()
                            for (obj in detectedObjects) {
                                if (obj.labels.size > 0 && obj.labels[0].text == LABEL_PERSON) {
                                    if (obj.boundingBox.top - obj.boundingBox.height() / PERCENT_INCREASE_HEIGHT_AI_PEOPLE > 0) {
                                        obj.boundingBox.top =
                                            obj.boundingBox.top - obj.boundingBox.height() / PERCENT_INCREASE_HEIGHT_AI_PEOPLE
                                    }
                                    val maskPeopleObj = Bitmap.createBitmap(
                                        maskAI,
                                        obj.boundingBox.left,
                                        obj.boundingBox.top,
                                        obj.boundingBox.width(),
                                        obj.boundingBox.height()
                                    )


                                    val origin = Bitmap.createBitmap(
                                        scaleBitmap,
                                        obj.boundingBox.left,
                                        obj.boundingBox.top,
                                        obj.boundingBox.width(),
                                        obj.boundingBox.height()
                                    )
                                    Log.d("AAA", obj.labels.toString())
//                                    if (obj.labels.size > 0 && obj.labels[0].text == LABEL_PERSON) {
                                    list.add(
                                        AITarget(
                                            obj.boundingBox,
                                            maskPeopleObj.asImageBitmap(),
                                            origin.asImageBitmap(),
                                            TYPE_PEOPLE
                                        )
                                    )
//                                    } else {
//                                        list.add(
//                                            AITarget(
//                                                obj.boundingBox,
//                                                maskOthersObj.asImageBitmap(),
//                                                origin.asImageBitmap(),
//                                                TYPE_OTHERS
//                                            )
//                                        )
//                                    }
                                } else {
                                    val origin = Bitmap.createBitmap(
                                        scaleBitmap,
                                        obj.boundingBox.left,
                                        obj.boundingBox.top,
                                        obj.boundingBox.width(),
                                        obj.boundingBox.height()
                                    )
                                    val temp = Bitmap.createBitmap(
                                        scaleBitmap,
                                        obj.boundingBox.left,
                                        obj.boundingBox.top,
                                        obj.boundingBox.width(),
                                        obj.boundingBox.height()
                                    )
                                    val imageMat = Mat()
                                    val outMat = Mat()
                                    Utils.bitmapToMat(temp, imageMat)
                                    Imgproc.blur(imageMat, imageMat, Size(BLUR, BLUR))
                                    Imgproc.Canny(
                                        imageMat,
                                        outMat,
                                        CANNY_THRESHOLD_1,
                                        CANNY_THRESHOLD_2
                                    )
                                    val listContours = mutableListOf<MatOfPoint>()
                                    Imgproc.findContours(
                                        outMat,
                                        listContours,
                                        Mat(),
                                        Imgproc.RETR_LIST,
                                        Imgproc.CHAIN_APPROX_NONE
                                    )
                                    val contourOjb = android.graphics.Path()
                                    val lp = mutableListOf<org.opencv.core.Point>()
                                    for (c in listContours) {
                                        lp.addAll(c.toList())
                                    }
                                    if (lp.size > 20) {
                                        val a = convexHull(lp)
                                        for (p in a) {
                                            contourOjb.lineTo(p.x.toFloat(), p.y.toFloat())
                                        }
                                        contourOjb.lineTo(a[0].x.toFloat(), a[0].y.toFloat())
//                                        contourOjb.translate(Offset(
//                                            obj.boundingBox.left.toFloat(),
//                                            obj.boundingBox.top.toFloat()
//                                        ))
                                    } else {
                                        contourOjb.moveTo(
                                            obj.boundingBox.left.toFloat(),
                                            obj.boundingBox.top.toFloat()
                                        )
                                        contourOjb.lineTo(
                                            obj.boundingBox.right.toFloat(),
                                            obj.boundingBox.top.toFloat()
                                        )
                                        contourOjb.lineTo(
                                            obj.boundingBox.right.toFloat(),
                                            obj.boundingBox.bottom.toFloat()
                                        )
                                        contourOjb.lineTo(
                                            obj.boundingBox.left.toFloat(),
                                            obj.boundingBox.bottom.toFloat()
                                        )
                                        contourOjb.lineTo(
                                            obj.boundingBox.left.toFloat(),
                                            obj.boundingBox.top.toFloat()
                                        )
                                    }
                                    val maskOthersObj = Bitmap.createBitmap(
                                        obj.boundingBox.width(),
                                        obj.boundingBox.height(),
                                        Bitmap.Config.ARGB_8888
                                    )
                                    val canvas = android.graphics.Canvas(maskOthersObj)
//                                    canvas.drawColor(android.graphics.Color.WHITE)
                                    canvas.drawPath(contourOjb, android.graphics.Paint().apply {
                                        this.strokeWidth = 25f
                                        color = android.graphics.Color.WHITE
                                        style = android.graphics.Paint.Style.FILL_AND_STROKE
                                    })
                                    list.add(
                                        AITarget(
                                            obj.boundingBox,
                                            maskOthersObj.asImageBitmap(),
                                            origin.asImageBitmap(),
                                            TYPE_OTHERS
                                        )
                                    )
                                }
                            }
                            _imageActions[0].AIObj.apply {
                                addAll(list)
                            }
                        }.addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                    }.addOnFailureListener { e ->
                        e.printStackTrace()
                    }
//            }
                    _currentBitmapIndex.value = _imageActions.lastIndex
                }

                override fun onLoadCleared(placeholder: Drawable?) {
//                    TODO("Not yet implemented")
                }

            })

        }
    }

    private val _selectedObj = MutableStateFlow<AITarget?>(null)
    val selectedObj: StateFlow<AITarget?> = _selectedObj

    internal fun addSticker(sticker: Sticker) {
        viewModelScope.launch(Dispatchers.IO) {
            val rect = RectF(sticker.rect)
            rect.move(dpToPx(application, 15), dpToPx(application, 15))
            _stickers.add(sticker.copy(rect = rect))
            setStickerActive(_stickers.lastIndex)
            updateStickerEx()
        }
    }

    fun moveSticker(index: Int, moveX: Float, moveY: Float) {
        val a = _stickers.toMutableList().apply {
            this[index].rect.move(moveX, moveY)
        }
        _stickers.clear()
        _stickers.addAll(a)
    }

    fun moveSticker(index: Int, offset: Offset) {
        val a = _stickers.toMutableList().apply {
            this[index].rect.move(offset)
        }
        _stickers.clear()
        _stickers.addAll(a)
    }

    private var _itemActive = MutableStateFlow(ITEM_ACTIVE_NULL)
    val itemActive: StateFlow<Int> = _itemActive
    internal fun setStickerActive(index: Int) {
        _itemActive.value = index
        if (_itemActive.value!= ITEM_ACTIVE_NULL && _stickers[itemActive.value].stickerType==StickerType.TIMESTAMP){
            _timeStampInActive.update {
                true
            }
        }else{
            _timeStampInActive.update {
                false
            }
        }
    }

    internal fun removeSticker() {
        _stickers.removeAt(_itemActive.value)
        setStickerActive(ITEM_ACTIVE_NULL)
    }

    private val _mainToolActive = MutableStateFlow(-1)
    val mainToolActive: StateFlow<Int> = _mainToolActive
    internal fun setMainToolActive(index: Int) {
        _mainToolActive.value = index
    }

    internal fun removeSticker(sticker: Sticker) {
        _stickers.remove(sticker)
    }

    internal fun flipItemActiveHorizontally() {
        //  warning : might have error here
//        viewModelScope.launch(Dispatchers.IO) {
        val bitmap = _stickers[_itemActive.value].bitmap.asAndroidBitmap()
        val matrix =
            Matrix().apply { postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f) }
        val newBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                .asImageBitmap()
        _stickers[_itemActive.value] = _stickers[_itemActive.value].apply {
            this.bitmap = newBitmap
        }
//        }
    }

    internal fun updateStickerEx() {
        if (_itemActive.value != ITEM_ACTIVE_NULL) {
            val rect = _stickers[_itemActive.value].rect
            rectDelete.value.set(
                rect.left - RECT_ITEM_EDIT_SIZE,
                rect.top - RECT_ITEM_EDIT_SIZE,
                rect.left + RECT_ITEM_EDIT_SIZE,
                rect.top + RECT_ITEM_EDIT_SIZE
            )
            rectCopy.value.set(
                rect.right - RECT_ITEM_EDIT_SIZE,
                rect.top - RECT_ITEM_EDIT_SIZE,
                rect.right + RECT_ITEM_EDIT_SIZE,
                rect.top + RECT_ITEM_EDIT_SIZE
            )
            rectFlip.value.set(
                rect.left - RECT_ITEM_EDIT_SIZE,
                rect.bottom - RECT_ITEM_EDIT_SIZE,
                rect.left + RECT_ITEM_EDIT_SIZE,
                rect.bottom + RECT_ITEM_EDIT_SIZE
            )
            rectScale.value.set(
                rect.right - RECT_ITEM_EDIT_SIZE,
                rect.bottom - RECT_ITEM_EDIT_SIZE,
                rect.right + RECT_ITEM_EDIT_SIZE,
                rect.bottom + RECT_ITEM_EDIT_SIZE
            )
        }
    }

    private fun initEditorSize(image: Bitmap) {
        val bitmapWidth = image.width
        val bitmapHeight = image.height
        val scale: Float = if (bitmapHeight > bitmapWidth) {
            kotlin.math.min(
                deviceWidth.toFloat() / bitmapWidth,
                deviceHeight.toFloat() / bitmapHeight
            )
        } else {
            deviceWidth.toFloat() / bitmapWidth.toFloat()
        }
        _editorWidth.value = (bitmapWidth * scale).toInt()
        _editorHeight.value = (bitmapHeight * scale).toInt()
    }

    private val matrixScale = Matrix()

    internal fun scaleItemActive(x: Float, y: Float) {
        val rectActive = _stickers[_itemActive.value].rect
        val oldDis = getDistance(
            rectActive.centerX(), rectActive.centerY(), rectActive.right, rectActive.bottom
        )
        val newDis = getDistance(rectActive.centerX(), rectActive.centerY(), x, y)
        val scaleF: Float = newDis / oldDis
        matrixScale.setScale(
            scaleF, scaleF, rectActive.centerX(), rectActive.centerY()
        )
        matrixScale.mapRect(rectActive)
        val angle = getAngle(
            PointF(rectActive.right, rectActive.bottom),
            PointF(x, y),
            PointF(rectActive.centerX(), rectActive.centerY())
        ).toFloat()
        val a = _stickers.toMutableList().apply {
            set(_itemActive.value, this[_itemActive.value].copy(rect = rectActive, angle = angle))
        }
        _stickers.clear()
        _stickers.addAll(a)
    }

    internal fun tranformItemActive(scaleFactor: Float, rotation: Float, offset: Offset) {
        val rectActive = _stickers[_itemActive.value].rect
        val angle = _stickers[_itemActive.value].angle + rotation
        rectActive.move(offset)
        matrixScale.setScale(
            scaleFactor, scaleFactor, rectActive.centerX(), rectActive.centerY()
        )
        matrixScale.mapRect(rectActive)
        val temp = _stickers.toMutableList().apply {
            set(_itemActive.value, this[_itemActive.value].copy(rect = rectActive, angle = angle))
        }
        _stickers.clear()
        _stickers.addAll(temp)
        updateStickerEx()
    }

    private val _removeObjectToolActive = MutableStateFlow(BRUSH_MODE)
    val removeObjectToolActive: StateFlow<Int> = _removeObjectToolActive

    internal fun setRemoveObjectToolActive(index: Int) {
        _removeObjectToolActive.value = index
    }

    //AI Remove
    private val selfieOptions =
        SelfieSegmenterOptions.Builder().setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
            .build()

    private val localModel =
        LocalModel.Builder().setAssetFilePath(LOCAL_MODEL_FILE_PATH).build()

    private val segmenter = Segmentation.getClient(selfieOptions)
    private val customObjectDetectorOptions = CustomObjectDetectorOptions.Builder(localModel)
        .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE).enableMultipleObjects()
        .enableClassification().setClassificationConfidenceThreshold(0.5f)
        .setMaxPerObjectLabelCount(1).build()
    private val objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)

    private val dptoPx = application.resources.displayMetrics.density
    private fun getMaskBitmap(path: Path?, obj: AITarget? = null, mode: Int): Bitmap {
        val result =
            Bitmap.createBitmap(_editorWidth.value, _editorHeight.value, Bitmap.Config.ARGB_8888)
                .asImageBitmap()
        val canvas = Canvas(result)
        canvas.drawRect(
            Rect(0f, 0f, result.width.toFloat(), result.height.toFloat()),
            Paint().apply {
                color = Color.Black
                style = PaintingStyle.Fill
            })
        val paint = Paint().apply {
            style = if (mode == BRUSH_MODE) PaintingStyle.Stroke else PaintingStyle.Fill
            color = Color.White
            strokeWidth = if (mode == BRUSH_MODE) 20 * dptoPx else 5 * dptoPx
        }
        path?.let {
            canvas.drawPath(path, paint)
        }

        if (obj != null) {
            canvas.drawImage(obj.mask, obj.box.offset(), Paint())
        }
        val bmp = result.asAndroidBitmap()
        val scale = Bitmap.createScaledBitmap(
            bmp,
            _imageActions[_currentBitmapIndex.value].image.width,
            _imageActions[_currentBitmapIndex.value].image.height,
            true
        )
        bmp.recycle()
        return scale
    }

    private val _stateScreen = MutableStateFlow(INDILE)
    val stateScreen: StateFlow<Boolean> = _stateScreen

    fun removeObject(
        path: Path? = null,
        obj: AITarget? = null,
        mode: Int = BRUSH_MODE,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _stateScreen.value = LOADING
            obj?.let { objSelected ->
                _selectedObj.update {
                    objSelected
                }
            }
            val image = _imageActions[_currentBitmapIndex.value].image.asAndroidBitmap()
            val mask = getMaskBitmap(path, obj, mode)
            repository.cleanupBitmap(image, mask, object : DataSource.EraserObjectCallback {
                override fun onLocalComplete(result: ImageBitmap) {
                    if (_currentBitmapIndex.value < _imageActions.lastIndex) {
                        _imageActions.removeRange(
                            _currentBitmapIndex.value + 1,
                            _imageActions.lastIndex + 1
                        )
                    }
                    _imageActions.add(
                        ImageAction(
                            result,
                            _imageActions[_currentBitmapIndex.value].AIObj.toMutableList().apply {
                                obj?.let {
                                    remove(it)
                                }
                            })
                    )
                    obj?.let { objSelected ->
                        _selectedObj.update {
                            null
                        }
                    }
                    _currentBitmapIndex.value = _imageActions.lastIndex
                    _stateScreen.value = INDILE
                    viewModelScope.launch(Dispatchers.Main) {
                        onComplete()
                    }
                }

                override fun onCloudComplete(result: ImageBitmap) {
                    if (_currentBitmapIndex.value < _imageActions.lastIndex) {
                        _imageActions.removeRange(
                            _currentBitmapIndex.value + 1,
                            _imageActions.lastIndex + 1
                        )
                    }
                    _imageActions.add(
                        ImageAction(
                            result,
                            _imageActions[_currentBitmapIndex.value].AIObj.toMutableList().apply {
                                obj?.let {
                                    remove(it)
                                }
                            })
                    )
                    obj?.let {
                        _selectedObj.update {
                            null
                        }
                    }
                    _currentBitmapIndex.value = _imageActions.lastIndex
                    _stateScreen.value = INDILE
                    viewModelScope.launch(Dispatchers.Main) {
                        onComplete()
                    }
                }

                override fun onFailed(error: String) {
                    _stateScreen.value = INDILE
                    _notification.update {
                        application.getString(R.string.err_template) + error
                    }
                    viewModelScope.launch(Dispatchers.Main) {
                        onComplete()
                    }
                }

            })
        }
    }

    private val _editorWidth = MutableStateFlow(0)
    val editorWidth: StateFlow<Int> = _editorWidth
    private val _editorHeight = MutableStateFlow(0)
    val editorHeight: StateFlow<Int> = _editorHeight

    internal fun undo() {
        if (currentBitmapIndex.value >= 1) {
            _currentBitmapIndex.value -= 1
        }
    }

    internal fun redo() {
        if (currentBitmapIndex.value < _imageActions.lastIndex) {
            _currentBitmapIndex.value += 1
        }
    }

    private val _assetsStickers = mutableStateListOf<AssetItem>()
    val assetsStickers: List<AssetItem> = _assetsStickers

    internal fun initAssetData(folderName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val assetManager = getApplication<Application>().assets
            val fileList = assetManager.list(folderName) ?: return@launch
            val list = mutableListOf<AssetItem>()
            for (fileName in fileList) {
                yield()
                list.add(AssetItem(fileName, "$ASSET_PATH$folderName/$fileName"))
            }
            when (folderName) {
                STORAGE_STICKERS -> {
                    _assetsStickers.addAll(list)
                }
            }
        }
    }

    fun addSticker(asset: AssetItem) {
        viewModelScope.launch(Dispatchers.IO) {
            _stateScreen.value = LOADING
            repository.getSticker(
                changeFileEx(asset.name),
                STORAGE_STICKERS,
                object : DataSource.StickerLoadCallBack {
                    override fun onLocalLoad(bitmap: Bitmap) {
                        val left = _editorWidth.value / 2f - bitmap.width / 2
                        val top = _editorHeight.value / 2f - bitmap.height / 2
                        val right = left + bitmap.width
                        val bottom = top + bitmap.height
                        addSticker(
                            Sticker(
                                RectF(left, top, right, bottom),
                                bitmap.asImageBitmap()
                            )
                        )
                        _stateScreen.value = INDILE
                    }

                    override fun onFireBaseLoad(bitmap: Bitmap) {
                        val left = _editorWidth.value / 2f - bitmap.width / 2
                        val top = _editorHeight.value / 2f - bitmap.height / 2
                        val right = left + bitmap.width
                        val bottom = top + bitmap.height
                        _stickers.add(
                            Sticker(
                                RectF(left, top, right, bottom),
                                bitmap.asImageBitmap()
                            )
                        )
                        _stateScreen.value = INDILE
                    }

                    override fun onLoadFailed(e: Exception) {
                        _notification.value = e.message.toString()
                        _stateScreen.value = INDILE
                    }

                })
        }

    }

    fun addTimeStamp(timeStamp: TimeStamp) {
        viewModelScope.launch(Dispatchers.IO) {
            val left = _editorWidth.value / 2f - timeStamp.bitmap.width / 2
            val top = _editorHeight.value / 2f - timeStamp.bitmap.height / 2
            val right = left + timeStamp.bitmap.width
            val bottom = top + timeStamp.bitmap.height
            _stickers.add(timeStamp.copy(RectF(left, top, right, bottom)))
            setStickerActive(_stickers.lastIndex)
            updateStickerEx()
        }
    }

    fun setFilter(asset: FilterObj) {
        viewModelScope.launch(Dispatchers.IO) {
            _stateScreen.value = LOADING
            _brightness.update {
                asset.brightness
            }
            _contrast.update {
                asset.contrast
            }
            _warmth.update {
                asset.warmth
            }
            _saturation.update {
                asset.saturation
            }
            updateImageMatrix()
            _stateScreen.value = INDILE
        }

    }

    private fun changeFileEx(oldName: String): String {
        return oldName.replace(OLD_EXTENSION, NEW_EXTENSION)
    }

    private val _fonts = mutableStateListOf<Font>()
    val fonts: List<Font> = _fonts
    fun initFonts() {
        viewModelScope.launch(Dispatchers.IO) {
            val assetManager = getApplication<Application>().assets
            val fileList = assetManager.list(STORAGE_FONTS) ?: return@launch
            val list = mutableListOf<Font>()
            for (fileName in fileList) {
                yield()
                list.add(Font("$STORAGE_FONTS/$fileName"))
            }
            _fonts.addAll(list)
        }
    }

    private val _brightness = MutableStateFlow(0f)
    val brightness: StateFlow<Float> = _brightness

    fun setBrightness(process: Float) {
        _brightness.value = process
    }

    private val _saturation = MutableStateFlow(1f)
    val saturation: StateFlow<Float> = _saturation
    fun setSaturation(process: Float) {
        _saturation.value = process
    }

    private val _contrast = MutableStateFlow(1f)
    val contrast: StateFlow<Float> = _contrast
    fun setContrast(process: Float) {
        _contrast.value = process
    }

    private val _warmth = MutableStateFlow(1f)
    val warmth: StateFlow<Float> = _warmth

    fun setWarmth(temp: Float) {
        _warmth.value = temp
    }

    fun addText(text: String, fontName: String?, textColor: Color) {
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = createImageBitmapFromText(application, text, fontName, textColor)
            val left = _editorWidth.value / 2f - bitmap.width / 2
            val top = _editorHeight.value / 2f - bitmap.height / 2
            val right = left + bitmap.width
            val bottom = top + bitmap.height
            addSticker(
                Sticker(
                    rectF =  RectF(left, top, right, bottom),
                    bitmap.asImageBitmap()
                )
            )
        }
    }

    private val _imageMatrix = MutableStateFlow(ImageMatrix())
    val imageMatrix: StateFlow<ImageMatrix> = _imageMatrix

    fun updateImageMatrix() {
        _imageMatrix.value.apply {
            mBrightness = brightness.value
            mContrast = contrast.value
            mSaturation = saturation.value
            mWarmth = warmth.value
            updateMatrix()
//            Log.d("MATRIX", "BRIGHTNESS: $mBrightness")
//            Log.d("MATRIX", "CONTRAST: $mContrast")
//            Log.d("MATRIX", "SATURATION: $mSaturation")
//            Log.d("MATRIX", "WARMTH: $mWarmth")
        }

    }

    fun saveImage(onSuccess: (Uri) -> Unit, onFailed: () -> Unit) {
        viewModelScope.launch {
            _stateScreen.value = LOADING
            val image = imageBitmaps[_currentBitmapIndex.value].image.asAndroidBitmap()
            val result = ImageBitmap(image.width, image.height, ImageBitmapConfig.Argb8888)
            val canvas = Canvas(result)
            canvas.drawImage(image = image.asImageBitmap(), Offset.Zero, Paint().apply {
                this.colorFilter =
                    ColorFilter.colorMatrix(ColorMatrix(_imageMatrix.value.mColorMatrix.array))
            })
            val stickerPaint = Paint()
            val scaleImageoverView = image.width / _editorWidth.value.toFloat()
            for (sticker in _stickers) {
                canvas.save()
                canvas.rotate(
                    sticker.angle,
                    sticker.rect.centerX() * scaleImageoverView,
                    sticker.rect.centerY() * scaleImageoverView
                )
                canvas.drawImageRect(
                    sticker.bitmap,
                    dstOffset = (sticker.rect.offset() * scaleImageoverView).round(),
                    dstSize = (sticker.rect.getSize() * scaleImageoverView).toIntSize(),
                    paint = stickerPaint
                )
                canvas.restore()
            }
            _frame.value?.let {
                canvas.drawImageRect(
                    image = it,
                    dstSize = IntSize(image.width, image.height),
                    dstOffset = IntOffset.Zero,
                    paint = Paint()
                )
            }
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val current = LocalDateTime.now().format(formatter)

            val name = "$APP_NAME$current.jpeg"
            val imageCollection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val content = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, image.width)
                put(MediaStore.Images.Media.HEIGHT, image.height)
            }
            try {
                application.contentResolver.insert(imageCollection, content)?.also {
                    application.contentResolver.openOutputStream(it).use { os ->
                        if (!result.asAndroidBitmap()
                                .compress(Bitmap.CompressFormat.JPEG, 100, os)
                        ) {
                            throw IOException("Failed to save BMP")
                        } else {
                            viewModelScope.launch(Dispatchers.Main) {
                                onSuccess(it)
                            }
                        }
                    }
                } ?: throw IOException("Failed to Create Media Entry")
                _stateScreen.value = INDILE

            } catch (
                e: IOException
            ) {
                e.printStackTrace()
                _stateScreen.value = INDILE
                viewModelScope.launch(Dispatchers.Main) {
                    onFailed()
                }
            }
        }
    }

    fun rsNoti() {
        _notification.value = ""
    }

    private val _frames = mutableStateListOf<List<AssetItem>>()
    val frames: List<List<AssetItem>> = _frames

    private val _frameContainerActive = MutableStateFlow(-1)
    val frameContainerActive: StateFlow<Int> = _frameContainerActive
    fun initFrames() {
        viewModelScope.launch(Dispatchers.IO) {
            val assetManager = getApplication<Application>().assets
            val fileList = assetManager.list(FRAME_FOLDER) ?: return@launch
            val list = mutableListOf<MutableList<AssetItem>>()
            for (i in fileList.indices) {
                list.add(mutableListOf())
                val f = assetManager.list(FRAME_FOLDER + "/" + fileList[i])
                f?.let {
                    for (sticker in it) {
                        list[i].add(
                            AssetItem(
                                sticker,
                                "$ASSET_PATH$FRAME_FOLDER/${fileList[i]}/$sticker"
                            )
                        )
                    }
                }
            }
            _frames.addAll(list)
            setFrameContainerActive(0)
        }
    }

    fun setFrameContainerActive(index: Int) {
        _frameContainerActive.update {
            index
        }
    }

    private val _frame = MutableStateFlow<ImageBitmap?>(null)
    val frame: StateFlow<ImageBitmap?> = _frame
    fun setFrame(asset: AssetItem?) {
        viewModelScope.launch(Dispatchers.IO) {
            _stateScreen.value = LOADING
            if (asset != null) {
                repository.getSticker(
                    changeFileEx(asset.name),
                    STORAGE_FRAMES,
                    object : DataSource.StickerLoadCallBack {
                        override fun onLocalLoad(bitmap: Bitmap) {
                            _frame.update {
                                bitmap.asImageBitmap()
                            }
                            _stateScreen.value = INDILE
                        }

                        override fun onFireBaseLoad(bitmap: Bitmap) {
                            _frame.update {
                                bitmap.asImageBitmap()
                            }
                            _stateScreen.value = INDILE
                        }

                        override fun onLoadFailed(e: Exception) {
                            _stateScreen.value = INDILE
                            //TODO Handle err
                        }

                    })
            } else {
                _frame.update {
                    null
                }
                _stateScreen.value = INDILE
            }
        }

    }

    private val _timeStamps = mutableStateListOf<TimeStamp>()
    val timeStamps: List<TimeStamp> = _timeStamps

    fun initTimeStamps() {
        viewModelScope.launch(Dispatchers.IO) {
            val asset = application.assets
            _timeStamps.clear()
            _timeStamps.addAll(
                listOf(
                    TimeStamp(application, paintSecondLine = android.graphics.Paint().apply {
                        textSize = 100f
                        typeface = Typeface.createFromAsset(asset, "fonts/DripOctober-vm0JA.ttf")
                        color = Color.White.toArgb()
                    }, paintFirstLine = android.graphics.Paint().apply {
                        textSize = 230f
                        typeface = Typeface.createFromAsset(asset, "fonts/Choret.ttf")
                        color = Color.White.toArgb()
                    }, padding = 28f),
                    TimeStamp(
                        application,
                        paintSecondLine = android.graphics.Paint().apply {
                            textSize = 130f
                            typeface = Typeface.createFromAsset(asset, "fonts/Chanrest.ttf")
                            color = Color.White.toArgb()
                        },
                        paintFirstLine = android.graphics.Paint().apply {
                            textSize = 250f
                            typeface =
                                Typeface.createFromAsset(asset, "fonts/AlexanderLettering-5MEj.ttf")
                            color = Color.White.toArgb()
                        },
                        padding = 28f,
                        formatterFirstLine = DateTimeFormatter.ofPattern("EEEE"),
                        lineSpacing = 40f
                    ), TimeStamp(
                        application,
                        formatterSecondLine = DateTimeFormatter.ofPattern("EE"),
                        paintSecondLine = android.graphics.Paint(),
                        paintFirstLine = android.graphics.Paint().apply {
                            textSize = 130f
                            typeface = Typeface.createFromAsset(asset, "fonts/Butterfly.ttf")
                            color = Color.White.toArgb()
                            this.letterSpacing = 0.06f
                        },
                        padding = 28f,
                        formatterFirstLine = DateTimeFormatter.ofPattern("yyyy.MM.dd"),
                        lineSpacing = 40f,
                        singleLine = true
                    ),
                    TimeStamp(
                        application,
                        paintSecondLine = android.graphics.Paint().apply {
                            textSize = 220f
                            typeface = Typeface.createFromAsset(asset, "fonts/Digital Dismay.otf")
                            color = Color.White.toArgb()
                        },
                        paintFirstLine = android.graphics.Paint().apply {
                            textSize = 250f
                            typeface =
                                Typeface.createFromAsset(asset, "fonts/AlexanderLettering-5MEj.ttf")
                            color = Color.White.toArgb()
                        },
                        padding = 28f,
                        formatterFirstLine = DateTimeFormatter.ofPattern("EEEE"),
                        formatterSecondLine = DateTimeFormatter.ofPattern("HH:mm"),
                        lineSpacing = 30f
                    ),
                    TimeStamp(
                        application,
                        paintSecondLine = android.graphics.Paint(),
                        paintFirstLine = android.graphics.Paint().apply {
                            textSize = 500f
                            typeface = Typeface.createFromAsset(asset, "fonts/Orloj.ttf")
                            color = Color.White.toArgb()
                        },
                        padding = 28f,
                        formatterFirstLine = DateTimeFormatter.ofPattern("HH:mm"),
                        lineSpacing = 30f,
                        singleLine = true
                    ),
                    TimeStamp(
                        application,
                        paintSecondLine = android.graphics.Paint().apply {
                            textSize = 180f
                            typeface =
                                Typeface.createFromAsset(asset, "fonts/DripOctober-vm0JA.ttf")
                            color = Color.White.toArgb()
                        },
                        paintFirstLine = android.graphics.Paint().apply {
                            textSize = 200f
                            typeface = Typeface.createFromAsset(asset, "fonts/LmsAIsForAngel.ttf")
                            color = Color.White.toArgb()
                        },
                        padding = 28f,
                        formatterFirstLine = DateTimeFormatter.ofPattern("EEEE"),
                        formatterSecondLine = DateTimeFormatter.ofPattern("yyyy.MM.dd"),
                        lineSpacing = 30f
                    )
                )
            )
        }
    }
    fun setTimeTimeStamp(time: LocalDateTime= LocalDateTime.now()){
        viewModelScope.launch {
            val a = _stickers.toMutableList().apply {
                val sticker = this[_itemActive.value]
                if (sticker is TimeStamp) {
                    sticker.updateDateTime(time)
                    _stickers[itemActive.value] = sticker
                }
//            this[index].rect.move(moveX, moveY)
            }
            _stickers.clear()
            _stickers.addAll(a)
        }
    }

    fun updateDate(date:LocalDate){
        viewModelScope.launch {
            val a = _stickers.toMutableList().apply {
                val sticker = this[_itemActive.value]
                if (sticker is TimeStamp) {
                    sticker.updateDate(date)
                    _stickers[itemActive.value] = sticker
                }
//            this[index].rect.move(moveX, moveY)
            }
            _stickers.clear()
            _stickers.addAll(a)
        }
    }

    fun updateTime(hours: Int, minutes: Int){
        viewModelScope.launch {
            val a = _stickers.toMutableList().apply {
                val sticker = this[_itemActive.value]
                if (sticker is TimeStamp) {
                    sticker.updateTime(hours,minutes)
                    _stickers[itemActive.value] = sticker
                }
            }
            _stickers.clear()
            _stickers.addAll(a)
        }
    }

    private val _timeStampInActive = MutableStateFlow(false)
    val timeStampInActive : StateFlow<Boolean> = _timeStampInActive
}
