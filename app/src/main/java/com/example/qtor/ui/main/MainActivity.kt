package com.example.qtor.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.qtor.constant.IMAGE_TO_EDIT
import com.example.qtor.constant.TYPE_ALL_IMAGE
import com.example.qtor.ui.editor.EditorActivity
import com.example.qtor.ui.theme.QTorTheme

class MainActivity : ComponentActivity() {

    private val pickImageToEditor =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                startActivity(Intent(
                    this@MainActivity,
                    EditorActivity::class.java
                ).apply {
                    putExtra(IMAGE_TO_EDIT, uri.toString())
                })
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DefaultPreview {
                pickImageToEditor.launch(TYPE_ALL_IMAGE)
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun DefaultPreview(onClick:()->Unit) {
    QTorTheme {
        Surface(modifier= Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            Column {
                Text(text = "EDIT", modifier = Modifier
                    .fillMaxSize()
                    .clickable(true, onClick = {
                        onClick()
                    }))
            }
        }
    }
}