package com.example.qtor.ui.share

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.qtor.R
import com.example.qtor.constant.URI_SAVED_IMAGE
import com.example.qtor.ui.main.MainActivity
import com.example.qtor.ui.share.ui.theme.QTorTheme

class ShareImageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(
            this@ShareImageActivity,
            intent.getStringExtra(URI_SAVED_IMAGE),
            Toast.LENGTH_SHORT
        ).show()
        setContent {
            QTorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TopAppBar(
                            elevation = 10.dp,
                            modifier = Modifier
                                .background(Color.Green)
                                .fillMaxWidth(),
                            title = {},
                            navigationIcon = {
                                IconButton(onClick = { onBackPressedDispatcher.onBackPressed() }) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.Black
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    startActivity(
                                        Intent(
                                            this@ShareImageActivity,
                                            MainActivity::class.java
                                        ).apply {
                                            flags =
                                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                        })
                                }) {
                                    Icon(
                                        painterResource(id = R.drawable.ic_home),
                                        contentDescription = "DownLoad",
                                        tint = Color.Black
                                    )
                                }
                            }

                        )
                        val painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = intent.getStringExtra("uri"))
                                .apply(block = fun ImageRequest.Builder.() {
                                    crossfade(true)
                                    placeholder(android.R.drawable.ic_menu_gallery)
                                    error(android.R.drawable.ic_dialog_alert)
                                }).build()
                        )
                        Image(
                            painter = painter, contentDescription = null, modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(.8f)
                        )
                        Button(onClick = {
                            shareImage(
                                Uri.parse(
                                    intent.getStringExtra(
                                        URI_SAVED_IMAGE
                                    )
                                )
                            )
                        }) {
                            Box(contentAlignment = Alignment.Center) {
                                Row {
                                    Text(text = getString(R.string.share_image))
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private fun shareImage(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)

        // putting uri of image to be shared
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        // adding text to share
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image")

        // Add subject Here
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        // setting type to image
        intent.type = "image/png"

        // calling startactivity() to share
        startActivity(Intent.createChooser(intent, "Share Via"))
    }
}
