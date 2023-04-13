package com.example.qtor.ui.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qtor.constant.*
import com.example.qtor.data.model.AITarget
import com.example.qtor.data.model.BottomMenuItem


@Composable
fun BottomNavigationTool(modifier: Modifier = Modifier, viewModel: EditorViewModel) {
    val mainToolActive by viewModel.mainToolActive.collectAsState()
    Column {
        when (mainToolActive) {
            EDIT_IMAGE_TOOl -> {
                RemoveObjectTool(viewModel = viewModel)
            }
            STICKER_TOOL -> {
                StickersTool(viewModel = viewModel)
            }
            FILTERS_TOOl -> {
                FiltersTool(viewModel)
            }
            TEXT_TOOL->{
                TextTool(viewModel = viewModel)
            }
            ADJUST_TOOL->{
                adjustTools(viewModel = viewModel)
            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(items = LIST_OF_TOOLS) { index, tool ->
                MainTool(tool,index==mainToolActive) {
                    viewModel.setMainToolActive(index)
                }
            }
        }
    }
}

private fun prepareBottomMenu(): List<BottomMenuItem> {
    val bottomMenuItemsList = arrayListOf<BottomMenuItem>()

    // add menu items
    bottomMenuItemsList.add(BottomMenuItem(label = "AI", icon = Icons.Filled.Delete))
    bottomMenuItemsList.add(BottomMenuItem(label = "Brush", icon = Icons.Filled.Person))
    bottomMenuItemsList.add(BottomMenuItem(label = "Lasso", icon = Icons.Filled.Settings))
    return bottomMenuItemsList
}

@Composable
fun RemoveObjectTool(modifier: Modifier = Modifier, viewModel: EditorViewModel) {
    val bottomMenuItemsList = prepareBottomMenu()
    val itemActive by viewModel.removeObjectToolActive.collectAsState()
    val bitmapIndex by viewModel.currentBitmapIndex.collectAsState()
    val AIObjects = if (viewModel.imageBitmaps.isNotEmpty()){
        viewModel.imageBitmaps[bitmapIndex].AIObj
    } else{
        mutableListOf()
    }
    Column {
        when (itemActive) {
            DETECT_OBJECT_MODE -> {
                AITool(objects = AIObjects, onClick = { index, item ->
                    viewModel.removeObject(obj = item) {
                        //TODO
                    }
                })
            }
        }
        BottomNavigation(
            modifier = modifier,
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.primary
        ) {
            bottomMenuItemsList.forEachIndexed { index, item ->
                BottomNavigationItem(
                    selected = (itemActive == index),
                    onClick = { viewModel.setRemoveObjectToolActive(index) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    },
                    label = {
                        Text(text = item.label)
                    },
                    enabled = true,
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = MaterialTheme.colors.secondary
                )
            }
        }
    }
}

@Composable
fun MainTool(tool: String,isSelected:Boolean, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.height(50.dp).clickable {
        onClick()
    }) {
        Text(text = tool, modifier = Modifier
            .wrapContentWidth(),
            fontWeight = FontWeight.SemiBold
            , fontSize = 13.sp, color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
        )
    }
}

@Composable
fun Toolbar(modifier: Modifier, title: String) {
    Column {
        TopAppBar(
            title = {
                Text(text = title, color = MaterialTheme.colors.primary)
            },
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 4.dp
        )
    }

}

@Preview
@Composable
fun ProgressBar() {
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
fun AITool(objects: List<AITarget>, onClick: (Int, AITarget) -> Unit) {
    LazyRow {
        itemsIndexed(items = objects) { index, item ->
            AIItem(item = item) {
                onClick(index, item)
            }
        }
    }
}

@Composable
fun AIItem(item: AITarget, onClick: () -> Unit) {
    Image(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .clickable { onClick() },
        bitmap = item.origin,
        contentDescription = null
    )
    Text(text = item.isSelected.toString())
}