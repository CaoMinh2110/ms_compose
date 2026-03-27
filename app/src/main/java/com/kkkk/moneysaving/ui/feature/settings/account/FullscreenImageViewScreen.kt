package com.kkkk.moneysaving.ui.feature.settings.account

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.common.ImagePermissionManager
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FullscreenImageViewScreen(
    imageUrl: String?,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: AccountViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(offsetX)
    val animatedOffsetY by animateFloatAsState(offsetY)

    val dragDistance by remember {
        derivedStateOf {
            maxOf(
                abs(animatedOffsetX),
                abs(animatedOffsetY)
            )
        }
    }
    val backgroundAlpha by remember {
        derivedStateOf {
            (1f - (dragDistance / 300f)).coerceIn(
                0f,
                1f
            )
        }
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as Activity

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.updateUserProfile(viewModel.uiState.value.userProfile.name, uri.toString())
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

    val cornerRadius by animatedVisibilityScope.transition.animateDp(
        label = "cornerRadius",
        transitionSpec = { tween(durationMillis = 400) }
    ) { state -> if (state == EnterExitState.Visible) 0.dp else 70.dp }

    val controlsAlpha by animateFloatAsState(
        targetValue = if (showButton && offsetY == 0f) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "controlsAlpha"
    )

    DisposableEffect(Unit) {
        val window = activity.window
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        onDispose {
            controller.show(WindowInsetsCompat.Type.statusBars())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = backgroundAlpha))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        val dragDist = maxOf(abs(offsetX), abs(offsetY))
                        if (dragDist > 300f) {
                            onBack()
                        } else {
                            offsetX = 0f
                            offsetY = 0f
                        }
                    },
                    onDragCancel = {
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { showButton = !showButton },
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            with(sharedTransitionScope) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .offset {
                            IntOffset(animatedOffsetX.roundToInt(), animatedOffsetY.roundToInt())
                        }
                        .sharedElement(
                            rememberSharedContentState(key = "avatar_image"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .clip(RoundedCornerShape(cornerRadius)),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .fillMaxWidth()
                .alpha(controlsAlpha),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        enabled = showButton && offsetY == 0f,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBack() },
                tint = Color.White,
            )

            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        enabled = showButton && offsetY == 0f,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showDialog = true },
                tint = Color.White,
            )
        }

        if (showDialog) {
            FullscreenMenuDialog(
                onDismiss = { showDialog = false },
                onDownload = {
                    if (!imageUrl.isNullOrEmpty()) {
                        scope.launch {
                            val result = viewModel.imageRepository.saveImageToGallery(imageUrl)
                            result.onSuccess {
                                Toast.makeText(
                                    context,
                                    R.string.message_save_success,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.onFailure {
                                Toast.makeText(
                                    context,
                                    R.string.message_save_fail,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    showDialog = false
                },
                onShare = { showDialog = false },
                onEdit = {
                    openGallery()
                },
            )
        }
    }
}
