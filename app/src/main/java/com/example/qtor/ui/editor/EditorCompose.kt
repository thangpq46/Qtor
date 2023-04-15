package com.example.qtor.ui.editor

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.qtor.R
import com.example.qtor.constant.LOADING
import com.example.qtor.constant.MAIN_TOOl_REMOVE_OBJECT
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
        val mainToolActive by viewModel.mainToolActive.collectAsState()
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state == LOADING) {
                ProgressBar()
            }
            EditorView(
                context,
                viewModel = viewModel
            )

        }
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom

        ) {
            if (mainToolActive == MAIN_TOOl_REMOVE_OBJECT) {
                Row(
                    horizontalArrangement = Arrangement.Start, modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                ) {
                    CircleButton(image = ImageVector.vectorResource(id = R.drawable.ic_undo)) {
                        viewModel.undo()
                    }
                    CircleButton(image = ImageVector.vectorResource(id = R.drawable.ic_redo)) {
                        viewModel.redo()
                    }
                }
            }
            BottomNavigationTool(
                Modifier
                    .background(MaterialTheme.colors.onSurface)
                    .weight(2f), viewModel = viewModel
            )
        }
        if (!state) {
            Column(modifier = Modifier
                .fillMaxSize()
                .clickable { }) {}
        }
    }
}


@Composable
fun CustomDialogUI(modifier: Modifier = Modifier,onCancel:()->Unit,onConfirm:()->Unit) {
    Card(
        //shape = MaterialTheme.shapes.medium,
        shape = RoundedCornerShape(10.dp),
        // modifier = modifier.size(280.dp, 240.dp)
        modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier
                .background(Color.White)
        ) {

            //.......................................................................
            Image(
                painter = painterResource(id = R.drawable.ic_notifi),
                contentDescription = null, // decorative
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colors.primary
                ),
                modifier = Modifier
                    .padding(top = 35.dp)
                    .height(70.dp)
                    .fillMaxWidth(),

                )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.discard_change),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.h6,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(id = R.string.alert_discard_change),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.body2
                )
            }
            //.......................................................................
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(MaterialTheme.colors.background),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                TextButton(onClick = {
                    onCancel()
                }) {

                    Text(
                        text = stringResource(id = R.string.cancel),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
                TextButton(onClick = {
                    onConfirm()
                }) {
                    Text(
                        stringResource(id = R.string.confirm),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}