package com.example.qtor.ui.editor.ui.compose

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.qtor.constant.FRAME_TITLES
import com.example.qtor.data.model.AssetItem
import com.example.qtor.ui.editor.EditorViewModel
import kotlin.reflect.KFunction1

@Composable
fun FrameTool(viewModel: EditorViewModel) {
    val frames = viewModel.frames
    val containerActive by viewModel.frameContainerActive.collectAsState()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.4f),
        color = MaterialTheme.colorScheme.onBackground
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.setMainToolActive(-1)
                        viewModel.setFrame(null)

                    }, shape = CircleShape, colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ), elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        hoveredElevation = 0.dp
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
                Button(
                    onClick = { viewModel.setMainToolActive(-1) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        hoveredElevation = 0.dp
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            }
            LazyRow {
                itemsIndexed(items = FRAME_TITLES) { index, it ->
                    TextButton(onClick = { viewModel.setFrameContainerActive(index) }) {
                        Text(
                            text = stringResource(id = it),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 100.dp)) {
                if (containerActive in frames.indices) {
                    items(items = frames[containerActive]) {
                        TextButton(onClick = { /*TODO*/ }) {
                            Frame(it, viewModel::setFrame)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Frame(item: AssetItem, onClick: KFunction1<AssetItem, Unit>) {
    AsyncImage(alignment = Alignment.Center,
        modifier = Modifier
            .width(100.dp)
            .height(130.dp)
            .clickable {
                onClick(item)
            }
            .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(5.dp)),
        model = Uri.parse(item.url),
        contentDescription = null
    )
}