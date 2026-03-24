package com.kkkk.moneysaving.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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

data class StatItemUI(
    val id: String,
    val categoryName: String,
    val categoryColor: Long,
    val amount: Long,
    val process: Float,
    @param:DrawableRes val categoryIcon: Int,
)

@Composable
fun StatItemCard(
    item: StatItemUI,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(item.categoryIcon),
            contentDescription = null,
            modifier = Modifier.size(46.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )

                Text(
                    text = item.amount.toAmountString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (item.amount < 0) TextError else TextPositive,
                )
            }

            LinearProgressIndicator(
                progress = { item.process },
                gapSize = 0.dp,
                drawStopIndicator = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(item.categoryColor),
                trackColor = Color(item.categoryColor).copy(alpha = 0.15f),
            )
        }
    }
}