package com.nexters.fooddiary.presentation.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraScreen(onClose: () -> Unit) {
    val imageCapture = remember { ImageCapture.Builder().build() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        CameraPreview(
            imageCapture = imageCapture,
            modifier = Modifier.fillMaxSize()
        )
        
        CameraControls(
            onClose = onClose,
            onCapture = { /* TODO: 사진 촬영 로직 구현 */ }
        )
    }
}

@Composable
private fun CameraPreview(
    imageCapture: ImageCapture,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                startCamera(ctx, lifecycleOwner, this, imageCapture)
            }
        },
        modifier = modifier
    )
}

@Composable
private fun CameraControls(
    onClose: () -> Unit,
    onCapture: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        CloseButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(dimensionResource(R.dimen.camera_button_padding_small))
        )
        
        CaptureButton(
            onClick = onCapture,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(dimensionResource(R.dimen.camera_button_padding_large))
        )
    }
}

@Composable
private fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
            contentDescription = context.getString(R.string.camera_close),
            tint = Color.White
        )
    }
}

@Composable
private fun CaptureButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(dimensionResource(R.dimen.camera_capture_button_size)),
        shape = CircleShape,
        containerColor = Color.White.copy(alpha = 0.9f)
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.camera_capture_button_inner_size))
                .background(Color.White, CircleShape)
        )
    }
}

private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    imageCapture: ImageCapture
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder()
            .build()
            .apply { setSurfaceProvider(previewView.surfaceProvider) }
        
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        
        runCatching {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        }.onFailure { it.printStackTrace() }
    }, ContextCompat.getMainExecutor(context))
}
