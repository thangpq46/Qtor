package com.example.qtor.data.model

import android.graphics.Rect
import androidx.compose.ui.graphics.ImageBitmap

data class AITarget(
    val box: Rect,
    val mask: ImageBitmap,
    val origin: ImageBitmap,
    val type: Int,
    var isSelected: Boolean = false
)
