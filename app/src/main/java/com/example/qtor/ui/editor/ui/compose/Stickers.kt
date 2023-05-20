package com.example.qtor.ui.editor

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.qtor.data.model.AssetItem

@Composable
fun StickersTool(viewModel: EditorViewModel) {
    val stickers = viewModel.assetsStickers
    LazyRow(
        modifier = Modifier.height(60.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(items = stickers) {
            StickerCompose(item = it) {
                viewModel.addSticker(it)
            }
        }
    }
}

@Composable
fun StickerCompose(item: AssetItem, onClick: () -> Unit) {
    AsyncImage(
        modifier = Modifier
            .width(50.dp)
            .height(50.dp)
            .clickable {
                onClick()
            }
            .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(5.dp)),
        model = Uri.parse(item.url),
        contentDescription = null
    )
}