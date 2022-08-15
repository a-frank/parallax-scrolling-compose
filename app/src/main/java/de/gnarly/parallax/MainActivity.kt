package de.gnarly.parallax

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.gnarly.parallax.ui.theme.ParallaxScrollingTheme

@ExperimentalFoundationApi class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			ParallaxScrollingTheme {

				val items = (0..1_000).map { "Item $it" }.toList()

				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colors.background
				) {
					MainScreen(items)
				}
			}
		}
	}
}

@ExperimentalFoundationApi
@Composable
fun MainScreen(items: List<String>) {

	val lazyListState = rememberLazyListState()
	val visibility by remember {
		derivedStateOf {
			when {
				lazyListState.layoutInfo.visibleItemsInfo.isNotEmpty() && lazyListState.firstVisibleItemIndex == 0 -> {
					val imageSize = lazyListState.layoutInfo.visibleItemsInfo[0].size
					val scrollOffset = lazyListState.firstVisibleItemScrollOffset

					scrollOffset / imageSize.toFloat()
				}
				else                                                                                               -> 1f
			}
		}
	}
	val firstItemTranslationY by remember {
		derivedStateOf {
			when {
				lazyListState.layoutInfo.visibleItemsInfo.isNotEmpty() && lazyListState.firstVisibleItemIndex == 0 -> lazyListState.firstVisibleItemScrollOffset * .6f
				else                                                                                               -> 0f
			}
		}
	}

	Box {
		LazyColumn(state = lazyListState) {
			item {
				Image(
					painter = painterResource(id = R.drawable.landscape_bw),
					contentDescription = "Landscape in BW",
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.fillParentMaxWidth()
						.graphicsLayer {
							alpha = 1f - visibility
							translationY = firstItemTranslationY
						}
				)
			}
			itemsIndexed(items) { idx, item ->
				Text(
					text = item,
					modifier = Modifier
						.fillMaxWidth()
						.background(MaterialTheme.colors.surface)
						.padding(8.dp)
				)

				if (idx < items.size - 1) {
					Divider(color = Color.LightGray)
				}
			}
		}

		TopBarWithFadeableBackground(backgroundAlpha = visibility)
	}
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
	ParallaxScrollingTheme {
		MainScreen((0..10).map { "Demo $it" }.toList())
	}
}