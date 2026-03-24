package com.kkkk.moneysaving.ui.feature.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
import com.kkkk.moneysaving.ui.components.SegmentedTab
import com.kkkk.moneysaving.ui.components.StatItemCard
import com.kkkk.moneysaving.ui.components.StatItemUI
import com.kkkk.moneysaving.ui.theme.AppColor
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.Secondary
import kotlinx.coroutines.launch
import java.time.Month
import java.util.Locale

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
) {
    StatisticsContent(modifier = modifier)
}

@Composable
private fun StatisticsContent(
    modifier: Modifier = Modifier,
) {
    var selectedType by remember { mutableStateOf(CategoryType.EXPENSE) }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColor,
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.screen_statistics_title),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            TotalBalanceCard()

            SegmentedTab(
                selected = selectedType,
                onSelectedChange = { selectedType = it }
            )

            MonthSelector(
                pagerState = pagerState
            )

            ChartPagerSection(pagerState)

            CategoryList()
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
                    text = stringResource(R.string.home_total_balance),
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "10,000,000 d",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
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
    month: Month = Month.MARCH,
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
            text = month.text(),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
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
                imageVector = if(pagerState.currentPage == 0) Icons.Default.BarChart else Icons.Default.PieChart,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

@Composable
private fun ChartPagerSection(
    pagerState: PagerState
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
                PieChartComposable()
            } else {
                BarChartComposable()
            }
        }
    }
}

@Composable
private fun BarChartComposable() {
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
                    labelCount = 3
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
                    axisMaximum = 800f
                }

                axisRight.isEnabled = false
                setScaleEnabled(false)
                setPinchZoom(false)

                val entries = listOf(
                    BarEntry(0.2f, 250f),
                    BarEntry(1f, 650f),
                    BarEntry(1.8f, 420f)
                )
                val dataSet = BarDataSet(entries, "").apply {
                    color = Color(0xFF4A80FF).toArgb()
                    setDrawValues(false)
                }

                data = BarData(dataSet).apply {
                    barWidth = 0.15f
                }

                animateY(1000)
                invalidate()
            }
        }
    )
}

@Composable
private fun PieChartComposable() {
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(Color.White.toArgb())
                holeRadius = 55f
                transparentCircleRadius = 0f
                setDrawEntryLabels(false)
                setTouchEnabled(false)

                val entries = listOf(
                    PieEntry(35f, ""),
                    PieEntry(25f, ""),
                    PieEntry(15f, ""),
                    PieEntry(25f, "")
                )

                val dataSet = PieDataSet(entries, "").apply {
                    colors = listOf(
                        Color(0xFFFFBDE0).toArgb(),
                        Color(0xFFADDCFF).toArgb(),
                        Color(0xFFA6E39D).toArgb(),
                        Color(0xFFFFE78F).toArgb()
                    )
                    sliceSpace = 2f
                    setDrawValues(false)
                }

                data = PieData(dataSet)
                animateY(1000)
                invalidate()
            }
        }
    )
}

@Composable
private fun CategoryList() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StatItemCard (
            item = StatItemUI(
                id = "1",
                categoryName = "Bread",
                categoryColor = 0xFFADAA12,
                amount= 200000,
                process = 0.7f,
                categoryIcon = R.drawable.ic_cat_salary
            )
        )

        StatItemCard (
            item = StatItemUI(
                id = "1",
                categoryName = "Bread",
                categoryColor = 0xFFADAA12,
                amount= 200000,
                process = 0.7f,
                categoryIcon = R.drawable.ic_cat_salary
            )
        )
    }
}

fun Month.text() = this.name.lowercase().replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString()
}

@Preview
@Composable
private fun StatisticsPreview() {
    MaterialTheme {
        StatisticsContent()
    }
}
