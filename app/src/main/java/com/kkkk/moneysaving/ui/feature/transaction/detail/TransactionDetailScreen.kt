package com.kkkk.moneysaving.ui.feature.transaction.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.model.Category
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.ui.components.BudgetIcon
import com.kkkk.moneysaving.ui.theme.AppColor
import com.kkkk.moneysaving.ui.theme.MoneySavingTheme
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.TextError
import com.kkkk.moneysaving.ui.theme.TextPositive
import com.kkkk.moneysaving.ui.theme.TextPrimary
import com.kkkk.moneysaving.ui.theme.TextSecondary
import com.kkkk.moneysaving.util.formatCurrencyAmount
import com.kkkk.moneysaving.util.formatDate

@Composable
fun TransactionDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    viewModel: TransactionDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TransactionDetailContent(
        uiState = uiState,
        onBack = onBack,
        onEdit = onEdit,
        onDelete = { viewModel.delete(onBack) },
    )
}

@Composable
private fun TransactionDetailContent(
    uiState: TransactionDetailUiState,
    onBack: () -> Unit = {},
    onEdit: (String) -> Unit = {},
    onDelete: () -> Unit = {},
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    onClick = { onEdit(uiState.transaction!!.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColor),
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.title_edit),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Primary,
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = uiState.transaction?.occurredAt?.formatDate().orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                )
                Text(
                    text = uiState.transaction?.amount?.formatCurrencyAmount().orEmpty(),
                    style = MaterialTheme.typography.titleLarge,
                    color = if ((uiState.transaction?.amount ?: 0) < 0) TextError else TextPositive,
                )
            }

            DetailCard(
                title = stringResource(R.string.title_editor_category),
                trailing = {
                    if (uiState.category != null) {
                        Image(
                            painter = painterResource(uiState.category.icon),
                            contentDescription = null
                        )
                    }
                },
                value = uiState.category?.name.orEmpty(),
            )

            DetailCard(
                title = stringResource(R.string.title_budget),
                trailing = {
                    if (uiState.budget != null) {
                        BudgetIcon(
                            iconSize = 40f,
                            budget = uiState.budget,
                            remainingPercent = 1f,
                            showRemaining = false,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.NotInterested,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                },
                value = uiState.budget?.name ?: "None",
            )

            DetailCard(
                title = stringResource(R.string.title_editor_note),
                trailing = {},
                value = uiState.transaction?.note.orEmpty(),
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextError,
                ),
                border = BorderStroke(1.dp, TextError),
                shape = RoundedCornerShape(28.dp),
            ) {
                Text(
                    text = stringResource(R.string.title_delete_transaction),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    trailing: @Composable () -> Unit,
    value: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(18.dp))
            .background(color = Color(0xFFEAEAEA), shape = RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = TextSecondary,
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            trailing()
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
            )
        }
    }
}

@Preview
@Composable
private fun TransactionDetailScreenPreview() {
    MoneySavingTheme {
        TransactionDetailContent(
            uiState = TransactionDetailUiState(
                transaction = Transaction(
                    id = "1",
                    categoryId = "1",
                    budgetId = "1",
                    amount = -150000,
                    note = "Dinner with friends",
                    occurredAt = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    isDeleted = false,
                ),
                category = Category(
                    id = "1",
                    name = "Food",
                    type = CategoryType.EXPENSE,
                    color = 0xFFF44336,
                    icon = R.drawable.ic_cat_food,
                ),
                budget = Budget(
                    id = "1",
                    name = "Monthly Budget",
                    amount = 5000000,
                    color = 0xFF4CAF50,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    isDeleted = false,
                )
            ),
        )
    }
}
