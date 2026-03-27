package com.kkkk.moneysaving.ui.feature.transaction.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.ui.components.BudgetIcon
import com.kkkk.moneysaving.ui.components.DialogBody
import com.kkkk.moneysaving.ui.components.ITEM_HEIGHT
import com.kkkk.moneysaving.ui.components.ScrollPicker
import com.kkkk.moneysaving.ui.components.SelectableText
import com.kkkk.moneysaving.ui.components.SelectedBg
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.time.YearMonth

// ══════════════════════════════════════════════════════════════
// 1.  SET AMOUNT DIALOG
// ══════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun BudgetDialog(
    initializeId: String? = null,
    options: List<Budget> = emptyList(),
    onDismiss: () -> Unit = {},
    onSave: (String?) -> Unit = {},
) {
    var selectedId by remember { mutableStateOf(initializeId) }

    DialogBody(
        title = R.string.title_budget,
        trailing = {
            LazyColumn {
                item {
                    BudgetDialogItem(
                        trailing = {
                            Icon(
                                imageVector = Icons.Default.NotInterested,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        },
                        id = null,
                        name = "None",
                        isSelected = selectedId == null,
                        onClick = { selectedId = it }
                    )
                }

                items(options, key = { it.id }) { budget ->
                    BudgetDialogItem(
                        trailing = {
                            BudgetIcon(
                                iconSize = 40f,
                                budget = budget,
                                remainingPercent = 1f,
                                showRemaining = false,
                            )
                        },
                        id = budget.id,
                        name = budget.name,
                        isSelected = selectedId == budget.id,
                        onClick = { selectedId = it }
                    )
                }
            }
        },
        onDismiss = onDismiss,
        onSave = {
            onSave(selectedId)
            onDismiss()
        }
    )
}

@Composable
private fun BudgetDialogItem(
    trailing: @Composable () -> Unit,
    id: String?,
    name: String = "",
    isSelected: Boolean = false,
    onClick: (String?) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(id) }
            .background(if (isSelected) SelectedBg else Color.Transparent)
            .padding(horizontal = 24.dp, vertical = 14.dp),
    ) {
        trailing()

        Spacer(Modifier.width(16.dp))

        SelectableText(
            text = name,
            isSelected = isSelected,
            modifier = Modifier.weight(1f),
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════
// 2.  TIME PICKER DIALOG  (HH : MM)
// ══════════════════════════════════════════════════════════════

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SetTimeDialog(
    initialHour: Int = LocalTime.now().hour,
    initialMinute: Int = LocalTime.now().minute,
    onDismiss: () -> Unit = {},
    onSave: (hour: Int, minute: Int) -> Unit = { _, _ -> },
) {
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }

    val hours = (0..23).map { it.toString().padStart(2, '0') }
    val minutes = (0..59).map { it.toString().padStart(2, '0') }

    DialogBody(
        title = R.string.title_editor_time,
        trailing = { modifier ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ITEM_HEIGHT)
                        .background(SelectedBg)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    ScrollPicker(
                        items = hours,
                        selectedIndex = selectedHour,
                        onItemSelected = { selectedHour = it },
                        modifier = modifier,
                    )

                    Text(
                        text = ":",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )

                    ScrollPicker(
                        items = minutes,
                        selectedIndex = selectedMinute,
                        onItemSelected = { selectedMinute = it },
                        modifier = modifier,
                    )
                }
            }
        },
        onDismiss = onDismiss,
        onSave = {
            onSave(selectedHour, selectedMinute)
            onDismiss()
        }
    )
}

// ══════════════════════════════════════════════════════════════
// 3.  DATE PICKER DIALOG  (DD / MM / YYYY)
// ══════════════════════════════════════════════════════════════

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SetDateDialog(
    initialDay: Int = LocalDate.now().dayOfMonth,
    initialMonth: Int = LocalDate.now().monthValue,
    initialYear: Int = LocalDate.now().year,
    onDismiss: () -> Unit = {},
    onSave: (day: Int, month: Int, year: Int) -> Unit = { _, _, _ -> },
) {
    val currentYear = Year.now().value
    val years = (currentYear - 20..currentYear).map { it.toString() }
    val months = (1..12).map { it.toString().padStart(2, '0') }

    var selectedYearIndex by remember {
        mutableIntStateOf(
            (initialYear - (currentYear - 20)).coerceIn(
                0,
                years.size - 1
            )
        )
    }
    var selectedMonthIndex by remember {
        mutableIntStateOf(
            (initialMonth - 1).coerceIn(
                0,
                months.size - 1
            )
        )
    }

    val daysInMonth =
        YearMonth.of(years[selectedYearIndex].toInt(), months[selectedMonthIndex].toInt())
            .lengthOfMonth()
    val days = (1..daysInMonth).map { it.toString().padStart(2, '0') }

    var selectedDayIndex by remember {
        mutableIntStateOf(
            (initialDay - 1).coerceIn(
                0,
                daysInMonth - 1
            )
        )
    }

    // Ensure selectedDayIndex is valid if daysInMonth changes
    LaunchedEffect(daysInMonth) {
        if (selectedDayIndex >= daysInMonth) {
            selectedDayIndex = daysInMonth - 1
        }
    }

    DialogBody(
        title = R.string.title_editor_date,
        trailing = { modifier ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ITEM_HEIGHT)
                        .background(SelectedBg)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    ScrollPicker(
                        items = days,
                        selectedIndex = selectedDayIndex,
                        onItemSelected = { selectedDayIndex = it },
                        modifier = modifier,
                    )

                    Text(
                        text = ":",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )

                    ScrollPicker(
                        items = months,
                        selectedIndex = selectedMonthIndex,
                        onItemSelected = { selectedMonthIndex = it },
                        modifier = modifier,
                    )

                    Text(
                        text = ":",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )

                    ScrollPicker(
                        items = years,
                        selectedIndex = selectedYearIndex,
                        onItemSelected = { selectedYearIndex = it },
                        modifier = modifier,
                    )
                }
            }
        },
        onDismiss = onDismiss,
        onSave = {
            onSave(
                days[selectedDayIndex].toInt(),
                months[selectedMonthIndex].toInt(),
                years[selectedYearIndex].toInt()
            )
            onDismiss()
        }
    )
}

// ══════════════════════════════════════════════════════════════
// SHARED: Time Dialog Structure
// ══════════════════════════════════════════════════════════════
