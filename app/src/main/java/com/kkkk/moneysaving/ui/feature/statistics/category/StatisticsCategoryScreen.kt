package com.kkkk.moneysaving.ui.feature.statistics.category

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.VectorGroup
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.ui.components.TransactionItemCard
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.TextPrimary
import com.kkkk.moneysaving.ui.theme.TextSecondary
import com.kkkk.moneysaving.util.formatDateWithPrefix
import com.kkkk.moneysaving.util.formatMonthYearOrPrefix
import com.kkkk.moneysaving.util.formatShortAmount
import kotlinx.coroutines.delay
import java.time.YearMonth

@Composable
fun StatisticsCategoryScreen(
    onBack: () -> Unit = {},
    onTransactionClick: (String) -> Unit = {},
    viewModel: StatisticsCategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StatisticsCategoryContent(
        uiState = uiState,
        onBack = onBack,
        onTransactionClick = onTransactionClick,
        onMonthSelect = viewModel::selectMonth
    )
}

@Composable
private fun StatisticsCategoryContent(
    uiState: StatisticsCategoryUiState,
    onBack: () -> Unit,
    onTransactionClick: (String) -> Unit,
    onMonthSelect: (YearMonth) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = uiState.category?.name.orEmpty(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp)
            ) {
                Text(
                    text = if (uiState.category != null) {
                        stringResource(
                            when (uiState.category.type) {
                                CategoryType.EXPENSE -> R.string.title_total_expense
                                CategoryType.INCOME -> R.string.title_total_income
                                CategoryType.LOAN -> R.string.title_total_loan
                            }
                        )
                    } else "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))

                CategoryExpendChart(
                    chartData = uiState.chartData,
                    selectedMonth = uiState.selectedMonth,
                    categoryIcon = uiState.category?.icon ?: 0,
                    onMonthSelect = onMonthSelect
                )
            }
        }

        uiState.groupedItems.forEach { (date, transactions) ->
            item(key = date) {
                Text(
                    text = date.formatDateWithPrefix(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(transactions, key = { it.id }) { transaction ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    TransactionItemCard(
                        item = transaction,
                        onClick = { onTransactionClick(transaction.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryExpendChart(
    chartData: List<ChartData>,
    selectedMonth: YearMonth,
    categoryIcon: Int,
    onMonthSelect: (YearMonth) -> Unit
) {
    val maxAmount = chartData.maxOfOrNull { it.amount }?.coerceAtLeast(1L) ?: 1L
    val scrollState = rememberLazyListState()

    LaunchedEffect(chartData) {
        if (chartData.isNotEmpty()) {
            val selectedIndex = chartData.indexOfFirst { it.month == selectedMonth }
            if (selectedIndex != -1) {
                scrollState.scrollToItem(selectedIndex)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.width(16.dp)
        ) {
            Text(
                text = "0",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Text(
                text = "",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        LazyRow(
            state = scrollState,
            modifier = Modifier
                .wrapContentWidth()
                .height(200.dp),
            verticalAlignment = Alignment.Bottom,
        ) {

            itemsIndexed(chartData, key = { _, data -> data.month.toString() }) { index, data ->
                val isSelected = data.month == selectedMonth
                val heightFactor = data.amount.toFloat() / maxAmount

                ChartBar(
                    index = index,
                    itemState = data,
                    monthLabel = data.month.formatMonthYearOrPrefix(),
                    heightFactor = heightFactor,
                    isSelected = isSelected,
                    categoryIcon = categoryIcon,
                    onClick = { onMonthSelect(data.month) }
                )
            }
        }
    }
}

@Composable
private fun ChartBar(
    index: Int,
    itemState: ChartData,
    monthLabel: String,
    heightFactor: Float,
    isSelected: Boolean,
    categoryIcon: Int,
    onClick: () -> Unit
) {
    var play by remember(itemState.month) { mutableStateOf(itemState.hasAnimated) }

    LaunchedEffect(itemState.month) {
        if (!itemState.hasAnimated) {
            delay(index * 80L)
            play = true
            itemState.hasAnimated = true
        }
    }

    val animatedHeightFactor by animateFloatAsState(
        targetValue = if (play) heightFactor else 0f,
        animationSpec = tween(500),
        label = "chart_bar_height"
    )

    val imageVector = ImageVector.vectorResource(id = categoryIcon)

    val painter = rememberVectorPainter(
        defaultWidth = imageVector.defaultWidth,
        defaultHeight = imageVector.defaultHeight,
        viewportWidth = imageVector.viewportWidth,
        viewportHeight = imageVector.viewportHeight,
        name = imageVector.name,
        tintColor = imageVector.tintColor,
        tintBlendMode = imageVector.tintBlendMode,
        autoMirror = imageVector.autoMirror,
        content = { _, _ ->
            imageVector.root.forEachIndexed { index, node ->
                if (index != 0) {
                    RenderVectorGroup(group = node as VectorGroup)
                }
            }
        }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(76.dp)
    ) {

        AnimatedContent(
            targetState = isSelected,
            transitionSpec = {
                (slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)))
                    .togetherWith(
                        slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
                    )
            },
            label = "bar_amount"
        ) {
            Text(
                text = if (it) itemState.amount.formatShortAmount() else "",
                style = MaterialTheme.typography.bodySmall,
                color = Primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(60.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .clickable(onClick = onClick)
                .fillMaxHeight(animatedHeightFactor * 0.7f + 0.1f)
                .background(if (isSelected) Color(0xFFFFE58F) else Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = TextPrimary
            )
        }
        HorizontalDivider(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Text(
            text = monthLabel,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Black else Color.Gray,
            maxLines = 1
        )
    }
}


