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
    val brightness by viewModel.brightness.collectAsState()
    val contrast by viewModel.contrast.collectAsState()
    val saturation by viewModel.saturation.collectAsState()
    val warmth by viewModel.warmth.collectAsState()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        when(toolActive){
            ADJUST_BRIGHTNESS->{
                SeekBar(modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.CenterHorizontally),valueRange = .5f..1.5f, value = brightness, step = 101, onValueChange ={
                    viewModel.setBrightness(it)
                } )
            }
            ADJUST_WARMTH->{
                SeekBar(modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.CenterHorizontally),valueRange = .5f..2f, value = warmth, step = 151, onValueChange ={
                    viewModel.setWarmth(it)
                } )
            }
            ADJUST_CONTRAST->{
                SeekBar(modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.CenterHorizontally),valueRange = .5f..1.5f, value = contrast, step = 101, onValueChange ={
                    viewModel.setContrast(it)
                } )
            }
            ADJUST_SATURATION->{
                SeekBar(modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.CenterHorizontally),valueRange = 0f..2f, value = saturation, step = 201, onValueChange ={
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
fun SeekBar(modifier: Modifier, valueRange: ClosedFloatingPointRange<Float>, value:Float, step:Int, onValueChange:(Float)->Unit) {
//    var sliderPosition by remember { mutableStateOf(value) }
    Column {
        Slider(modifier=modifier,
            value = value,
            onValueChange = {
//                sliderPosition = it
                            onValueChange(it)
                            },
            valueRange = valueRange,
            steps = step
        )
//        Text(
//            text = sliderPosition.toString(),
//            modifier = Modifier.fillMaxWidth()
//        )
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

