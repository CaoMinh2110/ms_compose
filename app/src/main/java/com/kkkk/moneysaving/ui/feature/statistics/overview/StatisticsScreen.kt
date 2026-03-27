package com.kkkk.moneysaving.ui.feature.statistics.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.ui.LocalScreenPadding
import com.kkkk.moneysaving.ui.components.SegmentedTab
import com.kkkk.moneysaving.ui.components.StatItemCard
import com.kkkk.moneysaving.ui.components.StatItemUI
import com.kkkk.moneysaving.ui.feature.home.SetMonthDialog
import com.kkkk.moneysaving.util.formatCurrencyAmount
import com.kkkk.moneysaving.ui.theme.AppColor
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.Secondary
import com.kkkk.moneysaving.util.formatMonthYear
import kotlinx.coroutines.launch
import java.time.YearMonth
import kotlin.math.abs

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel(),
    onBalanceClick: () -> Unit,
    onCategoryClick: (String, YearMonth) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StatisticsContent(
        modifier = modifier,
        uiState = uiState,
        onTypeChange = viewModel::selectType,
        onTimeChange = viewModel::selectTime,
        onBalanceClick = onBalanceClick,
        onCategoryClick = { categoryId -> onCategoryClick(categoryId, uiState.selectedTime) },
    )
}

@Composable
private fun StatisticsContent(
    modifier: Modifier = Modifier,
    uiState: StatisticsUiState,
    onTypeChange: (CategoryType) -> Unit,
    onTimeChange: (YearMonth) -> Unit,
    onBalanceClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    var showTimePicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor),
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(
                bottom = LocalScreenPadding.current.calculateBottomPadding() + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text(
                    modifier = Modifier.statusBarsPadding(),
                    text = stringResource(R.string.title_statistics),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
            }

            item {
                TotalBalanceCard(
                    balance = uiState.balance,
                    onClick = onBalanceClick
                )
            }

            item {
                SegmentedTab(
                    selected = uiState.selectedType,
                    onSelectedChange = onTypeChange
                )
            }

            item {
                MonthSelector(
                    time = uiState.selectedTime,
                    onCalendarClick = { showTimePicker = true },
                    pagerState = pagerState
                )
            }

            item { ChartPagerSection(pagerState, uiState.groupedItems) }

            items(uiState.groupedItems, key = { item -> item.categoryId }) { item ->
                StatItemCard(item = item, onClick = onCategoryClick)
            }
        }

        if (showTimePicker) {
            SetMonthDialog(
                initialYear = uiState.selectedTime.year,
                initialMonth = uiState.selectedTime.monthValue,
                onDismiss = { showTimePicker = false },
                onSave = { month, year -> onTimeChange(YearMonth.of(year, month)) }
            )
        }
    }
}

@Composable
private fun TotalBalanceCard(
    balance: Long = 0,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 10.dp,
        color = Secondary
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.title_balance),
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = balance.formatCurrencyAmount(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            IconButton(
                onClick, modifier = Modifier
                    .size(44.dp)
                    .clip(shape = CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF43889B),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun MonthSelector(
    time: YearMonth,
    onCalendarClick: () -> Unit = {},
    pagerState: PagerState,
) {
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onCalendarClick,
            modifier = Modifier
                .clip(shape = CircleShape)
                .background(Primary)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(25.dp)
            )
        }

        Text(
            text = time.formatMonthYear(),
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        IconButton(
            onClick = {
                val next = (pagerState.currentPage + 1) % 2
                scope.launch { pagerState.animateScrollToPage(next) }
            },
            modifier = Modifier
                .clip(shape = CircleShape)
                .background(Primary)
        ) {
            Icon(
                imageVector = if (pagerState.currentPage == 0) Icons.Default.BarChart else Icons.Default.PieChart,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

@Composable
private fun ChartPagerSection(
    pagerState: PagerState,
    items: List<StatItemUI>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
        ) { page ->
            if (page == 0) {
                PieChartComposable(items)
            } else {
                BarChartComposable(items)
            }
        }
    }
}

@Composable
private fun BarChartComposable(items: List<StatItemUI>) {
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(16.dp),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                setDrawValueAboveBar(false)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    axisLineColor = Color(0xFFF0F0F0).toArgb()
                    granularity = 1f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String = ""
                    }
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = Color(0xFFF0F0F0).toArgb()
                    axisLineColor = Color.Transparent.toArgb()
                    labelCount = 5
                    textColor = Color.Gray.toArgb()
                    textSize = 10f
                    axisMinimum = 0f
                }

                axisRight.isEnabled = false
                setScaleEnabled(false)
                setPinchZoom(false)
            }
        },
        update = { chart ->
            val entries = items.take(5).mapIndexed { index, item ->
                BarEntry(index.toFloat(), abs(item.amount).toFloat())
            }
            val dataSet = BarDataSet(entries, "").apply {
                colors = items.take(5).map { it.categoryColor.toInt() }
                valueTextColor = Color.Black.toArgb()
                valueTextSize = 10f
                setDrawValues(false)
            }
            chart.data = BarData(dataSet).apply {
                barWidth = 0.4f
            }
            chart.invalidate()
        }
    )
}

@Composable
private fun PieChartComposable(items: List<StatItemUI>) {
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(16.dp),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                isDrawHoleEnabled = true
                holeRadius = 50f
                transparentCircleRadius = 65f
                setHoleColor(Color.White.toArgb())
                setDrawEntryLabels(false)
                isRotationEnabled = false
            }
        },
        update = { chart ->
            val entries = items.map { item ->
                PieEntry(abs(item.amount).toFloat(), item.categoryName)
            }
            val dataSet = PieDataSet(entries, "").apply {
                colors = items.map { it.categoryColor.toInt() }
                setDrawValues(false)
                sliceSpace = 2f
            }
            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
}
