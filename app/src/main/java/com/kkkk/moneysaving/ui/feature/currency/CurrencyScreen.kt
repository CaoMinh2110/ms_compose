package com.kkkk.moneysaving.ui.feature.currency

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.Currency
import com.kkkk.moneysaving.ui.components.CurrencyItemCard
import com.kkkk.moneysaving.ui.theme.Secondary

@Composable
fun CurrencyScreen(
    onContinue: () -> Unit,
    viewModel: CurrencyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CurrencyContent(
        uiState = uiState,
        onCurrencySelect = viewModel::select,
        onContinue = {
            viewModel.persistSelection()
            onContinue()
        }
    )
}

@Composable
private fun CurrencyContent(
    uiState: CurrencyUiState,
    onCurrencySelect: (String) -> Unit,
    onContinue: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Secondary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp)
                .navigationBarsPadding(),
        ) {
            Text(
                text = stringResource(R.string.title_currency),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(18.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(uiState.currencies) { currency ->
                    CurrencyItemCard(
                        currency,
                        uiState.selectedCode == currency.code,
                        onClick = { onCurrencySelect(currency.code) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Secondary,
                ),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.title_continue),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CurrencyPreview() {
    MaterialTheme {
        CurrencyContent(
            uiState = CurrencyUiState(
                currencies = listOf(
                    Currency("USD", "United State Dollar", "$", R.drawable.ic_flag_gb),
                    Currency("USD", "United State Dollar", "$", R.drawable.ic_flag_gb),
                    Currency("USD", "United State Dollar", "$", R.drawable.ic_flag_gb),
                ),
                selectedCode = "VND"
            ),
            onCurrencySelect = {},
            onContinue = {}
        )
    }
}
