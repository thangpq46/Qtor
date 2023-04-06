package com.example.qtor.ui.editor

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.qtor.R
import com.example.qtor.constant.IMAGE_TO_EDIT
import com.example.qtor.constant.LOADING
import com.example.qtor.constant.TOOL_INIT_INDEX
import com.example.qtor.ui.editor.ui.theme.QTorTheme
import org.opencv.android.OpenCVLoader

class EditorActivity : ComponentActivity() {
    private val viewModel by viewModels<EditorViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OpenCVLoader.initDebug()
        intent.getStringExtra(IMAGE_TO_EDIT)?.let {
            viewModel.initBitmaps(Uri.parse(it))
        }
        intent.getIntExtra(TOOL_INIT_INDEX,0).let {
            viewModel.setMainToolActive(it)
        }
        setContent {
            QTorTheme {
                EditorTheme(viewModel = viewModel, context = this)
            }
        }
    }

}

