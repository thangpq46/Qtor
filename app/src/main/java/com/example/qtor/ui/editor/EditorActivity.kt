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
        setContent {
            QTorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val state by viewModel.stateScreen.collectAsState()
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Toolbar(Modifier.weight(1f), title = getString(R.string.title_editor))
                        if (state== LOADING){
                            ProgressBar()
                        }
                        EditorView(
                            this@EditorActivity,
                            Modifier
                                .fillMaxSize()
                                .weight(7f), viewModel = viewModel
                        )

                        BottomNavigationTool(
                            Modifier
                                .background(MaterialTheme.colors.onSurface)
                                .weight(2f), viewModel = viewModel
                        )
                    }
                }
            }
        }
    }

}

