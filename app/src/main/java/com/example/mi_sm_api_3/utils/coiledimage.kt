package com.example.mi_sm_api_3.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.compose.AsyncImage

@Composable
fun CoiledImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {

    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {

            add(SvgDecoder.Factory())
        }
        .build()


    val request = ImageRequest.Builder(LocalContext.current)
        .data(url)

        .decoderFactory(SvgDecoder.Factory())
        .crossfade(true)
        .build()


    AsyncImage(
        model = request,
        imageLoader = imageLoader,
        contentDescription = contentDescription,
        modifier = modifier
    )
}
