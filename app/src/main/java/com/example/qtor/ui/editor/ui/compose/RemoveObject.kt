package com.example.qtor.ui.editor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qtor.constant.*
import com.example.qtor.data.model.AITarget
import com.example.qtor.data.model.BottomMenuItem


@Composable
fun BottomNavigationTool(viewModel: EditorViewModel) {
    val mainToolActive by viewModel.mainToolActive.collectAsState()
    Surface(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground) {
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
                    AdjustColorTools(viewModel = viewModel)
                }
            }
            Divider(color = androidx.compose.material3.MaterialTheme.colorScheme.outline, thickness = 1.dp)
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
//            backgroundColor = MaterialTheme.colors.onBackground,
//            contentColor = MaterialTheme.colors.onBackground
        ) {
            bottomMenuItemsList.forEachIndexed { index, item ->
                BottomNavigationItem(
                    modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.onBackground),
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
                    selectedContentColor = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    unselectedContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@Composable
fun MainTool(tool: String,isSelected:Boolean, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier
        .height(50.dp)
        .clickable {
            onClick()
        }) {
        Text(text = tool, modifier = Modifier
            .wrapContentWidth(),
            fontWeight = FontWeight.SemiBold
            , fontSize = 13.sp, color = if (isSelected) MaterialTheme.colors.error else androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
fun Toolbar(modifier: Modifier, title: String) {

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
    LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(vertical = 5.dp)) {
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
            .width(70.dp)
            .height(70.dp)
            .clickable { onClick() }
            .clip(RoundedCornerShape(5.dp)).border(
                BorderStroke(5.dp, androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer),
                RoundedCornerShape(5.dp)
            ),
        bitmap = item.origin,
        contentDescription = null
    )
}