package com.kkkk.moneysaving.ui.feature.settings.rate

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.TextPrimary
import kotlinx.coroutines.delay

private data class Content(
    val emoji: String,
    @param:StringRes val message: Int,
)

@Preview
@Composable
fun RateDialog(
    onDismiss: () -> Unit = {},
    onRate: (Int) -> Unit = {},
) {
    var rating by remember { mutableIntStateOf(0) }
    val contents = listOf(
        Content("😊", R.string.message_rate_0),
        Content("😭", R.string.message_rate_1),
        Content("😢", R.string.message_rate_1),
        Content("😟", R.string.message_rate_1),
        Content("\uD83D\uDE01", R.string.message_rate_2),
        Content("😁", R.string.message_rate_2),
    )
    val interactionSource = remember { MutableInteractionSource() }
    var pressedIndex by remember { mutableIntStateOf(-1) }
    val duration = 150
    val emojiScale by animateFloatAsState(
        targetValue = if (pressedIndex != -1) 1.2f else 1f,
        animationSpec = tween(duration),
        label = ""
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = contents[rating].emoji,
                    fontSize = 50.sp,
                    fontFamily = FontFamily(Font(R.font.emoji_regular)),
                    modifier = Modifier.graphicsLayer {
                        scaleX = emojiScale
                        scaleY = emojiScale
                    }
                )
                Text(
                    text = stringResource(contents[rating].message),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(5) { index ->
                        val filled = index < rating
                        val isPressed = pressedIndex == index

                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 1.3f else 1f,
                            animationSpec = tween(duration),
                            label = ""
                        )

                        val color by animateColorAsState(
                            targetValue = if (filled) Primary else Color(0xFFE0E0E0),
                            animationSpec = tween(duration),
                            label = ""
                        )

                        LaunchedEffect(isPressed) {
                            if (isPressed) {
                                delay(150)
                                pressedIndex = -1
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier
                                .size(26.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable(
                                    enabled = rating != index + 1,
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    rating = index + 1
                                    pressedIndex = index
                                }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                            text = stringResource(R.string.title_rate_not_now),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Button(
                        onClick = { onRate(rating) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.White,
                        ),
                        shape = RoundedCornerShape(14.dp),
                        enabled = rating > 0,
                    ) {
                        Text(
                            text = stringResource(R.string.title_rate),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

