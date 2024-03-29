package com.example.qtor.ui.editor.ui.compose

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.qtor.R
import com.example.qtor.data.model.TimeStamp
import com.example.qtor.ui.editor.EditorViewModel
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection

import java.time.LocalDateTime


enum class PICKTIMETYPE{
    PICK,SET
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeStampCompose(viewModel: EditorViewModel){

    val calendarState = rememberUseCaseState()
    val timeState = rememberUseCaseState()
    val timeStampInActive by viewModel.timeStampInActive.collectAsState()
    var pickTimeType by remember{
        mutableStateOf(PICKTIMETYPE.PICK)
    }

    CalendarDialog(state =calendarState,
        config = CalendarConfig(monthSelection = true, yearSelection = true)
        , selection = CalendarSelection.Date{
            viewModel.updateDate(it)
    })
    ClockDialog(state = timeState , selection = ClockSelection.HoursMinutes{ hours, minutes ->
        viewModel.updateTime(hours,minutes)
    } )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
            TextButton(onClick = { pickTimeType = PICKTIMETYPE.PICK }
            ) {
                Text(text = stringResource(id = R.string.pick),color =androidx.compose.material3.MaterialTheme.colorScheme.onSecondary)
            }
            TextButton(onClick = { pickTimeType = PICKTIMETYPE.SET }) {
                Text(text = stringResource(id = R.string.set), color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary)
            }
        }
        if (pickTimeType==PICKTIMETYPE.PICK){
            Button(onClick = { viewModel.setTimeTimeStamp() }, enabled = timeStampInActive
            ) {
                Text(text = stringResource(id = R.string.current_time))
            }
            Button(onClick = { viewModel.setTimeTimeStamp(LocalDateTime.of(2001,6,4,4,20)) }, enabled = timeStampInActive) {
                Text(text = stringResource(id = R.string.taken_time))
            }
        }else{
            Button(onClick = { calendarState.show() }, enabled = timeStampInActive) {
                Text(text = stringResource(id = R.string.set_date))
            }
            Button(onClick = { timeState.show() }, enabled = timeStampInActive) {
                Text(text = stringResource(id = R.string.set_time))
            }
        }
    }

    LazyRow(modifier = Modifier
        .height(60.dp)
        .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically){
        items(items = viewModel.timeStamps){
            TimeStamp(item = it, onClick = {
                viewModel.addTimeStamp(it)
            })
        }
    }
}

@Composable
fun TimeStamp(item: TimeStamp,onClick:()->Unit) {
    Image(modifier = Modifier
        .clickable { onClick() }
        .width(60.dp)
        .height(60.dp),bitmap = item.bitmap, contentDescription =null )
}
