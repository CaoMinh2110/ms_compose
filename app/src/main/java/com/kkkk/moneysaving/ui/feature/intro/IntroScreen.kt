package com.kkkk.moneysaving.ui.feature.intro

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.theme.Secondary
import kotlinx.coroutines.launch

@Composable
fun IntroScreen(
    onFinished: () -> Unit,
) {
    IntroContent(onFinished = onFinished)
}

@Composable
private fun IntroContent(
    onFinished: () -> Unit,
) {
    val pages = remember {
        listOf(
            IntroPage(R.string.message_intro_1, R.string.message_intro_1),
            IntroPage(R.string.message_intro_2, R.string.message_intro_2),
            IntroPage(R.string.message_intro_3, R.string.message_intro_3),
            IntroPage(R.string.message_intro_4, R.string.message_intro_4),
        )
    }
    val pagerState = rememberPagerState(initialPage = 0) { pages.size }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Secondary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 34.dp, bottom = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { pageIndex ->
                IntroPageContent(
                    page = pages[pageIndex],
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            PagerIndicators(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = onFinished,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Secondary,
                        contentColor = Color.White,
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.title_skip),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                val isLast = pagerState.currentPage == pages.lastIndex
                Button(
                    onClick = {
                        if (isLast) onFinished() else {
                            val next = (pagerState.currentPage + 1).coerceAtMost(pages.lastIndex)
                            scope.launch { pagerState.animateScrollToPage(next) }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Secondary,
                    ),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(
                        text = stringResource(if (isLast) R.string.title_start else R.string.title_next),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun IntroPageContent(
    page: IntroPage,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(color = Color(0xFF17889A), shape = RoundedCornerShape(24.dp)),
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = stringResource(page.titleRes),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(page.descRes),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFE0F2F6),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PagerIndicators(
    pageCount: Int,
    currentPage: Int,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val color = if (index == currentPage) Color.White else Color(0xFFB9DDE4)
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color = color, shape = CircleShape),
            )
        }
    }
}

private data class IntroPage(
    @param:StringRes val titleRes: Int,
    @param:StringRes val descRes: Int,
)

@Preview
@Composable
private fun IntroPreview() {
    MaterialTheme {
        IntroContent(onFinished = {})
    }
}
