package com.kkkk.moneysaving.ui.feature.transaction.editor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.Category
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.ui.LocalCurrencySymbol
import com.kkkk.moneysaving.ui.components.SegmentedTab
import com.kkkk.moneysaving.ui.theme.AppColor
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.TextBudgetDark
import com.kkkk.moneysaving.ui.theme.TextError
import com.kkkk.moneysaving.ui.theme.TextPrimary
import com.kkkk.moneysaving.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val Radius = RoundedCornerShape(12.dp)

@Composable
fun TransactionEditorScreen(
    onBack: () -> Unit,
    viewModel: TransactionEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White,
    ) {
        TransactionEditorContent(
            uiState = uiState,
            onBack = onBack,
            onSave = { viewModel.save(it, onSaved = onBack) },
            onTypeSelected = viewModel::selectType,
            onAmountChanged = viewModel::updateAmount,
            onCategorySelected = viewModel::selectCategory,
            onNoteChanged = viewModel::updateNote,
            onBorrowerChanged = viewModel::updateBorrower,
            onDateChanged = viewModel::updateDate,
            onTimeChanged = viewModel::updateTime,
        )
    }
}

@Preview
@Composable
private fun TransactionEditorContent(
    uiState: TransactionEditorUiState = TransactionEditorUiState(),
    onBack: () -> Unit = {},
    onSave: (String?) -> Unit = {},
    onTypeSelected: (CategoryType) -> Unit = {},
    onAmountChanged: (String) -> Unit = {},
    onCategorySelected: (String) -> Unit = {},
    onNoteChanged: (String) -> Unit = {},
    onBorrowerChanged: (String) -> Unit = {},
    onDateChanged: (LocalDate) -> Unit = {},
    onTimeChanged: (LocalTime) -> Unit = {},
) {
    var showBudget by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }
    var showDate by remember { mutableStateOf(false) }

    val column = 4

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor)
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item(span = { GridItemSpan(column) }) {
                EditorTopBar(
                    onCancel = onBack,
                    onNext = { showBudget = true },
                )
            }

            item(span = { GridItemSpan(column) }) {
                SegmentedTab(
                    selected = uiState.selectedType,
                    onSelectedChange = onTypeSelected,
                )
            }

            item(span = { GridItemSpan(column) }) {
                EditorAmountSection(
                    value = uiState.amount,
                    error = uiState.amountError,
                    onValueChange = onAmountChanged,
                )
            }

            item(span = { GridItemSpan(column) }) {
                Text(
                    text = stringResource(R.string.title_editor_category),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextSecondary,
                )
            }

            items(uiState.filteredCategories, key = { it.id }) {
                EditorCategoryItem(
                    item = it,
                    selected = it.id == uiState.selectedCategoryId,
                    onClick = { onCategorySelected(it.id) },
                )
            }

            item(span = { GridItemSpan(column) }) {
                AnimatedContent(
                    targetState = uiState.selectedType == CategoryType.LOAN,
                    transitionSpec = {
                        if (targetState) {
                            (slideInVertically(initialOffsetY = { it }) + fadeIn()).togetherWith(
                                fadeOut() + slideOutVertically(targetOffsetY = { -it })
                            )
                        } else {
                            (fadeIn() + slideInVertically(initialOffsetY = { -it })).togetherWith(
                                fadeOut() + slideOutVertically(targetOffsetY = { it })
                            )
                        }.using(SizeTransform(clip = false))
                    }
                ) { visible ->
                    if (visible) {
                        EditorBorrowerSection(
                            value = uiState.borrower,
                            onValueChange = onBorrowerChanged,
                        )
                    }
                }
            }

            item(span = { GridItemSpan(column) }) {
                EditorNoteSection(
                    value = uiState.note,
                    onValueChange = onNoteChanged,
                )
            }

            item(span = { GridItemSpan(column) }) {
                EditorDateTimeSection(
                    uiState.selectedTime,
                    onDateClick = { showDate = true },
                    onTimeClick = { showTime = true }
                )
            }
        }

        if (showBudget) {
            BudgetDialog(
                initializeId = uiState.selectedBudgetId,
                options = uiState.allBudget,
                onDismiss = { showBudget = false },
                onSave = { budget ->
                    onSave(budget)
                    showBudget = false
                },
            )
        }

        if (showTime) {
            SetTimeDialog(
                onDismiss = { showTime = false },
                onSave = { h, m ->
                    onTimeChanged(LocalTime.of(h, m))
                    showTime = false
                },
            )
        }

        if (showDate) {
            SetDateDialog(
                onDismiss = { showDate = false },
                onSave = { day, month, year ->
                    onDateChanged(LocalDate.of(year, month, day))
                    showDate = false
                },
            )
        }
    }
}

@Composable
private fun EditorTopBar(
    onCancel: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp, top = 40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColor,
                contentColor = TextPrimary,
            ),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            Text(
                text = stringResource(R.string.title_cancel),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColor,
                contentColor = TextPrimary,
            ),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            Text(
                text = stringResource(R.string.title_next),
                style = MaterialTheme.typography.bodyMedium,
                color = TextBudgetDark,
            )
        }
    }
}


@Composable
private fun EditorAmountSection(
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = stringResource(R.string.title_editor_amount),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = if (error != null) TextError else TextSecondary,
            )
            if (error != null) {
                Text(
                    text = stringResource(R.string.error_editor_amount_invalid),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextError,
                )
            }
        }
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = value,
            isError = error != null,
            onValueChange = { input ->
                val digitsOnly = input.filter { it.isDigit() }

                val processedValue = when {
                    digitsOnly.isEmpty() -> ""
                    digitsOnly.startsWith("0") && digitsOnly.length > 1 -> {
                        digitsOnly.dropWhile { it == '0' }.ifEmpty { "0" }
                    }

                    else -> digitsOnly
                }

                onValueChange(processedValue)
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.titleLarge,
            shape = Radius,
            singleLine = true,
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
    }
}


@Composable
private fun EditorCategoryItem(
    item: Category,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) Primary else Color.Transparent

    Column(
        modifier = Modifier
            .clip(Radius)
            .clickable(onClick = onClick)
            .background(color = Color.Transparent, shape = Radius)
            .border(border = BorderStroke(1.dp, borderColor), shape = Radius)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Image(
            painter = painterResource(item.icon),
            contentDescription = null,
            modifier = Modifier.size(58.dp)
        )
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) TextPrimary else TextSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun EditorBorrowerSection(
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.title_editor_borrower),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = TextSecondary,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.hint_transaction_name),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            },
            shape = Radius,
            singleLine = true,
        )
    }
}

@Composable
private fun EditorNoteSection(
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.title_editor_note),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = TextSecondary,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.hint_note),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            },
            shape = Radius,
            singleLine = true,
        )
    }
}

@Composable
private fun EditorDateTimeSection(
    currentTime: LocalDateTime,
    onTimeClick: () -> Unit = {},
    onDateClick: () -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.title_editor_date),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(4.dp, shape = Radius)
                    .clip(Radius)
                    .clickable { onDateClick() }
                    .height(44.dp)
                    .background(color = Color(0xFFF6F6F6), shape = Radius),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = currentTime.toDateString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                )
            }
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .shadow(4.dp, shape = Radius)
                    .clip(Radius)
                    .clickable { onTimeClick() }
                    .height(44.dp)
                    .background(color = Color(0xFFF6F6F6), shape = Radius),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = currentTime.toTimeString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                )
            }
        }
    }
}

fun LocalDateTime.toTimeString(): String = this.format(DateTimeFormatter.ofPattern("HH:mm"))
fun LocalDateTime.toDateString(): String = this.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))