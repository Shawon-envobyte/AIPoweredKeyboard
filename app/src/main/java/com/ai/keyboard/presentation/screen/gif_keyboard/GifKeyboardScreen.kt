package com.ai.keyboard.presentation.screen.gif_keyboard

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.ai.keyboard.R
import com.ai.keyboard.presentation.screen.keyboard.KeyboardIntent
import org.koin.androidx.compose.koinViewModel

@Composable
fun GifKeyboardScreen(
    onIntent: (KeyboardIntent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GifKeyboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()


    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        if (uiState.isSearchBarVisible) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.toggleSearchBar() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.searchGifs(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search GIFs") }
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.toggleSearchBar() }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                LazyRow {
                    items(uiState.categories) { category ->
                        Text(
                            text = category.searchTerm.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase() else it.toString()
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { viewModel.onCategorySelected(category) }
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(text = uiState.error!!, modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(uiState.gifs) { gif ->
                        AsyncImage(
                            model = gif.previewUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(100.dp)
                                .fillMaxWidth()
                                .clickable {

                                }
                        )
                    }
                    item {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tenor_attribution),
                            contentDescription = "Tenor attribution",
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}
