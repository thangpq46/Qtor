package com.example.qtor.ui.editor.ui.compose

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.qtor.data.model.TimeStamp
import com.example.qtor.ui.editor.EditorViewModel

@Composable
fun TimeStampCompose(viewModel: EditorViewModel){
    LazyRow(modifier = Modifier.height(60.dp).padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically){
        items(items = viewModel.timeStamps){
            TimeStamp(item = it, onClick = {
                viewModel.addSticker(it)
            })
        }
    }
}

@Composable
fun TimeStamp(item: TimeStamp,onClick:()->Unit) {
    Image(modifier = Modifier.clickable { onClick() }.width(60.dp).height(60.dp),bitmap = item.bitmap, contentDescription =null )
}