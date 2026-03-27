package com.kkkk.moneysaving.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kkkk.moneysaving.util.formatCurrencyAmount
import com.kkkk.moneysaving.ui.theme.TextError
import com.kkkk.moneysaving.ui.theme.TextPositive
import com.kkkk.moneysaving.ui.theme.TextPrimary
import com.kkkk.moneysaving.ui.theme.TextSecondary
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
    onClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
    showDelete: Boolean = false
) {
    val actionWidth = remember { Animatable(0f) }
    var rowHeight by remember { mutableFloatStateOf(0f) }
    val maxWidthPx = with(LocalDensity.current) { 50.dp.toPx() }

    val heightPx = remember(actionWidth.value, rowHeight) {
        val ratio = (actionWidth.value / maxWidthPx).coerceIn(0f, 1f)
        val eased = FastOutSlowInEasing.transform(ratio)
        rowHeight * eased
    }

    val spacerWidth by animateDpAsState(
        if (actionWidth.value > 0f) 8.dp else 0.dp,
        label = "spacer_animation"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                .onSizeChanged { rowHeight = it.height.toFloat() }
                .clickable(enabled = actionWidth.value == 0f || heightPx == rowHeight) {
                    onClick(
                        item.id
                    )
                }
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .pointerInput(Unit) {
                    if (showDelete) {
                        coroutineScope {
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { _, dragAmount ->
                                    val newValue =
                                        (actionWidth.value - dragAmount).coerceIn(0f, maxWidthPx)

                                    launch { actionWidth.snapTo(newValue) }
                                },
                                onDragEnd = {
                                    val shouldExpand = actionWidth.value >= maxWidthPx * 0.5f

                                    launch { actionWidth.animateTo(if (shouldExpand) maxWidthPx else 0f) }
                                }
                            )
                        }
                    }
                },
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
                    text = item.amount.formatCurrencyAmount(),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (item.amount < 0) TextError else TextPositive,
                )
            }
        }

        Spacer(modifier = Modifier.width(spacerWidth))

        // 🔴 Delete button
        Box(
            modifier = Modifier
                .width(with(LocalDensity.current) { actionWidth.value.toDp() })
                .height(with(LocalDensity.current) { heightPx.toDp() })
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Red)
                .clickable { onDeleteClick(item.id) },
            contentAlignment = Alignment.Center
        ) {
            if (actionWidth.value > maxWidthPx * 0.4f) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

fun Long.toTimeString(): String {
    val dt = java.time.Instant.ofEpochMilli(this)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalTime()
    return dt.format(DateTimeFormatter.ofPattern("HH:mm"))
}
