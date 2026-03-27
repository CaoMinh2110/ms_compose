package com.kkkk.moneysaving.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.ui.theme.Primary

@Preview
@Composable
fun SegmentedTab(
    modifier: Modifier = Modifier,
    selected: CategoryType = CategoryType.EXPENSE,
    onSelectedChange: (CategoryType) -> Unit = {},
) {
    val radius = 20.dp
    val padding = 5.dp
    val selectedIndex = selected.ordinal

    val tabs = listOf(
        stringResource(id = R.string.title_expense),
        stringResource(id = R.string.title_income),
        stringResource(id = R.string.title_loan)
    )

    val tabIcon = listOf(
        painterResource(id = R.drawable.bg_tab_segment_0),
        painterResource(id = R.drawable.bg_tab_segment_1),
        painterResource(id = R.drawable.bg_tab_segment_2)
    )

    val backgroundColor = Color(0xFFEAF4F7)
    val selectedColor = Primary

    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier
            .height(56.dp)
            .shadow(4.dp, RoundedCornerShape(radius))
            .clip(RoundedCornerShape(radius))
            .background(backgroundColor),
        containerColor = Color.Transparent,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedIndex])
                    .padding(padding)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(radius - padding))
                    .background(selectedColor)
            ) {
                Image(
                    painter = tabIcon[selectedIndex],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.BottomEnd,
                )
            }
        },
        divider = { }
    ) {
        tabs.forEachIndexed { index, title ->
            Box(modifier = Modifier.zIndex(1f)) {
                Tab(
                    selected = selectedIndex == index,
                    onClick = { onSelectedChange(CategoryType.entries[index]) },
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(radius - padding))
                        .background(Color.Transparent),
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.Black,
                    text = { Text(title, style = MaterialTheme.typography.bodyMedium) }
                )

                val hideDivider =
                    index == selectedIndex || index == selectedIndex - 1

                if (index < tabs.lastIndex && !hideDivider) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(1.dp)
                            .height(35.dp)
                            .background(Color.Gray.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}
