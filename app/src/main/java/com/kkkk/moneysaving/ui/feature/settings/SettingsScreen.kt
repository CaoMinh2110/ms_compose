package com.kkkk.moneysaving.ui.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.UserProfile
import com.kkkk.moneysaving.ui.LocalScreenPadding
import com.kkkk.moneysaving.ui.components.LoadingOverlay
import com.kkkk.moneysaving.ui.feature.settings.rate.RateDialog
import com.kkkk.moneysaving.ui.feature.settings.sync.SyncDialog
import com.kkkk.moneysaving.ui.feature.settings.sync.SyncDialogMode
import com.kkkk.moneysaving.ui.theme.AppColor
import com.kkkk.moneysaving.ui.theme.Secondary
import com.kkkk.moneysaving.ui.theme.TextPrimary

@Composable
fun SettingsScreen(
    onAccountClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onCurrencyClick: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showSyncDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        SettingsContent(
            uiState = uiState,
            onAccountClick = onAccountClick,
            onLanguageClick = onLanguageClick,
            onCurrencyClick = onCurrencyClick,
            onSyncClick = {
                if (uiState.isLoggedIn) {
                    showSyncDialog = true
                }
            },
            onRateClick = { showRateDialog = true },
            onLoginClick = { viewModel.loginWithGoogle(context) },
            onLogoutClick = { viewModel.logout() },
            onSyncCheckedChange = { checked ->
                if (uiState.isLoggedIn) {
                    viewModel.setSyncEnabled(checked)
                }
            }
        )

        if (showSyncDialog) {
            SyncDialog(
                mode = SyncDialogMode.ConfirmBackup,
                onDismiss = { showSyncDialog = false },
                onConfirm = {
                    showSyncDialog = false
                    viewModel.syncData()
                },
            )
        }

        if (showRateDialog) {
            RateDialog(
                onDismiss = { showRateDialog = false },
                onRate = { rating ->
                    showRateDialog = false
                    // Handle rating
                }
            )
        }

        if (uiState.isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onAccountClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onCurrencyClick: () -> Unit = {},
    onSyncClick: () -> Unit = {},
    onRateClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onSyncCheckedChange: (Boolean) -> Unit = {},
) {
    @Composable fun arrowIcon() = Icon(
        Icons.AutoMirrored.Filled.ArrowForwardIos,
        contentDescription = null,
        modifier = Modifier.size(16.dp),
        tint = Color(0xFF939393)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Secondary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(LocalScreenPadding.current),
        ) {
            SettingsHeaderSection(
                onClick = { if (uiState.isLoggedIn) onAccountClick() else onLoginClick() },
                isLoggedIn = uiState.isLoggedIn,
                userProfile = uiState.userProfile,
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = AppColor,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(vertical = 14.dp)
                    .padding(top = 24.dp),
            ) {
                SettingsRow(
                    icon = Icons.Default.Language,
                    title = stringResource(R.string.settings_language),
                    trailing = { arrowIcon() } ,
                    modifier = Modifier.clickable(onClick = onLanguageClick),
                )
                SettingsRow(
                    icon = Icons.Default.Public,
                    title = stringResource(R.string.settings_currency),
                    subtitle = uiState.currencyCode,
                    trailing = { arrowIcon() },
                    modifier = Modifier.clickable(onClick = onCurrencyClick),
                )
                SettingsRow(
                    icon = Icons.Default.Sync,
                    title = stringResource(R.string.settings_sync),
                    trailing = {
                        Switch(
                            checked = if (uiState.isLoggedIn) uiState.isSyncEnabled else false,
                            onCheckedChange = onSyncCheckedChange,
                            enabled = uiState.isLoggedIn
                        )
                    },
                    modifier = Modifier.clickable(enabled = uiState.isLoggedIn, onClick = onSyncClick)
                )
                SettingsRow(
                    icon = Icons.Default.Star,
                    title = stringResource(R.string.settings_rate),
                    trailing = { arrowIcon() },
                    modifier = Modifier.clickable(onClick = onRateClick),
                )
                SettingsRow(
                    icon = Icons.Default.Policy,
                    title = stringResource(R.string.settings_privacy_policy),
                    trailing = { },
                )
                SettingsRow(
                    icon = Icons.Default.Share,
                    title = stringResource(R.string.settings_share),
                    trailing = { },
                )
                SettingsRow(
                    icon = if (uiState.isLoggedIn) Icons.AutoMirrored.Filled.Logout else Icons.AutoMirrored.Filled.Login,
                    title = stringResource(if (uiState.isLoggedIn) R.string.settings_log_out else R.string.settings_log_in),
                    trailing = { },
                    modifier = Modifier.clickable{
                        if (uiState.isLoggedIn) onLogoutClick() else onLoginClick()
                    },
                )
            }
        }
    }
}

@Composable
private fun SettingsHeaderSection(
    onClick: () -> Unit,
    isLoggedIn: Boolean,
    userProfile: UserProfile?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_account),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (isLoggedIn && userProfile?.avatar != null) {
                    AsyncImage(
                        model = userProfile.avatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(52.dp),
                    )
                }
                Column {
                    Text(
                        text = userProfile?.name ?: stringResource(if (isLoggedIn) R.string.settings_user_name_placeholder else R.string.settings_guest),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                    Text(
                        text = userProfile?.email ?: (if (isLoggedIn) stringResource(R.string.settings_user_email_placeholder) else ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE0F2F6),
                    )
                }
            }
            if(isLoggedIn) {
                IconButton(onClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailing: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(color = Color(0xFFEAF4F7), shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF1B4B59),
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF939393),
                    )
                }
            }
        }
        trailing()
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    MaterialTheme {
        SettingsContent(
            uiState = SettingsUiState(
                languageCode = "en",
                currencyCode = "USD",
                isLoggedIn = true,
                userProfile = UserProfile(
                    uid = "123",
                    name = "John Doe",
                    email = "john.doe@example.com",
                    avatar = null
                )
            ),
        )
    }
}
