package com.kkkk.moneysaving.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kkkk.moneysaving.ui.feature.transaction.detail.toAmountString
import com.kkkk.moneysaving.ui.theme.TextError
import com.kkkk.moneysaving.ui.theme.TextPositive
import com.kkkk.moneysaving.ui.theme.TextPrimary
import com.kkkk.moneysaving.ui.theme.TextSecondary
import java.time.format.DateTimeFormatter

data class TransactionItemUI(
    val id: String,
    val categoryName: String,
    val note: String?,
    val amount: Long,
    val occurredAt: Long,
    @param:DrawableRes val categoryIcon: Int,
)

@Composable
fun TransactionItemCard(
    item: TransactionItemUI,
    onClick: (String) -> Unit,

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
            .clickable { onClick(item.id) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(item.categoryIcon),
                contentDescription = null,
                modifier = Modifier.size(46.dp)
            )
            Column {
                Text(
                    text = item.categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Text(
                    text = item.note.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = item.occurredAt.toTimeString(),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
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

fun Long.toTimeString(): String {
    val dt = java.time.Instant.ofEpochMilli(this)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalTime()
    return dt.format(DateTimeFormatter.ofPattern("HH:mm"))
}