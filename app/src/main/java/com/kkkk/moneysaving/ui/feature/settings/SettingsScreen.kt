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
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
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
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.Secondary
import com.kkkk.moneysaving.ui.theme.TextPrimary
import com.kkkk.moneysaving.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    onAccountClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onCurrencyClick: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showRateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        SettingsContent(
            uiState = uiState,
            onAccountClick = onAccountClick,
            onLanguageClick = onLanguageClick,
            onCurrencyClick = onCurrencyClick,
            onRateClick = { showRateDialog = true },
            onLoginClick = { viewModel.loginWithGoogle(context) },
            onLogoutClick = { viewModel.logout() },
            onSyncCheckedChange = { checked ->
                if (uiState.isLoggedIn) {
                    viewModel.setSyncEnabled(checked)
                }
            }
        )

        if (uiState.showSyncConfirmDialog) {
            SyncDialog(
                mode = SyncDialogMode.ConfirmBackup,
                onDismiss = { viewModel.dismissSyncConfirmDialog() },
                onConfirm = { viewModel.confirmSync() },
            )
        }

        if (uiState.showSyncSuccessDialog) {
            SyncDialog(
                mode = SyncDialogMode.Completed,
                onDismiss = { viewModel.dismissSyncSuccessDialog() },
                onConfirm = { viewModel.dismissSyncSuccessDialog() },
            )
        }

        if (showRateDialog) {
            RateDialog(
                onDismiss = { showRateDialog = false },
                onRate = { _ ->
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
    onRateClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onSyncCheckedChange: (Boolean) -> Unit = {},
) {
    @Composable
    fun arrowIcon() = Icon(
        Icons.AutoMirrored.Rounded.ArrowForwardIos,
        contentDescription = null,
        modifier = Modifier.size(16.dp),
        tint = TextSecondary
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
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(vertical = 14.dp)
                    .padding(top = 24.dp),
            ) {
                SettingsRow(
                    icon = Icons.Default.Language,
                    title = stringResource(R.string.title_language),
                    trailing = { arrowIcon() },
                    onClick = onLanguageClick,
                )
                SettingsRow(
                    icon = Icons.Default.Public,
                    title = stringResource(R.string.title_currency),
                    subtitle = uiState.currencyCode,
                    trailing = { arrowIcon() },
                    onClick = onCurrencyClick,
                )
                SettingsRow(
                    icon = Icons.Default.Sync,
                    title = stringResource(R.string.title_sync),
                    trailing = {
                        Switch(
                            checked = if (uiState.isLoggedIn) uiState.isSyncEnabled else false,
                            onCheckedChange = onSyncCheckedChange,
                            enabled = uiState.isLoggedIn
                        )
                    },
                )
                SettingsRow(
                    icon = Icons.Default.Star,
                    title = stringResource(R.string.title_rate),
                    trailing = { arrowIcon() },
                    onClick = onRateClick,
                )
                SettingsRow(
                    icon = Icons.Default.Policy,
                    title = stringResource(R.string.title_privacy_policy),
                    trailing = { },
                )
                SettingsRow(
                    icon = Icons.Default.Share,
                    title = stringResource(R.string.title_share),
                    trailing = { },
                )
                SettingsRow(
                    icon = if (uiState.isLoggedIn) Icons.AutoMirrored.Filled.Logout else Icons.AutoMirrored.Filled.Login,
                    title = stringResource(if (uiState.isLoggedIn) R.string.title_log_out else R.string.title_log_in),
                    trailing = { },
                    onClick = { if (uiState.isLoggedIn) onLogoutClick() else onLoginClick() },
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
            text = stringResource(R.string.title_account),
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
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Primary), // vòng ngoài (Primary)
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoggedIn && userProfile?.avatar != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp) // độ dày vòng Primary
                                .clip(CircleShape)
                                .background(Secondary), // vòng trong (Secondary)
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = userProfile.avatar,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(3.dp) // khoảng cách tới vòng Secondary
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                Column {
                    Text(
                        text = userProfile?.name.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                    Text(
                        text = userProfile?.email.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE0F2F6),
                    )
                }
            }
            if (isLoggedIn) {
                IconButton(onClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
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
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
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
                        tint = Primary,
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
                            color = TextSecondary,
                        )
                    }
                }
            }
            trailing()
        }
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
