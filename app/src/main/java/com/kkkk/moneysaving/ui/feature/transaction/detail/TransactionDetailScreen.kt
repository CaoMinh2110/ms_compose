package com.kkkk.moneysaving.ui.feature.transaction.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.model.Category
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.ui.LocalCurrencySymbol
import com.kkkk.moneysaving.ui.LocalScreenPadding
import com.kkkk.moneysaving.ui.theme.MoneySavingTheme
import com.kkkk.moneysaving.ui.theme.TextError
import com.kkkk.moneysaving.ui.theme.TextPositive
import com.kkkk.moneysaving.ui.theme.TextPrimary
import com.kkkk.moneysaving.ui.theme.TextSecondary
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        onDelete = viewModel::delete,
    )
}

@Composable
private fun TransactionDetailContent(
    uiState: TransactionDetailUiState,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit,
) {
    val tx = uiState.transaction

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 20.dp,
                    vertical = LocalScreenPadding.current.calculateTopPadding() + 14.dp
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBackIos,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onBack),
                )

                Text(
                    text = stringResource(R.string.transaction_edit),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF1B4B59),
                    modifier = Modifier.clickable(enabled = tx != null) {
                        tx?.let { onEdit(it.id) }
                    },
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val date = tx?.occurredAt?.toDateLabel() ?: ""
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                )
                Text(
                    text = tx?.amount?.toAmountString().orEmpty(),
                    style = MaterialTheme.typography.titleLarge,
                    color = if ((tx?.amount ?: 0) < 0) TextError else TextPositive,
                )
            }

            DetailCard(
                title = stringResource(R.string.transaction_category),
                trailing = {
                    Image(
                        painter = painterResource(uiState.category!!.icon),
                        contentDescription = null
                    )
                },
                value = uiState.category?.name.orEmpty(),
            )

            DetailCard(
                title = stringResource(R.string.transaction_budget),
                trailing = {
                    Icon(
                        painter = painterResource(uiState.category!!.icon),
                        contentDescription = null
                    )
                },
                value = uiState.budget?.name.orEmpty(),
            )

            DetailCard(
                title = stringResource(R.string.transaction_note),
                trailing = {},
                value = tx?.note.orEmpty(),
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextError,
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, TextError),
                shape = RoundedCornerShape(28.dp),
            ) {
                Text(text = stringResource(R.string.transaction_delete))
            }
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
            .background(color = Color(0xFFF6F6F6), shape = RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        HorizontalDivider(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp))
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

private fun Long.toDateLabel(): String {
    val dt = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
    return dt.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
}

@Composable
fun Long.toAmountString(): String {
    val format = NumberFormat.getInstance(Locale.GERMANY)
    val s = format.format(this)
    return "$s ${LocalCurrencySymbol.current}"
}

@Preview
@Composable
private fun TransactionDetailScreenPreview() {
    MoneySavingTheme {
        TransactionDetailContent (
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
            onBack = {},
            onEdit = {},
            onDelete = {},
        )
    }
}
