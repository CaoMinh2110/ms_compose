package com.kkkk.moneysaving.ui.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.ui.LocalScreenPadding
import com.kkkk.moneysaving.ui.components.SegmentedTab
import com.kkkk.moneysaving.ui.components.TransactionItemCard
import com.kkkk.moneysaving.ui.components.TransactionItemUI
import com.kkkk.moneysaving.util.formatCurrencyAmount
import com.kkkk.moneysaving.ui.theme.AppColor
import com.kkkk.moneysaving.ui.theme.MoneySavingTheme
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.Secondary
import com.kkkk.moneysaving.ui.theme.TextPrimary
import com.kkkk.moneysaving.ui.theme.TextSecondary
import com.kkkk.moneysaving.util.formatDateWithPrefix
import com.kkkk.moneysaving.util.formatMonthYear
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth

@Composable
fun HomeScreen(
    onOpenSearch: () -> Unit = {},
    onOpenTransactionDetail: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        onTypeSelected = viewModel::selectType,
        onTimeSelected = viewModel::selectTime,
        onDeleteTransaction = viewModel::delete,
        onOpenSearch = onOpenSearch,
        onOpenTransactionDetail = onOpenTransactionDetail,
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onTypeSelected: (CategoryType) -> Unit = {},
    onTimeSelected: (YearMonth) -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onOpenTransactionDetail: (String) -> Unit = {},
    onDeleteTransaction: (String) -> Unit = {},
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor)
            .padding(bottom = LocalScreenPadding.current.calculateBottomPadding()),
    ) {
        LazyColumn(
            modifier = Modifier
                .wrapContentHeight()
                .background(color = Secondary)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    HomeHeaderSection(
                        showTimePicker,
                        uiState.selectedTime
                    ) { showTimePicker = true }
                    HomeBalanceSection(uiState.balance)
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(
                            color = AppColor,
                            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
                        )
                        .padding(vertical = 16.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    SegmentedTab(
                        selected = uiState.selectedType,
                        onSelectedChange = onTypeSelected,
                    )
                    HomeTotalRow(
                        totalAmount = uiState.totalAmount,
                        type = uiState.selectedType,
                    )
                    HomeSearchBar(onClick = onOpenSearch)
                }
            }

            uiState.groupedItems.forEach { (date, transactions) ->
                item(key = date) {
                    Text(
                        text = date.formatDateWithPrefix(),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColor)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(
                    items = transactions,
                    key = { it.id }
                ) { transaction ->

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = AppColor)
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        TransactionItemCard(
                            item = transaction,
                            onClick = { onOpenTransactionDetail(it) },
                            onDeleteClick = { onDeleteTransaction(it) },
                            showDelete = true
                        )
                    }
                }
            }
        }

        if (showTimePicker) {
            SetMonthDialog(
                initialYear = uiState.selectedTime.year,
                initialMonth = uiState.selectedTime.monthValue,
                onDismiss = { showTimePicker = false },
                onSave = { month, year -> onTimeSelected(YearMonth.of(year, month)) }
            )
        }
    }
}

@Composable
private fun HomeHeaderSection(
    isDialogVisible: Boolean,
    time: YearMonth,
    onTimeHolderClick: () -> Unit = {}
) {
    val rotation by animateFloatAsState(
        targetValue = if (isDialogVisible) 180f else 0f,
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_coins),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = stringResource(R.string.title_app_name),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
            )
        }

        Row(
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onTimeHolderClick() }
                .background(color = Color.Transparent, shape = CircleShape)
                .border(border = BorderStroke(1.dp, Color.White), shape = CircleShape)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = time.formatMonthYear(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
            )
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
private fun HomeBalanceSection(amount: Long) {
    var isVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Primary, shape = RoundedCornerShape(18.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(R.string.title_balance),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedContent(targetState = isVisible, label = "balance_vis") { visible ->
                    Text(
                        text = if (visible) amount.formatCurrencyAmount() else "*** ***",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                    )
                }
                IconButton({ isVisible = !isVisible }) {
                    Icon(
                        imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeTotalRow(
    totalAmount: Long,
    type: CategoryType,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_expenditure),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = stringResource(
                    when (type) {
                        CategoryType.EXPENSE -> R.string.title_total_expense
                        CategoryType.INCOME -> R.string.title_total_income
                        else -> R.string.title_total_loan
                    }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = Primary,
            )
        }
        Text(
            text = totalAmount.formatCurrencyAmount(),
            style = MaterialTheme.typography.titleMedium,
            color = Primary,
        )
    }
}

@Composable
private fun HomeSearchBar(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            placeholder = {
                Text(
                    text = stringResource(R.string.hint_search),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = TextSecondary,
                )
            },
            shape = RoundedCornerShape(18.dp),
            singleLine = true,
        )
    }
}

@Preview
@Composable
private fun HomeContentPreview() {
    MoneySavingTheme {
        HomeContent(
            uiState = HomeUiState(
                selectedType = CategoryType.EXPENSE,
                selectedTime = YearMonth.now(),
                totalAmount = 1500000L,
                groupedItems = mapOf(
                    LocalDate.now() to listOf(
                        TransactionItemUI(
                            id = "1",
                            categoryName = "Food",
                            note = "Lunch at restaurant",
                            amount = -50000L,
                            occurredAt = System.currentTimeMillis(),
                            categoryIcon = R.drawable.ic_cat_food
                        ),
                        TransactionItemUI(
                            id = "2",
                            categoryName = "Transport",
                            note = "Taxi to work",
                            amount = -30000L,
                            occurredAt = System.currentTimeMillis(),
                            categoryIcon = R.drawable.ic_cat_traffic
                        )
                    ),
                    LocalDate.of(
                        Year.now().value,
                        Month.FEBRUARY,
                        LocalDate.now().dayOfMonth
                    ) to listOf(
                        TransactionItemUI(
                            id = "3",
                            categoryName = "Shopping",
                            note = "New shirt",
                            amount = -250000L,
                            occurredAt = System.currentTimeMillis() - 86400000,
                            categoryIcon = R.drawable.ic_cat_shopping
                        )
                    )
                )
            ),
        )
    }
}

