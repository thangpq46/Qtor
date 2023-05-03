package com.example.qtor.ui.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.qtor.R
import com.example.qtor.constant.Filters
import com.example.qtor.data.model.FilterObj
import kotlin.reflect.KFunction1


@Composable
fun FiltersTool(viewModel: EditorViewModel) {
    LazyRow(contentPadding = PaddingValues(5.dp)) {
        items(items = Filters) {
            Filter(item = it, onClick = viewModel::setFilter)
        }
    }
}

@Composable
fun Filter(item: FilterObj, onClick: KFunction1<FilterObj, Unit>) {
    val imageMatrix = ImageMatrix().apply {
        mWarmth = item.warmth
        mBrightness = item.brightness
        mContrast = item.contrast
        mSaturation = item.saturation
        updateMatrix()
    }
    Surface(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .clickable { onClick(item) }, shape = RoundedCornerShape(5.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.demo2),
                contentDescription = null,
                modifier = Modifier
                    .width(70.dp)
                    .height(70.dp),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix(imageMatrix.mColorMatrix.array)
                )
            )
            Text(text = stringResource(id = item.nameID), fontStyle = FontStyle.Italic)
        }

    }
}