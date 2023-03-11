package com.example.qtor.ui.editor

import android.app.Application
import android.graphics.*
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewModelScope
import com.example.qtor.R
import com.example.qtor.constant.ITEM_ACTIVE_NULL
import com.example.qtor.constant.RECT_ITEM_EDIT_SIZE
import com.example.qtor.constant.ZERO
import com.example.qtor.data.model.Sticker
import com.example.qtor.data.repository.DataSource
import com.example.qtor.data.repository.ImageRepository
import com.example.qtor.ui.base.BaseViewModel
import com.example.qtor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditorViewModel(private val application: Application) : BaseViewModel(application) {
    private val repository = ImageRepository()
    private val _bitmap = MutableStateFlow(ImageBitmap(10, 10, ImageBitmapConfig.Alpha8))
    val bitmap: StateFlow<ImageBitmap> get() = _bitmap
    private val _stickers = mutableStateListOf<Sticker>()
    val stickers: List<Sticker> = _stickers
    private val _rectDraw = RectF(ZERO, ZERO, ZERO, ZERO)

    private val _imageOffset = MutableStateFlow(IntOffset.Zero)
    val imageOffset : StateFlow<IntOffset> = _imageOffset

    private val _imageSize = MutableStateFlow(IntSize.Zero)
    val imageSize : StateFlow<IntSize> = _imageSize

    private val deviceWidth = application.resources.displayMetrics.widthPixels

    private val _rectDelete = MutableStateFlow(RectF(ZERO, ZERO, ZERO, ZERO))
    val rectDelete: StateFlow<RectF> get() = _rectDelete

    private val _rectFlip = MutableStateFlow(RectF(ZERO, ZERO, ZERO, ZERO))
    val rectFlip: StateFlow<RectF> get() = _rectFlip

    private val _rectCopy = MutableStateFlow(RectF(ZERO, ZERO, ZERO, ZERO))
    val rectCopy: StateFlow<RectF> get() = _rectCopy

    private val _rectScale = MutableStateFlow(RectF(ZERO, ZERO, ZERO, ZERO))
    val rectScale: StateFlow<RectF> get() = _rectScale

    internal fun initBitmaps(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _bitmap.emit(
                BitmapFactory.decodeStream(
                    getApplication<Application>().contentResolver.openInputStream(uri)
                ).asImageBitmap()
            )
        }
    }

    internal fun addSticker(sticker: Sticker) {
        val rect = RectF(sticker.rect)
        rect.move(20f,20f)
        _stickers.add(sticker.copy(rect = rect))
        setItemActive(_stickers.lastIndex)
        updateStickerEx()
    }

    fun moveSticker(index: Int,moveX:Float,moveY:Float){
        val a= _stickers.toMutableList().apply {
            this[index].rect.move(moveX,moveY)
        }
        _stickers.clear()
        _stickers.addAll(a)
    }

    internal fun moveImage(moveOffset: Offset) {
        viewModelScope.launch(Dispatchers.IO) {
            _rectDraw.move(moveOffset)
            _imageSize.value = _rectDraw.getIntSize()
            _imageOffset.value=_rectDraw.getIntOffset()
        }
    }

    private var _itemActive = MutableStateFlow(ITEM_ACTIVE_NULL)
    val itemActive: StateFlow<Int> = _itemActive
    internal fun setItemActive(index: Int) {
        _itemActive.value = index
    }

    internal fun removeSticker(){
        _stickers.removeAt(_itemActive.value)
        setItemActive(ITEM_ACTIVE_NULL)
    }

    private val _toolActive = MutableStateFlow(-1)
    val toolActive: StateFlow<Int> = _toolActive
    internal fun setToolActive(index: Int) {
        _toolActive.value = index
    }

    internal fun initStickerS() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getStickers(application, object : DataSource.StickersCallback {
                override fun onLocalLoaded(stickers: MutableList<Sticker>) {
                    _stickers.clear()
                    _stickers.addAll(stickers)
                }

                override fun onFireBaseLoaded(stickerFB: Sticker) {
                    if (!_stickers.contains(stickerFB)) {
                        addSticker(stickerFB)
                    }
                }

                override fun onDataNotAvailable() {

                }

            })
        }
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
        if (_itemActive.value!= ITEM_ACTIVE_NULL){
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

    private var viewHeight =0
    internal fun setViewHeight(index: Int){
        viewHeight=index
        if (viewHeight>0){
            initRectDraw()
        }
    }

    private fun initRectDraw(){
        val bitmapWidth = _bitmap.value.width
        val bitmapHeight = _bitmap.value.height
        if (bitmapHeight>bitmapWidth){
            val scale = kotlin.math.min(deviceWidth.toFloat()/bitmapWidth,viewHeight.toFloat()/bitmapHeight)
            val top =(viewHeight-(bitmapHeight*scale))/2f
            val left = (deviceWidth-(bitmapWidth*scale))/2f
            val right = left+bitmapWidth*scale
            val bot = top + bitmapHeight*scale
            _rectDraw.set(left,top,right,bot)
            updateOffsetAndSize()
        }else{
            val scale = deviceWidth/bitmapWidth
        }
    }
    internal fun scaleImage(scaleF:Float){
        if (scaleF<=1.1f && scaleF>0.9f){
            _rectDraw.scale(scaleF)
            updateOffsetAndSize()
        }
    }

    private fun updateOffsetAndSize(){
        _imageSize.value =_rectDraw.getIntSize()
        _imageOffset.value = _rectDraw.getIntOffset()
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
        val a= _stickers.toMutableList().apply {
            set(_itemActive.value,this[_itemActive.value].copy(rect = rectActive, angle = angle))
        }
        _stickers.clear()
        _stickers.addAll(a)
    }
}