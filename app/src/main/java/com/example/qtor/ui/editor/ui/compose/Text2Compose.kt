package com.example.qtor.ui.editor.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.qtor.R
import com.example.qtor.constant.colors
import com.example.qtor.data.model.BottomMenuItem
import com.example.qtor.ui.editor.EditorViewModel
import com.example.qtor.ui.editor.SeekBar
import com.example.qtor.ui.editor.fontFamily
import com.example.qtor.util.removeFolderAndEx
import kotlinx.coroutines.delay


//enum class TextToolActive{
//    NULL,EDITTEXT,FONTS,OPACITY,COLORS,BACKGROUNDS
//}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextTools(viewModel: EditorViewModel){
    val showKeyBoard by viewModel.showKeyboard.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val windowInfo = LocalWindowInfo.current
    val keyboard = LocalSoftwareKeyboardController.current
    var text by remember {
        mutableStateOf("QT")
    }
    var alpha by remember {
        mutableStateOf(50f)
    }
    val textInActive by viewModel.textInActive.collectAsState()
    var toolactive by remember {
        mutableStateOf(-1)
    }
    var colorActive by remember {
        mutableStateOf(0)
    }
    val fonts = viewModel.fonts
    LaunchedEffect(showKeyBoard) {
        if (showKeyBoard) {
            focusRequester.requestFocus()
            delay(100) // Make sure you have delay here
            keyboard?.show()
        }
    }
    when(toolactive){
        1->{
            if (textInActive){
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = text,
                    textStyle = MaterialTheme.typography.body2,
                    onValueChange = {
                        text=it
                        viewModel.updateStickerText(it)
                    },
                    label = {  },
                )
            }

        }
        2->{
            LazyRow{
                items(items=fonts){
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(5.dp)
                            )
                    ) {
                        Text(
                            modifier = Modifier
                                .width(70.dp)
                                .height(70.dp)
                                .wrapContentHeight(Alignment.CenterVertically)
                                .clickable {
                                    viewModel.updateStickerText(it)
                                },
                            maxLines = 2,
                            textAlign = TextAlign.Center,
                            fontSize = MaterialTheme.typography.h6.fontSize,
                            text = removeFolderAndEx(it.fontName),
                            fontFamily = fontFamily(it.fontName),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
        3->{
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(5.dp)
            ) {
                itemsIndexed(colors) { index, item ->
                    Box(
                        modifier = Modifier
                            .width(25.dp)
                            .height(25.dp)
                            .clip(CircleShape)
                            .background(item)
                            .border(
                                BorderStroke(
                                    if (index == colorActive) 3.dp else (-1).dp,
                                    androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                                ), shape = CircleShape
                            )
                            .clickable {
                                viewModel.updateTextColor(item)
                                colorActive = index
                            }
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
                Text(modifier = Modifier.fillMaxWidth(.12f),text = stringResource(id = R.string.opacity),color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary)
                SeekBar(modifier = Modifier
                    .fillMaxWidth(0.7f)
                    ,
                    valueRange = 0f..255f,
                    value = alpha,
                    step = 128,
                    onValueChange = {
                        alpha=it
                        viewModel.updateAlphaText(it)
                    })
                Text(modifier = Modifier.fillMaxWidth(.15f),text = "${(alpha/255*100).toInt()}", color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary)
            }

        }
        4->{

        }
        else->{}
    }
    BottomNavigation(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val items = listOf(BottomMenuItem(label = "Add+", icon = Icons.Filled.Add),BottomMenuItem(label = "EditText", icon = Icons.Filled.Edit),BottomMenuItem(label = "Fonts", icon = Icons.Filled.Email),BottomMenuItem(label = "Colors", icon = Icons.Filled.Settings))
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.onBackground),
                selected = false,
                onClick = {
                    toolactive=index
                    viewModel.selectTextTool(index)},
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                },
                enabled = index==0 || textInActive,
                selectedContentColor = androidx.compose.material3.MaterialTheme.colorScheme.error,
                unselectedContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}