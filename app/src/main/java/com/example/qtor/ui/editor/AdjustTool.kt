package com.example.qtor.ui.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qtor.constant.*
import com.example.qtor.data.model.Tool


@Composable
fun adjustTools(viewModel: EditorViewModel) {
    var toolActive by remember {
        mutableStateOf(0)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        when(toolActive){
            ADJUST_BRIGHTNESS->{
                SeekBar(modifier = Modifier.fillMaxWidth(0.7f).align(Alignment.CenterHorizontally),valueRange = -255f..255f, initValue = 0f, step = 510, onValueChange ={
                    viewModel.setBrightness(it)
                } )
            }
            ADJUST_BRILLIANCE->{
                SeekBar(modifier = Modifier.fillMaxWidth(0.7f).align(Alignment.CenterHorizontally),valueRange = 1f..100f, initValue = 50f, step = 100, onValueChange ={
                    viewModel.setBrilliance(it)
                } )
            }
            ADJUST_CONTRAST->{
                SeekBar(modifier = Modifier.fillMaxWidth(0.7f).align(Alignment.CenterHorizontally),valueRange = 0f..10f, initValue = 1f, step = 1, onValueChange ={
                    viewModel.setContrast(it)
                } )
            }
            ADJUST_SATURATION->{
                SeekBar(modifier = Modifier.fillMaxWidth(0.7f).align(Alignment.CenterHorizontally),valueRange = 0f..200f, initValue = 100f, step = 200, onValueChange ={
                    viewModel.setSaturation(it)
                } )
            }
        }
        LazyRow(contentPadding = PaddingValues(horizontal = 5.dp)) {
            itemsIndexed(items = ADJUST_LIST_TOOLS) { index, item ->

                AdjustTool(tool = item,index==toolActive){
                    toolActive=index
                }

            }
        }
    }

}

@Composable
fun SeekBar(modifier: Modifier,valueRange: ClosedFloatingPointRange<Float>,initValue:Float,step:Int,onValueChange:(Float)->Unit) {
    var sliderPosition by remember { mutableStateOf(initValue) }
    Column {
        Slider(modifier=modifier,
            value = sliderPosition,
            onValueChange = { sliderPosition = it
                            onValueChange(it)
                            },
            valueRange = valueRange,
            steps = step
        )
        Text(
            text = sliderPosition.toInt().toString(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AdjustTool(tool: Tool,isToolActive:Boolean,onClick: ()->Unit) {
    Surface(
        Modifier
            .clickable {
                onClick()
            }
            .width(70.dp)
            .height(70.dp)) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(modifier = Modifier.padding(vertical = 5.dp),painter = painterResource(id = tool.resourceID), contentDescription = null, colorFilter = ColorFilter.tint(if (isToolActive) MaterialTheme.colors.primary else MaterialTheme.colors.secondary))
            Text(text = stringResource(id = tool.toolNameID), color =  if (isToolActive) MaterialTheme.colors.primary else MaterialTheme.colors.secondary, textAlign = TextAlign.Center, fontSize = 12.sp)
        }
    }
}

