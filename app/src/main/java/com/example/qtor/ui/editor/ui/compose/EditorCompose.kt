package com.example.qtor.ui.editor.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.qtor.R

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
fun CustomDialogUI(modifier: Modifier = Modifier, onCancel: () -> Unit, onConfirm: () -> Unit) {
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

@Preview
@Composable
fun SnackbarDemo() {
    Column {
        val (snackbarVisibleState, setSnackBarState) = remember { mutableStateOf(false) }

        Button(onClick = { setSnackBarState(!snackbarVisibleState) }) {
            if (snackbarVisibleState) {
                Text("Hide Snackbar")
            } else {
                Text("Show Snackbar")
            }
        }
        if (snackbarVisibleState) {
            Snackbar(

                action = {
                    TextButton(onClick = { setSnackBarState(false) }) {
                        Text("OK")
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) { Text(text = "This is a snackbar!") }
        }
    }
}
