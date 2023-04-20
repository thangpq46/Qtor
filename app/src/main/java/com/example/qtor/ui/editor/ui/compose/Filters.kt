package com.example.qtor.ui.editor

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.qtor.R


@Composable
fun FiltersTool(viewModel: EditorViewModel){
    val filters = viewModel.assetsFilters
    LazyRow(contentPadding = PaddingValues(5.dp)){
        items(items = filters) {
            Filter(item = it){
                viewModel.setFilter(it)
            }
        }
    }
}

@Composable
fun Filter(item:com.example.qtor.data.model.Filter,onClick: ()->Unit){
    Surface(modifier = Modifier.padding(horizontal = 5.dp), shape = RoundedCornerShape(5.dp)) {
        Image(painter = painterResource(id = R.drawable.demo2), contentDescription = null, modifier = Modifier
            .width(60.dp)
            .height(60.dp), contentScale = ContentScale.Crop)
        AsyncImage(
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
                .alpha(.3f)
                .clickable {
                    onClick()
                }, model = Uri.parse(item.url), contentDescription = null, contentScale = ContentScale.Crop
        )
    }
}