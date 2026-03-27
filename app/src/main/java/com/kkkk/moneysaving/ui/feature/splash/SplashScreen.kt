package com.kkkk.moneysaving.ui.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.navigate.RootRoute
import com.kkkk.moneysaving.ui.navigate.StartupViewModel
import com.kkkk.moneysaving.ui.theme.Primary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: (RootRoute) -> Unit,
    viewModel: StartupViewModel,
) {
    val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()
    val loadingProgress by viewModel.loadingProgress.collectAsStateWithLifecycle()

    LaunchedEffect(startDestination, loadingProgress) {
        if (startDestination != null && loadingProgress >= 1f) {
            delay(500)
            onFinished(startDestination!!)
        }
    }

    SplashContent(progress = loadingProgress)
}

@Composable
private fun SplashContent(progress: Float = 0f) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFCBE8EE),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.pc_app_logo),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.title_app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF4B4B4B),
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(PaddingValues(start = 24.dp, end = 24.dp, bottom = 28.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.raw.gif_logo)
                        .decoderFactory(GifDecoder.Factory())
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.size(width = 90.dp, height = 86.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.message_splash_loading,
                        (progress * 100).toInt()
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF4B4B4B),
                )
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                    modifier = Modifier
                        .height(6.dp)
                        .padding(horizontal = 56.dp),
                    color = Primary,
                    trackColor = Color(0xFFE6F5F8),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.message_splash_ads_note),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF4B4B4B),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SplashPreview() {
    MaterialTheme {
        SplashContent(progress = 0.7f)
    }
}
