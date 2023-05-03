package com.example.qtor.ui.editor

import android.app.Application
import android.content.ContentValues
import android.graphics.*
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.round
import androidx.lifecycle.viewModelScope
import com.example.qtor.constant.*
import com.example.qtor.data.model.*
import com.example.qtor.data.repository.DataSource
import com.example.qtor.ui.base.BaseViewModel
import com.example.qtor.util.*
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
import java.io.IOException
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
        _drawX.value += x
        _drawY.value += y
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

            val bitmap = BitmapFactory.decodeStream(
                getApplication<Application>().contentResolver.openInputStream(uri)
            )
            _imageActions.add(ImageAction(bitmap.asImageBitmap(), mutableListOf()))
            initEditorSize(bitmap)
            val scaleBitmap =
                Bitmap.createScaledBitmap(bitmap, _editorWidth.value, _editorHeight.value, true)
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
                        val maskPeopleObj = Bitmap.createBitmap(
                            maskAI,
                            obj.boundingBox.left,
                            obj.boundingBox.top,
                            obj.boundingBox.width(),
                            obj.boundingBox.height()
                        )

                        val maskOthersObj = Bitmap.createBitmap(
                            obj.boundingBox.width(),
                            obj.boundingBox.height(),
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = android.graphics.Canvas(maskOthersObj)
                        canvas.drawColor(android.graphics.Color.WHITE)
                        val origin = Bitmap.createBitmap(
                            scaleBitmap,
                            obj.boundingBox.left,
                            obj.boundingBox.top,
                            obj.boundingBox.width(),
                            obj.boundingBox.height()
                        )
                        Log.d("AAA", obj.labels.toString())
                        if (obj.labels.size > 0 && obj.labels[0].text == LABEL_PERSON) {
                            list.add(
                                AITarget(
                                    obj.boundingBox,
                                    maskPeopleObj.asImageBitmap(),
                                    origin.asImageBitmap(),
                                    TYPE_PEOPLE
                                )
                            )
                        } else {
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
    }

    internal fun addSticker(sticker: Sticker) {
        val rect = RectF(sticker.rect)
        rect.move(dpToPx(application, 15), dpToPx(application, 15))
        _stickers.add(sticker.copy(rect = rect))
        setStickerActive(_stickers.lastIndex)
        updateStickerEx()
    }

    fun moveSticker(index: Int, moveX: Float, moveY: Float) {
        val a = _stickers.toMutableList().apply {
            this[index].rect.move(moveX, moveY)
        }
        _stickers.clear()
        _stickers.addAll(a)
    }

    private var _itemActive = MutableStateFlow(ITEM_ACTIVE_NULL)
    val itemActive: StateFlow<Int> = _itemActive
    internal fun setStickerActive(index: Int) {
        _itemActive.value = index
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

    private val _removeObjectToolActive = MutableStateFlow(BRUSH_MODE)
    val removeObjectToolActive: StateFlow<Int> = _removeObjectToolActive
//    private val _currentRemoveActionIndex = MutableStateFlow(-1)
//    val currentRemoveActionIndex: StateFlow<Int> = _currentRemoveActionIndex

//    fun addRemoveObjectAction() {
//        _removeObjectActions.add(
//            RemoveObjectAction(
//                _aiObjects.toList()
//            )
//        )
//        _currentRemoveActionIndex.value = _currentRemoveActionIndex.value + 1
//    }

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
                    _currentBitmapIndex.value = _imageActions.lastIndex
                    _stateScreen.value = INDILE
                    viewModelScope.launch(Dispatchers.Main) {
                        onComplete()
                    }
                }

                override fun onFailed(error: String) {
                    _stateScreen.value = INDILE
                    Log.d("AAAsadas", error)
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
                    RectF(left, top, right, bottom),
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
            Log.d("MATRIX", "BRIGHTNESS: $mBrightness")
            Log.d("MATRIX", "CONTRAST: $mContrast")
            Log.d("MATRIX", "SATURATION: $mSaturation")
            Log.d("MATRIX", "WARMTH: $mWarmth")
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
}
