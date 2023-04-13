package com.example.qtor.ui.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.qtor.R


@Composable
fun TextTool(viewModel: EditorViewModel) {
    var text by remember {
        mutableStateOf("")
    }
    var userTextFont by remember {
        mutableStateOf<String?>(null)
    }
    var textColor by remember {
        mutableStateOf(Color.Black)
    }
    val fonts = viewModel.fonts
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(modifier = Modifier
            .fillMaxWidth()
            .weight(7f), value = text, colors = TextFieldDefaults.textFieldColors(textColor = textColor), placeholder = {
            Text(
                text = stringResource(id = R.string.text_place_holder),
                fontFamily = userTextFont?.let { fontFamily(fontName = it) },
                textAlign = TextAlign.Center,
                color = textColor,
                modifier = Modifier.fillMaxWidth()
            )
        }, onValueChange = {
            text = it
        }, singleLine = true, textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.h5.fontSize,
            fontFamily = userTextFont?.let { fontFamily(fontName = it) }
        ))
        IconButton(modifier = Modifier.weight(1f), onClick = {
            if (text.isNotEmpty()) {
                viewModel.addText(text, userTextFont, 12f)
                text = ""
            }
        }) {
            Image(
                painter = painterResource(id = R.drawable.ic_add_text),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
            )
        }
    }
    val colors = mutableListOf<Color>(
        Color.Blue, Color.Black, Color.Cyan, Color.DarkGray, Color.Gray,
        Color.Green, Color.LightGray, Color.Magenta, Color.Yellow
    )

    LazyRow {
        items(colors) {
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .padding(2.dp)
                    .border(2.dp, Brush.linearGradient(listOf(Color.Black, Color.Yellow,Color.Green)), shape = RectangleShape)
                    .background(it).clickable { textColor=it }
            )
        }
    }


    LazyRow {
        items(items = fonts) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.border(
                    1.dp, MaterialTheme.colors.primary,
                    RectangleShape
                )
            ) {
                Text(
                    modifier = Modifier
                        .width(150.dp)
                        .height(50.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .clickable {
                            userTextFont = it.fontName
                        },
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.h5.fontSize,
                    text = stringResource(id = R.string.text_demo_font),
                    fontFamily = fontFamily(it.fontName)
                )
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun fontFamily(fontName: String) = FontFamily(
    Font(fontName, LocalContext.current.assets)
)