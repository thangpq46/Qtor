package com.example.qtor.ui.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.qtor.constant.LIST_OF_TOOLS
import com.example.qtor.data.model.Sticker
import com.example.qtor.data.model.Tool


@Composable
fun BottomNavigation(modifier: Modifier = Modifier, viewModel: EditorViewModel) {
    val tool by viewModel.toolActive.collectAsState()
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(items = LIST_OF_TOOLS) { index, tool ->
            Tool(tool) {
                viewModel.setToolActive(index)
            }
        }
    }

}

@Composable
fun Tool(tool: String, onClick: () -> Unit) {
    Text(text = tool, modifier = Modifier
        .width(70.dp)
        .clickable(true, null, null) {
            onClick()
        })
}

@Composable
fun Toolbar(modifier: Modifier, title: String) {
    TopAppBar(
        title = {
            Text(text = title, color = MaterialTheme.colors.primary)
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 4.dp
    )
}

@Composable
@Preview
fun StickersTool(modifier: Modifier = Modifier) {
    Surface {
        var textSearch by remember {
            mutableStateOf("")
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { textSearch = it },
            value = textSearch,
            placeholder = {
                Text(
                    text = "Search here...",
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    color = MaterialTheme.colors.onSecondary
                )
            },
            singleLine = true,
            leadingIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = { textSearch = "" }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        )
    }
}

@Composable
fun PhotoGrid(photos: List<Sticker>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp)
    ) {
        items(photos) {

        }
    }
}

@Composable
fun ListTools(viewModel: EditorViewModel){
    val tool by viewModel.toolActive.collectAsState()
    val listTools = mutableListOf(StickersTool(),StickersTool())
}

@Composable
fun TemplateTool(){

}

@Composable
fun TextTools(){

}