package com.kkkk.moneysaving.ui.feature.transaction.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.LocalScreenPadding
import com.kkkk.moneysaving.ui.components.toTimeString
import com.kkkk.moneysaving.ui.feature.transaction.detail.toAmountString
import com.kkkk.moneysaving.ui.theme.TextError
import com.kkkk.moneysaving.ui.theme.TextPositive
import com.kkkk.moneysaving.ui.theme.TextPrimary

@Composable
fun TransactionSearchScreen(
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit,
    viewModel: TransactionSearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                IconButton(onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBackIos,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = viewModel::updateQuery,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.transaction_search_title),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF939393),
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF939393),
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.items.isEmpty()) {
                TransactionSearchEmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                ) {
                    items(uiState.items) { item ->
                        TransactionSearchItemRow(
                            item = item,
                            onClick = { onOpenDetail(item.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionSearchEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        contentAlignment = Alignment.Center,
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
                text = stringResource(R.string.transaction_search_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
            )
        }
    }
}

@Composable
private fun TransactionSearchItemRow(
    item: TransactionSearchItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(color = Color(item.categoryColor), shape = CircleShape),
            )
            Column {
                Text(
                    text = item.categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                item.note?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF939393),
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = item.occurredAt.toTimeString(),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF939393),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.amount.toAmountString(),
                style = MaterialTheme.typography.titleMedium,
                color = if (item.amount < 0) TextError else TextPositive,
            )
        }
    }
}

