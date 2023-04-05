package com.example.qtor.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.qtor.R
import com.example.qtor.constant.IMAGE_TO_EDIT
import com.example.qtor.constant.ONE_SECOND
import com.example.qtor.constant.TYPE_ALL_IMAGE
import com.example.qtor.ui.editor.EditorActivity
import com.example.qtor.ui.theme.QTorTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.delay
import kotlin.math.min

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
            MainActivityUI {
                pickImageToEditor.launch(TYPE_ALL_IMAGE)
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainActivityUI(onClick:()->Unit) {
    QTorTheme {
        Surface(modifier= Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val screenHeight =  LocalConfiguration.current.screenHeightDp.dp
                val images = listOf(
                    ImageBitmap.imageResource(R.drawable.demo1),
                    ImageBitmap.imageResource(R.drawable.demo2),
                    ImageBitmap.imageResource(R.drawable.demo3),
                    ImageBitmap.imageResource(R.drawable.demo4),
                    ImageBitmap.imageResource(R.drawable.demo5),
                    ImageBitmap.imageResource(R.drawable.demo6),
                )
                val tools = listOf(
                    R.drawable.ic_template,
                    R.drawable.ic_remove_object,
                    R.drawable.ic_portrait,
                    R.drawable.ic_filter,
                    R.drawable.ic_adjust,
                    R.drawable.ic_effects,
                    R.drawable.ic_template,
                    R.drawable.ic_remove_object,
                )
                Card(
                    modifier = Modifier,
                    shape = RoundedCornerShape(5.dp),
                ) {
                    AutoSlidingCarousel(
                        itemsCount = images.size,
                        itemContent = { index ->
                            Image(
                                images[index],
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.height(screenHeight/2)
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
                    items(tools) { id ->
                        // Replace this with your item composable
                        Image(painter = painterResource(id = id), contentDescription = null, modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 40.dp))
                    }
                }
                Button(onClick = { onClick() }) {
                    Text(text = "START EDITING", modifier = Modifier
                        .fillMaxWidth(.7f), textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.h5.fontSize
                        )
                }
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
                selectedIndex = if (isDragged) pagerState.currentPage else pagerState.targetPage,
                dotSize = 8.dp
            )
        }
    }
}