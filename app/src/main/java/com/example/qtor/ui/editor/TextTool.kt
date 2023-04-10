package com.example.qtor.ui.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qtor.R


@Composable
fun TextTool(viewModel: EditorViewModel) {
    var text by remember {
        mutableStateOf("")
    }
    var userTextFont by remember {
        mutableStateOf<String?>(null)
    }
    val fonts = viewModel.fonts
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(modifier = Modifier
            .fillMaxWidth()
            .weight(7f),value = text, placeholder = {
                                                    Text(text="Please type here...", fontFamily =userTextFont?.let { fontFamily(fontName = it) }, textAlign = TextAlign.Center )
        }, onValueChange = {
            text = it
        }, singleLine = true, textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontSize=MaterialTheme.typography.h5.fontSize,
            fontFamily = userTextFont?.let { fontFamily(fontName = it) }
        ))
        IconButton(modifier = Modifier.weight(1f),onClick = {
            if (text.isNotEmpty()){
                viewModel.addText(text,userTextFont,12f)
            }
        }) {
            Text(text = "ADD")
        }
    }
    LazyRow {
        items(items = fonts) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 7.dp)
                    .clickable {
                        userTextFont = it.fontName
                    },
                text = stringResource(id = R.string.text_demo_font),
                fontFamily = fontFamily(it.fontName)
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun fontFamily(fontName: String) = FontFamily(
    Font(fontName, LocalContext.current.assets)
)