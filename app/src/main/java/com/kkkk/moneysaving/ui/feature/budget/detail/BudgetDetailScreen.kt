package com.kkkk.moneysaving.ui.feature.budget.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.ui.components.BudgetIcon
import com.kkkk.moneysaving.ui.components.StatItemCard
import com.kkkk.moneysaving.ui.theme.AppColor
import com.kkkk.moneysaving.ui.theme.Secondary
import com.kkkk.moneysaving.ui.theme.TextBudgetDark
import com.kkkk.moneysaving.ui.theme.TextPrimary
import com.kkkk.moneysaving.util.formatCurrencyAmount

private const val iconSize = 170F

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BudgetDetailScreen(
    onBack: () -> Unit,
    onCategoryDetail: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    viewModel: BudgetDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BudgetDetailContent(
        uiState = uiState,
        onBackClick = onBack,
        onCategoryClick = onCategoryDetail,
        onDelete = { viewModel.delete(onBack) },
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun BudgetDetailContent(
    uiState: BudgetDetailUiState,
    onBackClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onDelete: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    var headerHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val isTransitionFinished = animatedVisibilityScope?.transition?.let {
        it.currentState == it.targetState && it.currentState == EnterExitState.Visible
    } ?: true

    val contentAlpha by animateFloatAsState(
        targetValue = if (isTransitionFinished) 1f else 0f,
        animationSpec = tween(300),
        label = "contentAlpha"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isTransitionFinished) AppColor else Secondary,
        animationSpec = tween(300),
        label = "contentAlpha"
    )

    val budgetProgress by animateFloatAsState(
        targetValue = if (isTransitionFinished) uiState.progress else 0f,
        animationSpec = tween(300),
        label = "contentAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .navigationBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = contentAlpha }
                .padding(top = headerHeight)
                .background(
                    color = AppColor,
                    shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.groupedItems.isEmpty()) {
                BudgetScreenEmptyState()
            }
        }

        LazyColumn(
            Modifier
                .wrapContentHeight()
                .background(color = Secondary)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            headerHeight = with(density) {
                                coordinates.size.height.toDp() - (iconSize * 0.6f).dp
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.graphicsLayer { alpha = contentAlpha }) {
                        BudgetDetailHeader(
                            title = uiState.budget.name,
                            amount = uiState.budget.amount,
                            onBack = onBackClick,
                            onDelete = onDelete,
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height((iconSize * 0.6f).dp)
                                .background(
                                    color = backgroundColor,
                                    shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
                                )
                        )
                        BudgetIcon(
                            iconSize,
                            uiState.budget,
                            budgetProgress,
                            showRemaining = false,
                            showShadow = false,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )

                    }
                }
            }
            uiState.groupedItems.forEach { (typeLabel, items) ->
                item(key = typeLabel) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { alpha = contentAlpha }
                            .background(AppColor)
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(
                                when (typeLabel) {
                                    CategoryType.EXPENSE -> R.string.title_total_expense
                                    CategoryType.INCOME -> R.string.title_total_income
                                    CategoryType.LOAN -> R.string.title_total_loan
                                }
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                        )

                        Text(
                            text = uiState.groupedSummary[typeLabel]!!.formatCurrencyAmount(),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                        )
                    }
                }
                items(
                    items = items,
                    key = { it.categoryId }
                ) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { alpha = contentAlpha }
                            .background(color = AppColor)
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        StatItemCard(
                            item = item,
                            onClick = { onCategoryClick(it) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetDetailHeader(
    title: String,
    amount: Long = 0,
    onBack: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onBack) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )

            Text(
                text = amount.formatCurrencyAmount(),
                style = MaterialTheme.typography.titleLarge,
                color = TextBudgetDark,
            )
        }
        IconButton(onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun BudgetScreenEmptyState() {
    Box(contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.pc_no_transaction),
                contentDescription = null,
                modifier = Modifier.size(135.dp),
            )
            Text(
                text = stringResource(R.string.message_budget_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
            )
        }
    }
}

@Preview
@Composable
private fun BudgetDetailPreview() {
    MaterialTheme {
        BudgetDetailContent(
            uiState = BudgetDetailUiState(
                budget = Budget(
                    id = "1",
                    name = "Lunch",
                    amount = 500000,
                    color = 0xFFFFC0D8,
                    createdAt = 0,
                    updatedAt = 0,
                    isDeleted = false
                ),
                groupedItems = emptyMap(),
            ),
            onBackClick = {},
            onCategoryClick = {},
            onDelete = {}
        )
    }
}
