package com.example.qtor.ui.editor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.compose.AppTheme
import com.example.qtor.R
import com.example.qtor.constant.*
import com.example.qtor.ui.editor.ui.compose.CircleButton
import com.example.qtor.ui.editor.ui.compose.CustomDialogUI
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
        intent.getIntExtra(TOOL_INIT_INDEX, 0).let {
            viewModel.setMainToolActive(it)
        }
        viewModel.initAssetData(STORAGE_STICKERS)
        viewModel.setBrightness(1.001f)
        viewModel.initFonts()
        viewModel.initTimeStamps()
        setContent {
            AppTheme {
                val notification by viewModel.notification.collectAsState()
                if (notification != "") {
                    Toast.makeText(this, notification, Toast.LENGTH_SHORT).show()
                    viewModel.rsNoti()
                }
                var showDialog by remember {
                    mutableStateOf(false)
                }
                onBackPressedDispatcher.addCallback {
                    showDialog = true
                }
                val drawX by viewModel.drawX.collectAsState()
                val drawY by viewModel.drawY.collectAsState()
                val scaleF by viewModel.scaleF.collectAsState()
                Box {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.inverseSurface
                    ) {
                        val state by viewModel.stateScreen.collectAsState()
                        val mainToolActive by viewModel.mainToolActive.collectAsState()
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TopAppBar(
                                modifier = Modifier.zIndex(2f),
                                title = {
                                },
                                backgroundColor = Color.Transparent,
                                navigationIcon = {
                                    IconButton(onClick = { onBackPressedDispatcher.onBackPressed() }) {
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
                                                Toast.makeText(
                                                    this@EditorActivity,
                                                    it.toString(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                startActivity(
                                                    Intent(
                                                        this@EditorActivity,
                                                        ShareImageActivity::class.java
                                                    ).apply {
                                                        putExtra(URI_SAVED_IMAGE, it.toString())
                                                    })
                                            },
                                            onFailed = {
                                                Toast.makeText(
                                                    this@EditorActivity,
                                                    "F",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
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
                            if (state == LOADING) {
                                ProgressBar()
                            }
                            EditorView(
                                this@EditorActivity,
                                viewModel = viewModel
                            )

                        }
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom

                        ) {
                            if (mainToolActive == MAIN_TOOl_REMOVE_OBJECT) {
                                Row(
                                    horizontalArrangement = Arrangement.Start, modifier = Modifier
                                        .padding(horizontal = 10.dp)
                                        .fillMaxWidth()
                                ) {
                                    CircleButton(image = ImageVector.vectorResource(id = R.drawable.ic_undo)) {
                                        viewModel.undo()
                                    }
                                    CircleButton(image = ImageVector.vectorResource(id = R.drawable.ic_redo)) {
                                        viewModel.redo()
                                    }
                                }
                            }
                            BottomNavigationTool(
                                viewModel = viewModel
                            )
                        }
                        if (!state) {
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .clickable { }) {}
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (drawX != 0f || drawY != 0f || scaleF != 1f) {
                            Button(
                                onClick = { /*TODO*/ },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 8.dp,
                                    disabledElevation = 0.dp
                                )
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .clickable { viewModel.resetDrawPos() }
                                        .width(35.dp)
                                        .height(35.dp),
                                    painter = painterResource(id = R.drawable.ic_fit_screen),
                                    contentDescription = null,
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                    }
                }
                if (showDialog) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxSize()
                        .clickable { }) {
                        CustomDialogUI(onCancel = {
                            showDialog = false
                        }, onConfirm = {
                            finish()
                        })
                    }
                }


            }
        }
    }

}

