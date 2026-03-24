package com.kkkk.moneysaving.ui.feature.settings.sync

import android.R.attr.contentDescription
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.theme.TextPrimary

@Composable
fun SyncDialog(
    mode: SyncDialogMode,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
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
                Image(
                    painter = painterResource(if (mode == SyncDialogMode.ConfirmBackup) R.drawable.ic_sync_confirm else R.drawable.ic_sync_success),
                    contentDescription = null,
                    modifier = Modifier.size(54.dp),
                )
                Text(
                    text = stringResource(if (mode == SyncDialogMode.ConfirmBackup) R.string.sync_backup_title else R.string.sync_done_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                if (mode == SyncDialogMode.ConfirmBackup) {
                    Text(
                        text = stringResource(R.string.sync_backup_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF939393),
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (mode == SyncDialogMode.ConfirmBackup) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEAF4F7),
                                contentColor = Color(0xFF1B4B59),
                            ),
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Text(text = stringResource(R.string.sync_later))
                        }
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1B4B59),
                                contentColor = Color.White,
                            ),
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Text(text = stringResource(R.string.sync_yes))
                        }
                    } else {
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1B4B59),
                                contentColor = Color.White,
                            ),
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Text(text = stringResource(R.string.sync_ok))
                        }
                    }
                }
            }
        }
    }
}

enum class SyncDialogMode {
    ConfirmBackup,
    Completed,
}
