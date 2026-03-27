package com.kkkk.moneysaving.ui.feature.statistics.balance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.theme.AppColor
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.Secondary
import com.kkkk.moneysaving.ui.theme.TextPrimary
import com.kkkk.moneysaving.ui.theme.TextSecondary
import com.kkkk.moneysaving.util.formatCurrencyAmount

@Composable
fun StatisticsBalanceScreen(
    onBack: () -> Unit = {},
    viewModel: StatisticsBalanceViewModel = hiltViewModel()
) {
    var showYearPicker by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val header = listOf(
        stringResource(R.string.title_month),
        stringResource(R.string.title_expense),
        stringResource(R.string.title_income),
        stringResource(R.string.title_loan),
        stringResource(R.string.title_borrow),
        stringResource(R.string.title_balance),
    )

    Surface(
        Modifier
            .background(AppColor)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Column(Modifier.statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onBack) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = { showYearPicker = true },
                ) {
                    Text(
                        text = uiState.selectedYear.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                    )
                }
            }

            BalanceCard(uiState.totalBalance)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${stringResource(R.string.title_expenses)}: ${uiState.totalExpense.formatCurrencyAmount()}",
                    style = MaterialTheme.typography.bodyMedium,
                )

                Text(
                    text = "${stringResource(R.string.title_incomes)}: ${uiState.totalIncome.formatCurrencyAmount()}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            LazyVerticalGrid(
                modifier = Modifier.background(TextSecondary),
                columns = GridCells.Fixed(6),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                if (uiState.monthlyStats.isNotEmpty()) {
                    header.forEachIndexed { index, item ->
                        item(key = "header_$index") {
                            GridCells(
                                text = item,
                                textColor = Color.White,
                                backgroundColor = Secondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                uiState.monthlyStats.forEach { stat ->
                    item { GridCells(stat.monthName, TextPrimary, AppColor) }
                    item { GridCells(stat.expense.toString(), TextPrimary, AppColor) }
                    item { GridCells(stat.income.toString(), TextPrimary, AppColor) }
                    item { GridCells(stat.loan.toString(), TextPrimary, AppColor) }
                    item { GridCells(stat.borrow.toString(), TextPrimary, AppColor) }
                    item { GridCells(stat.balance.toString(), TextPrimary, AppColor) }
                }
            }
        }

        if (showYearPicker) {
            SetYearDialog(
                onDismiss = { showYearPicker = false },
                onSave = {
                    viewModel.selectYear(it)
                    showYearPicker = false
                }
            )
        }
    }
}

@Composable
private fun GridCells(
    text: String,
    textColor: Color,
    backgroundColor: Color,
    fontWeight: FontWeight = FontWeight.Medium
) {
    Text(
        modifier = Modifier
            .background(backgroundColor)
            .padding(vertical = 6.dp),
        color = textColor,
        text = text,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = fontWeight,
    )
}

@Composable
private fun BalanceCard(
    balance: Long
) {
    Card(
        modifier = Modifier
            .padding(vertical = 24.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Secondary),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.title_balance),
                color = TextPrimary.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = balance.formatCurrencyAmount(),
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
