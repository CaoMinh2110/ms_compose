package com.kkkk.moneysaving.ui.feature.settings.account

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.common.ImagePermissionManager
import com.kkkk.moneysaving.ui.theme.AppColor
import com.kkkk.moneysaving.ui.theme.Primary
import com.kkkk.moneysaving.ui.theme.Secondary
import com.kkkk.moneysaving.ui.theme.TextPrimary
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AccountScreen(
    onBack: () -> Unit,
    onViewFullscreenImage: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: AccountViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var editableName by remember(uiState.userProfile.name) { mutableStateOf(uiState.userProfile.name) }
    var selectedAvatarUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedAvatarUri = uri
            showDialog = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        }
    }

    fun openGallery() {
        val permissions = ImagePermissionManager.getRequiredPermissions()
        if (ImagePermissionManager.hasMediaPermission(context)) {
            galleryLauncher.launch("image/*")
        } else {
            permissionLauncher.launch(permissions[0])
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 14.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.title_account),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )

                IconButton(
                    onClick = {
                        viewModel.updateUserProfile(editableName, selectedAvatarUri?.toString())
                    },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(color = Secondary, shape = CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { showDialog = true }
                            .fillMaxSize()
                            .padding(6.dp)
                    ) {
                        val displayImageUrl =
                            selectedAvatarUri?.toString() ?: uiState.userProfile.avatar
                        if (!displayImageUrl.isNullOrEmpty()) {
                            with(sharedTransitionScope) {
                                AsyncImage(
                                    model = displayImageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .sharedElement(
                                            rememberSharedContentState(key = "avatar_image"),
                                            animatedVisibilityScope = animatedVisibilityScope
                                        ),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                tint = Color.White,
                            )
                        }
                    }
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .shadow(2.dp, CircleShape)
                            .size(30.dp)
                            .background(color = Color.White, shape = CircleShape)
                            .padding(8.dp),
                        onClick = { openGallery() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Secondary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(R.string.title_name),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                )
                OutlinedTextField(
                    value = editableName,
                    onValueChange = { editableName = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(R.string.title_email),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                )
                OutlinedTextField(
                    value = uiState.userProfile.email,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    shape = RoundedCornerShape(14.dp),
                )
            }
        }
    }

    if (showDialog) {
        AccountMenuDialog(
            onDismiss = { showDialog = false },
            onViewImage = {
                val imageUrl = selectedAvatarUri?.toString() ?: uiState.userProfile.avatar
                if (!imageUrl.isNullOrEmpty()) {
                    val encodedUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString())
                    onViewFullscreenImage(encodedUrl)
                }
                showDialog = false
            },
            onEdit = {
                openGallery()
                showDialog = false
            }
        )
    }
}
