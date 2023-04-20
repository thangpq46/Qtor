package com.example.qtor.ui.editor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.qtor.R
import com.example.qtor.constant.IMAGE_TO_EDIT
import com.example.qtor.constant.TOOL_INIT_INDEX
import com.example.qtor.constant.URI_SAVED_IMAGE
import com.example.qtor.ui.setting.ui.theme.QTorTheme
import com.example.qtor.ui.share.ShareImageActivity
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
        viewModel.setBrightness(1.001f)
        setContent {
            AppTheme {
                var showDialog by remember {
                    mutableStateOf(false)
                }
                onBackPressedDispatcher.addCallback {
                    showDialog=true
                }
                Box {
                    EditorTheme(viewModel = viewModel, context = this@EditorActivity)
                    TopAppBar(
                        title = {
                        },
                        backgroundColor = Color.Transparent,
                        navigationIcon = {
                            IconButton(onClick = {  onBackPressedDispatcher.onBackPressed()}) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        },
                        elevation = 0.dp,
                        actions = {
                            IconButton(onClick = {
                                viewModel.saveImage(
                                    onSuccess = {
                                        Toast.makeText(this@EditorActivity, it.toString(),Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@EditorActivity,ShareImageActivity::class.java).apply {
                                            putExtra(URI_SAVED_IMAGE,it.toString())
                                        })
                                    },
                                    onFailed = {
                                        Toast.makeText(this@EditorActivity,"F",Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }) {
                                Icon(
                                    painterResource(id = R.drawable.ic_download),
                                    contentDescription = "DownLoad",
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                    )
                }
                if(showDialog){
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxSize()
                        .clickable { }) {
                            CustomDialogUI(onCancel = {
                                showDialog=false
                            }, onConfirm = {
                                finish()
                            })
                    }
                }


            }
        }
    }

}

