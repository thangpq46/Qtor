package com.example.qtor.ui.editor

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@Composable
fun FiltersTool(viewModel: EditorViewModel){
    val filters = viewModel.assetsFilters
    LazyRow{
        items(items = filters) {
            Filter(item = it){
                viewModel.setFilter(it)
            }
        }
    }
}

@Composable
fun Filter(item:com.example.qtor.data.model.Filter,onClick: ()->Unit){
    AsyncImage(
        modifier = Modifier
            .width(50.dp)
            .height(50.dp)
            .clickable {
                onClick()
            }, model = Uri.parse(item.url), contentDescription = null
    )
}