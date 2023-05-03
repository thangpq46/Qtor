package com.example.qtor.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.qtor.R
import com.example.qtor.constant.*
import com.example.qtor.ui.editor.EditorActivity
import com.example.qtor.ui.setting.SettingActivity
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private var toolIndex: Int? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                startActivity(Intent(
                    this@MainActivity,
                    EditorActivity::class.java
                ).apply {
                    putExtra(IMAGE_TO_EDIT, uri.toString())
                    toolIndex?.let {
                        putExtra(TOOL_INIT_INDEX, it)
                    }
                })
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainActivityUI(this) {
                toolIndex = it
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainActivityUI(context: Context, onClick: (Int?) -> Unit) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Card(
                    modifier = Modifier,
                    shape = RoundedCornerShape(5.dp),
                ) {
                    AutoSlidingCarousel(
                        itemsCount = images.size,
                        itemContent = { index ->
                            Image(
                                painterResource(id = images[index]),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(.5f)
                            )
                        }
                    )
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 80.dp),
                    contentPadding = PaddingValues(vertical = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .height(LocalConfiguration.current.screenHeightDp.dp * 3 / 10f)
                ) {
                    itemsIndexed(tools) { index, tool ->
                        // Replace this with your item composable
                        Surface(
                            modifier = Modifier
                                .padding(vertical = 15.dp)
                                .clickable {
                                    onClick(index)
                                },
                            color = Color.Transparent
                        ) {
                            Column {
                                Icon(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Transparent)
                                        .padding(vertical = 10.dp),
                                    painter = painterResource(id = tool.resourceID),
                                    contentDescription = null,
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = stringResource(id = tool.toolNameID),
                                    textAlign = TextAlign.Center,
                                    fontSize = androidx.compose.material3.MaterialTheme.typography.titleSmall.fontSize,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                        }

                    }
                }
                Button(
                    onClick = { onClick(null) }, colors = ButtonDefaults.buttonColors(
                        backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.start_edit),
                        modifier = Modifier
                            .fillMaxWidth(.8f),
                        textAlign = TextAlign.Center,
                        fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize
                    )
                }
            }
            Column {

                TopAppBar(
                    title = {},
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp,
                    actions = {
                        IconButton(onClick = {
                            context.startActivity(Intent(context, SettingActivity::class.java))
                        }) {
                            Icon(
                                Icons.Default.Settings,
                                null,
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    })
            }

        }
    }
}


@Composable
fun DotsIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = MaterialTheme.colors.primary /* Color.Yellow */,
    unSelectedColor: Color = MaterialTheme.colors.secondary /* Color.Gray */,
    dotSize: Dp
) {
    LazyRow(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ) {
        items(totalDots) { index ->
            IndicatorDot(
                color = if (index == selectedIndex) selectedColor else unSelectedColor,
                size = dotSize
            )

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}

@Composable
fun IndicatorDot(
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AutoSlidingCarousel(
    modifier: Modifier = Modifier,
    autoSlideDuration: Long = ONE_SECOND,
    pagerState: PagerState = remember { PagerState() },
    itemsCount: Int,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(pagerState.currentPage) {
        delay(autoSlideDuration)
        pagerState.animateScrollToPage((pagerState.currentPage + 1) % itemsCount)
    }

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        HorizontalPager(count = itemsCount, state = pagerState) { page ->
            itemContent(page)
        }

        // you can remove the surface in case you don't want
        // the transparant bacground
        Surface(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.BottomCenter),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.5f)
        ) {
            DotsIndicator(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                totalDots = itemsCount,
                selectedIndex = if (isDragged) pagerState.currentPage else pagerState.currentPage,
                dotSize = 8.dp
            )
        }
    }
}