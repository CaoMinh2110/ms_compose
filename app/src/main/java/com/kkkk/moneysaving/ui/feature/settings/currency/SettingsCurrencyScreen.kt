package com.kkkk.moneysaving.ui.feature.settings.currency

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.LocalScreenPadding
import com.kkkk.moneysaving.ui.components.CurrencyItemCard
import com.kkkk.moneysaving.ui.feature.currency.CurrencyViewModel
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.TextPrimary

@Composable
fun SettingsCurrencyScreen(
    onBack: () -> Unit,
    viewModel: CurrencyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

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
                IconButton(onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBackIos,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.currency_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                )
                IconButton({ viewModel.persistSelection() }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
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
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(uiState.currencies) {
                    CurrencyItemCard(
                        currency = it,
                        isSelected = it.code == uiState.selectedCode,
                        onClick = { code -> viewModel.select(code) }
                    )
                }
            }
        }
    }
}
