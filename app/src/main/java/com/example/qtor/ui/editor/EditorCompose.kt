package com.example.qtor.ui.editor

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.qtor.R
import com.example.qtor.constant.LOADING
import com.example.qtor.constant.STORAGE_FILTERS
import com.example.qtor.constant.STORAGE_STICKERS

@Composable
fun CircleButton(image: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = { onClick() },
        modifier = Modifier
            .then(Modifier.size(50.dp))
            .border(1.dp, Color.Red, shape = CircleShape)
    ) {
        Icon(image, contentDescription = null, tint = Color.Red)
    }
}

@Composable
fun EditorTheme(viewModel: EditorViewModel, context: Context) {
    // A surface container using the 'background' color from the theme
    viewModel.initAssetData(STORAGE_STICKERS)
    viewModel.initAssetData(STORAGE_FILTERS)
    viewModel.initFonts()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        val state by viewModel.stateScreen.collectAsState()
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Toolbar(Modifier.weight(1f), title = context.getString(R.string.title_editor))
            if (state == LOADING) {
                ProgressBar()
            }
            EditorView(
                context,
                Modifier
                    .fillMaxSize()
                    .weight(7f), viewModel = viewModel
            )
            Row {
                CircleButton(image = ImageVector.vectorResource(id = R.drawable.ic_undo)) {
                    viewModel.undo()
                }
                CircleButton(image = ImageVector.vectorResource(id = R.drawable.ic_redo)) {
                    viewModel.redo()
                }
            }
            BottomNavigationTool(
                Modifier
                    .background(MaterialTheme.colors.onSurface)
                    .weight(2f), viewModel = viewModel
            )
        }
    }
}