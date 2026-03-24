package com.kkkk.moneysaving.ui.feature.budget.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.ui.LocalScreenPadding
import com.kkkk.moneysaving.ui.components.BudgetIcon
import com.kkkk.moneysaving.ui.components.StatItemCard
import com.kkkk.moneysaving.ui.feature.transaction.detail.toAmountString
import com.kkkk.moneysaving.ui.theme.AppColor
import com.kkkk.moneysaving.ui.theme.Secondary
import com.kkkk.moneysaving.ui.theme.TextPrimary

private const val iconSize = 170F

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BudgetDetailScreen(
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    viewModel: BudgetDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BudgetDetailContent(
        uiState = uiState,
        onBack = onBack,
        onDelete = { viewModel.delete(onBack) },
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun BudgetDetailContent(
    uiState: BudgetDetailUiState,
    onBack: () -> Unit,
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Secondary)
            .padding(top = LocalScreenPadding.current.calculateTopPadding()),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = contentAlpha }
                .padding(top = headerHeight)
                .background(
                    color = AppColor,
                    shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
                )
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Column(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            headerHeight = with(density) {
                                coordinates.size.height.toDp() - iconSize.dp * 0.7f + 16.dp
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.graphicsLayer { alpha = contentAlpha }) {
                        BudgetDetailHeader(
                            title = uiState.budget.name,
                            amount = uiState.budget.amount,
                            onBack = onBack,
                            onDelete = onDelete,
                        )
                    }

                    BudgetIcon(
                        iconSize,
                        uiState.budget,
                        uiState.progress,
                        showRemaining = false,
                        showShadow = false,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }

            if (uiState.groupedItems.isEmpty()) {
                item {
                    BudgetScreenEmptyState(
                        Modifier
                        .graphicsLayer { alpha = contentAlpha }
                        .fillMaxSize(),
                    )
                }
            } else {
                uiState.groupedItems.forEach { (typeLabel, items) ->
                    item(key = typeLabel) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer { alpha = contentAlpha }
                                .background(AppColor)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = typeLabel,
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                            )

                            Text(
                                text = typeLabel,
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                            )
                        }
                    }
                    items(
                        items = items,
                        key = { it.id }
                    ) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer { alpha = contentAlpha }
                                .background(AppColor)
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            StatItemCard(
                                item = item,
                                onClick = {},
                            )
                        }
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
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBackIos,
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
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
            )

            Text(
                text = amount.toAmountString(),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
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
private fun BudgetScreenEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(color = Color(0xFFEAF4F7), shape = RoundedCornerShape(18.dp)),
            )
            Text(
                text = stringResource(R.string.budget_no_data),
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
                    name = "Mỹ phẩm",
                    amount = 500000,
                    color = 0xFFFFC0D8,
                    createdAt = 0,
                    updatedAt = 0,
                    isDeleted = false
                ),
                groupedItems = emptyMap(),
            ),
            onBack = {},
            onDelete = {}
        )
    }
}
