package com.kkkk.moneysaving.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kkkk.moneysaving.domain.model.Currency
import com.kkkk.moneysaving.ui.theme.AppColor
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.TextPrimary

@Composable
fun CurrencyItemCard(
    currency: Currency,
    isSelected: Boolean,
    onClick: (String) -> Unit = {}
) {
    Surface(
        onClick = { onClick(currency.code) },
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Primary else AppColor,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(currency.iconResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = CircleShape),
            )
            Text(
                text = "${currency.code} - ${currency.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) Color.White else TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = currency.symbol,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) Color.White else TextPrimary,
            )
        }
    }
}