package com.kkkk.moneysaving.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.ui.theme.AppColor
import com.kkkk.moneysaving.ui.theme.TextBudgetDark
import com.kkkk.moneysaving.ui.theme.TextBudgetLight

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BudgetIcon(
    iconSize: Float,
    budget: Budget,
    remainingPercent: Float,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    showRemaining: Boolean = true,
    showShadow: Boolean = true,
) {
    val baseHeight = 170f
    val baseWidth = 145f
    val scale = iconSize / baseHeight

    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .then(
                    if (this != null && animatedVisibilityScope != null) {
                        Modifier.sharedElement(
                            rememberSharedContentState(key = "budget_icon_${budget.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    } else Modifier
                )
                .size(width = (baseWidth * scale).dp, height = (baseHeight * scale).dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .requiredSize(width = baseWidth.dp, height = baseHeight.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                contentAlignment = Alignment.TopCenter
            ) {
                val border = 12f
                val capGap = 16f
                val bottleRadius = 50f
                val capWidth = 67f

                Box(modifier = Modifier.padding(top = capGap.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(
                                (if (showShadow) capGap * scale else 0f).dp,
                                RoundedCornerShape(bottleRadius.dp)
                            )
                            .border(border.dp, AppColor, RoundedCornerShape(bottleRadius.dp))
                            .clip(RoundedCornerShape(bottleRadius.dp))
                            .background(Color(0xFFE7E7E7), RoundedCornerShape(bottleRadius.dp))
                            .padding(border.dp)
                    ) {
                        val fillHeight = (baseHeight - capGap - border * 2) * remainingPercent

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .height(fillHeight.dp)
                                .fillMaxWidth()
                                .background(
                                    Color(budget.color),
                                    RoundedCornerShape(
                                        topStart = (bottleRadius * remainingPercent / 2f).dp,
                                        topEnd = (bottleRadius * remainingPercent / 2f).dp,
                                    )
                                )
                        )

                        if (showRemaining) {
                            Text(
                                text = "${(remainingPercent * 100).toInt()} %",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .graphicsLayer {
                                        scaleX = 1f / scale
                                        scaleY = 1f / scale
                                    },
                                style = MaterialTheme.typography.bodySmall,
                                color = getContrastTextColor(Color(budget.color)),
                            )
                        }
                    }
                }

                Box {
                    Box(
                        modifier = Modifier
                            .shadow(
                                (if (showShadow) (capGap - border) * scale else 0f).dp,
                                CircleShape
                            )
                            .width(capWidth.dp)
                            .height(border.dp)
                            .clip(CircleShape)
                            .background(AppColor)
                    )
                }
            }
        }
    }
}

private fun getContrastTextColor(background: Color): Color {
    val luminance = ColorUtils.calculateLuminance(background.toArgb())
    return if (luminance < 0.5) TextBudgetDark else TextBudgetLight
}
