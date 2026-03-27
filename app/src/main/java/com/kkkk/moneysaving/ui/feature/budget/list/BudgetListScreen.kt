package com.kkkk.moneysaving.ui.feature.budget.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.ui.LocalScreenPadding
import com.kkkk.moneysaving.ui.components.BudgetIcon
import com.kkkk.moneysaving.ui.feature.budget.editor.BudgetEditorDialog
import com.kkkk.moneysaving.ui.theme.AppColor
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.Secondary
import com.kkkk.moneysaving.ui.theme.Tertiary
import com.kkkk.moneysaving.ui.theme.TextPrimary
import com.kkkk.moneysaving.ui.theme.TextSecondary
import com.kkkk.moneysaving.util.formatCurrencyAmount
import kotlinx.coroutines.delay

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BudgetListScreen(
    onOpenDetail: (String) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    viewModel: BudgetListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentBudgetId by viewModel.currentBudgetId.collectAsStateWithLifecycle()

    BudgetListContent(
        contentPadding = LocalScreenPadding.current,
        uiState = uiState,
        currentBudgetId = currentBudgetId,
        onOpenDetail = onOpenDetail,
        onUpdateBudgetId = viewModel::updateBudgetId,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun BudgetListContent(
    contentPadding: PaddingValues,
    uiState: BudgetListUiState,
    currentBudgetId: String?,
    onOpenDetail: (String) -> Unit = {},
    onUpdateBudgetId: (String?) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    val gridState = rememberLazyGridState()

    var showDialog by remember { mutableStateOf(false) }

    var previousIndex by remember { mutableIntStateOf(0) }
    var previousScrollOffset by remember { mutableIntStateOf(0) }
    val isScrollingUp by remember {
        derivedStateOf {
            when {
                gridState.firstVisibleItemIndex < previousIndex -> {
                    true
                }

                gridState.firstVisibleItemIndex > previousIndex -> {
                    false
                }

                else -> {
                    gridState.firstVisibleItemScrollOffset <= previousScrollOffset
                }
            }.also {
                previousIndex = gridState.firstVisibleItemIndex
                previousScrollOffset = gridState.firstVisibleItemScrollOffset
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor)
    ) {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = contentPadding.calculateTopPadding() + 14.dp,
                bottom = contentPadding.calculateBottomPadding() + 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.title_budget_detail),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = TextPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            item {
                BudgetRingCard(
                    remainingPercent = uiState.totalRemainingPercent
                )
            }
            item {
                BudgetSummaryCard(
                    totalBudget = uiState.totalBudget,
                    totalSpent = uiState.totalSpent
                )
            }

            itemsIndexed(
                items = uiState.items,
                key = { _, item -> item.budget.id }
            ) { index, item ->
                BudgetGridItem(
                    index = index,
                    itemState = item,
                    onOpenDetailClick = { onOpenDetail(item.budget.id) },
                    onEditClick = {
                        onUpdateBudgetId(item.budget.id)
                        showDialog = true
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 18.dp)
                .padding(bottom = contentPadding.calculateBottomPadding() + 16.dp)
        ) {
            AnimatedVisibility(
                visible = isScrollingUp,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = fadeOut() + slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300)
                )
            ) {
                Button(
                    onClick = {
                        onUpdateBudgetId(null)
                        showDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White,
                    ),
                    shape = RoundedCornerShape(28.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.title_budget_add),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (showDialog) {
            BudgetEditorDialog(
                budgetId = currentBudgetId,
                onDismiss = {
                    showDialog = false
                    onUpdateBudgetId(null)
                }
            )
        }
    }
}

@Composable
private fun BudgetRingCard(remainingPercent: Int) {
    val animatedPercent by animateIntAsState(
        targetValue = remainingPercent,
        animationSpec = tween(durationMillis = 300),
        label = "remaining_percent_animation"
    )

    val animatedSweepAngle by animateFloatAsState(
        targetValue = (remainingPercent.toFloat() / 100f) * 360f,
        animationSpec = tween(durationMillis = 300),
        label = "sweep_angle_animation"
    )

    Box(
        modifier = Modifier.height(140.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.size(110.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = Color(0xFFE7F0F3),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = Secondary,
                    startAngle = -90f,
                    sweepAngle = animatedSweepAngle,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.title_remaining),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Text(
                    text = "$animatedPercent%",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                )
            }
        }
    }
}

@Composable
private fun BudgetSummaryCard(totalBudget: Long, totalSpent: Long) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(18.dp))
                .background(Color.White, RoundedCornerShape(18.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.title_budget) + ": ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = totalBudget.formatCurrencyAmount(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = TextPrimary
                )
            }
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.title_expense) + ": ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = totalSpent.formatCurrencyAmount(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = TextPrimary
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, shape = CircleShape)
                .background(color = Tertiary, shape = CircleShape)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.title_remaining) + ": " ,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = (totalBudget - totalSpent).coerceAtLeast(0).formatCurrencyAmount(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun BudgetGridItem(
    index: Int,
    itemState: BudgetItemUiState,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    onOpenDetailClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    var play by remember(itemState.budget.id) { mutableStateOf(itemState.hasAnimated) }

    LaunchedEffect(itemState.budget.id) {
        if (!itemState.hasAnimated) {
            delay(index * 80L)
            play = true
            itemState.hasAnimated = true
        }
    }

    val animatedPercent by animateFloatAsState(
        targetValue = if (play) itemState.percent else 0f,
        animationSpec = tween(500),
        label = "budget_percent"
    )

    Box {
        Column(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .clickable(onClick = onOpenDetailClick)
                .background(color = Color(0xFFEAF4F7), shape = RoundedCornerShape(24.dp))
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            BudgetIcon(
                82f,
                itemState.budget,
                remainingPercent = animatedPercent,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
            Text(
                text = itemState.budget.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
            )
            Text(
                text = itemState.budget.amount.formatCurrencyAmount(),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF4F80FC),
                    fontSize = 14.sp
                ),
            )
        }

        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .shadow(2.dp, CircleShape)
                .size(28.dp)
                .background(Color.White, CircleShape)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = TextPrimary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview
@Composable
private fun BudgetListPreview() {
    MaterialTheme {
        BudgetListContent(
            uiState = BudgetListUiState(
                items = listOf(
                    BudgetItemUiState(
                        budget = Budget(
                            id = "1",
                            name = "Necessities",
                            amount = 5500000,
                            color = 0xFFFFD700,
                        ),
                        spentAmount = 3025000,
                        percent = 0.85f
                    )
                ),
                totalBudget = 4000000,
                totalSpent = 800000,
                totalRemainingPercent = 80
            ),
            currentBudgetId = null,
            contentPadding = PaddingValues(0.dp)
        )
    }
}
