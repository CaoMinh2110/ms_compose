package com.kkkk.moneysaving.ui.feature.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.navigate.StartupViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: (String) -> Unit,
    viewModel: StartupViewModel,
) {
    val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()

    LaunchedEffect(startDestination) {
        val dest = startDestination ?: return@LaunchedEffect
        delay(900)
        onFinished(dest)
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFCBE8EE),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .background(color = Color(0xFFF2FAFC), shape = CircleShape),
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.splash_title),
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.splash_loading, 70),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF4B4B4B),
                )
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { 0.7f },
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                    modifier = Modifier
                        .height(6.dp)
                        .padding(horizontal = 56.dp),
                    color = Color(0xFF1B4B59),
                    trackColor = Color(0xFFE6F5F8),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.splash_ads_note),
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
        SplashContent()
    }
}
