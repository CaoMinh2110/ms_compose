package com.kkkk.moneysaving.ui.feature.budget.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.LocalCurrencySymbol
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.TextPrimary
import com.kkkk.moneysaving.ui.theme.TextSecondary

@Composable
fun BudgetEditorDialog(
    budgetId: String? = null,
    viewModel: BudgetEditorViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(budgetId) {
        viewModel.loadBudget(budgetId)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = if (budgetId == null) stringResource(R.string.title_budget_add) else stringResource(
                        R.string.title_budget_edit
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = stringResource(R.string.hint_budget_name)) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                )
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    value = uiState.amount,
                    onValueChange = { input ->
                        val digitsOnly = input.filter { it.isDigit() }

                        val processedValue = when {
                            digitsOnly.isEmpty() -> ""
                            digitsOnly.startsWith("0") && digitsOnly.length > 1 -> {
                                digitsOnly.dropWhile { it == '0' }.ifEmpty { "0" }
                            }

                            else -> digitsOnly
                        }

                        viewModel.updateAmount(processedValue)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = stringResource(R.string.hint_budget_amount)) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            VerticalDivider(
                                modifier = Modifier
                                    .height(24.dp)
                                    .padding(horizontal = 8.dp),
                                thickness = 1.dp,
                                color = Color.LightGray
                            )

                            Text(
                                text = LocalCurrencySymbol.current,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEAF4F7),
                            contentColor = Primary,
                        ),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.title_cancel),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Button(
                        onClick = { viewModel.save(onSaved = onDismiss) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.White,
                        ),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.title_save),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
