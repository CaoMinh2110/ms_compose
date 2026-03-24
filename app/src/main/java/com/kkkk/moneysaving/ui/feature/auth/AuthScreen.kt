package com.kkkk.moneysaving.ui.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.components.LoadingOverlay
import com.kkkk.moneysaving.ui.theme.TextError

@Composable
fun AuthScreen(
    onFinished: (Boolean) -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    AuthContent(
        uiState = uiState,
        onGoogleSignInClick = { viewModel.signInWithGoogle(context, onFinished) },
        onSkipClick = { viewModel.skip(onFinished) }
    )
}

@Composable
private fun AuthContent(
    uiState: AuthUiState,
    onGoogleSignInClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.auth_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1B4B59),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.auth_google),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = onGoogleSignInClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B4B59),
                        contentColor = Color.White,
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.auth_google),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onSkipClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEAF4F7),
                        contentColor = Color(0xFF1B4B59),
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.auth_skip),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                uiState.error?.let { err ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = err,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextError,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (uiState.isLoading) {
            LoadingOverlay()
        }
    }
}

@Preview
@Composable
private fun AuthPreview() {
    MaterialTheme {
        AuthContent(
            uiState = AuthUiState(isLoading = false),
            onGoogleSignInClick = {},
            onSkipClick = {}
        )
    }
}

@Preview
@Composable
private fun AuthLoadingPreview() {
    MaterialTheme {
        AuthContent(
            uiState = AuthUiState(isLoading = true),
            onGoogleSignInClick = {},
            onSkipClick = {}
        )
    }
}
