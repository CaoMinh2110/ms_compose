package com.kkkk.moneysaving.ui.feature.statistics.balance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.components.DialogBody
import com.kkkk.moneysaving.ui.components.ITEM_HEIGHT
import com.kkkk.moneysaving.ui.components.ScrollPicker
import com.kkkk.moneysaving.ui.components.SelectedBg
import java.time.Year

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SetYearDialog(
    initialYear: Int = Year.now().value,
    onDismiss: () -> Unit = {},
    onSave: (year: Int) -> Unit = {},
) {
    val now = Year.now()
    val years = (now.value - 20..now.value).map { it.toString().padStart(4, '0') }

    var selectedYear by remember { mutableIntStateOf(initialYear) }

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
                        items = years,
                        selectedIndex = selectedYear,
                        onItemSelected = { selectedYear = it },
                        modifier = modifier,
                    )
                }
            }
        },
        onDismiss = onDismiss,
        onSave = { onSave(selectedYear) }
    )
}