package com.kkkk.moneysaving.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.Secondary
import com.kkkk.moneysaving.ui.theme.TextPrimary
import com.kkkk.moneysaving.ui.theme.TextSecondary
import kotlinx.coroutines.launch

const val VISIBLE_ITEMS = 3
val ITEM_HEIGHT = 48.dp
val SelectedBg = Color(0xFFF0FAF9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogBody(
    @StringRes title: Int?,
    trailing: @Composable (Modifier) -> Unit,
    showButton: Boolean = true,
    onDismiss: () -> Unit = {},
    onSave: () -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = Color.White,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            if(title != null) {
                Text(
                    text = stringResource(title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
                )

                HorizontalDivider(color = TextSecondary)
            }

            trailing(Modifier.weight(1f))

            if(showButton) {
                HorizontalDivider(color = TextSecondary)

                DialogButtons(
                    onCancel = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    onSave = {
                        scope.launch {
                            sheetState.hide()
                            onSave()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DialogButtons(
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        // Cancel
        OutlinedButton(
            onClick = onCancel,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, TextSecondary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
        ) {
            Text(
                text = stringResource(R.string.title_cancel),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Save
        Button(
            onClick = onSave,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
        ) {
            Text(
                text = stringResource(R.string.title_save),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}


@Composable
fun ScrollPicker(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedIndex) {
        if (listState.firstVisibleItemIndex != selectedIndex) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val newIndex = listState.firstVisibleItemIndex
            if (newIndex in items.indices && newIndex != selectedIndex) {
                onItemSelected(newIndex)
            }
        }
    }

    Box(
        modifier = modifier.height(ITEM_HEIGHT * VISIBLE_ITEMS),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            item { Spacer(Modifier.height(ITEM_HEIGHT)) }

            items(items.size) { index ->
                val isSelected = index == selectedIndex
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ITEM_HEIGHT)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onItemSelected(index)
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        },
                ) {
                    SelectableText(
                        text = items[index],
                        isSelected = isSelected,
                    )
                }
            }

            item { Spacer(Modifier.height(ITEM_HEIGHT)) }
        }
    }
}

@Composable
fun SelectableText(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean
) {
    Text(
        text = text,
        fontSize = if (isSelected) 18.sp else 16.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) Secondary else TextSecondary,
        modifier = modifier,
    )
}