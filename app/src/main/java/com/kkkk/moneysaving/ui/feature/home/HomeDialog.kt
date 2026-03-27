package com.kkkk.moneysaving.ui.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.components.DialogBody
import com.kkkk.moneysaving.ui.components.ITEM_HEIGHT
import com.kkkk.moneysaving.ui.components.ScrollPicker
import com.kkkk.moneysaving.ui.components.SelectedBg
import com.kkkk.moneysaving.ui.theme.Primary
import java.time.Year
import java.time.YearMonth

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SetMonthDialog(
    initialMonth: Int = YearMonth.now().monthValue,
    initialYear: Int = Year.now().value,
    onDismiss: () -> Unit = {},
    onSave: (month: Int, year: Int) -> Unit = { _, _ -> },
) {
    val months = remember { (1..12).map { it.toString().padStart(2, '0') } }
    val currentYear = remember { Year.now().value }
    val years = remember(currentYear) { (currentYear - 20..currentYear).map { it.toString() } }

    var selectedMonth by remember { mutableIntStateOf((initialMonth - 1).coerceIn(0, 11)) }
    var selectedYear by remember {
        val index = years.indexOf(initialYear.toString())
        mutableIntStateOf(if (index != -1) index else years.size - 1)
    }

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
                        items = months,
                        selectedIndex = selectedMonth,
                        onItemSelected = { selectedMonth = it },
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
                        selectedIndex = selectedYear,
                        onItemSelected = { selectedYear = it },
                        modifier = modifier,
                    )
                }
            }
        },
        onDismiss = onDismiss,
        onSave = {
            onSave(months[selectedMonth].toInt(), years[selectedYear].toInt())
            onDismiss()
        }
    )
}