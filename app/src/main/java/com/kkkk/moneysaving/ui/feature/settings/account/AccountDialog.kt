package com.kkkk.moneysaving.ui.feature.settings.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.theme.TextSecondary
import kotlinx.coroutines.launch

// =======================
// 1. MENU ITEM MODEL
// =======================
data class MenuItem(
    val title: String,
    val icon: Any,
    val onClick: () -> Unit
)

// =======================
// 2. BASE BOTTOM SHEET
// =======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseMenuBottomSheet(
    containerColor: Color = Color.White,
    rippleColor: Color? = null,
    items: List<MenuItem>,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        containerColor = containerColor,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items.forEach { item ->
                DialogItem(
                    item = item,
                    sheetState = sheetState,
                    onDismiss = onDismiss,
                    rippleColor = rippleColor
                )
            }
        }
    }
}

// =======================
// 3. DIALOG ITEM
// =======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogItem(
    item: MenuItem,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    rippleColor: Color? = null
) {
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = rippleColor ?: Color.Unspecified),
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        item.onClick()
                        onDismiss()
                    }
                }
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        when (item.icon) {
            is ImageVector -> {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            is Int -> {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
    }
}

// =======================
// 4. FULLSCREEN MENU
// =======================
@Composable
fun FullscreenMenuDialog(
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    onEdit: () -> Unit,
) {

    val items = listOf(
        MenuItem(
            title = stringResource(R.string.title_device_save),
            icon = R.drawable.ic_download,
            onClick = onDownload
        ),
        MenuItem(
            title = stringResource(R.string.title_share),
            icon = R.drawable.ic_share,
            onClick = onShare
        ),
        MenuItem(
            title = stringResource(R.string.title_edit_avatar),
            icon = R.drawable.ic_edit_square,
            onClick = onEdit
        )
    )

    BaseMenuBottomSheet(
        containerColor = Color(0xFF1F1F1F),
        rippleColor = Color.White,
        items = items,
        onDismiss = onDismiss
    )
}

// =======================
// 5. ACCOUNT MENU
// =======================
@Composable
fun AccountMenuDialog(
    onDismiss: () -> Unit,
    onViewImage: () -> Unit,
    onEdit: () -> Unit,
) {

    val items = listOf(
        MenuItem(
            title = stringResource(R.string.title_show_avatar),
            icon = Icons.Default.Image,
            onClick = onViewImage
        ),
        MenuItem(
            title = stringResource(R.string.title_edit_avatar),
            icon = R.drawable.ic_edit_square,
            onClick = onEdit
        )
    )

    BaseMenuBottomSheet(
        items = items,
        onDismiss = onDismiss
    )
}
