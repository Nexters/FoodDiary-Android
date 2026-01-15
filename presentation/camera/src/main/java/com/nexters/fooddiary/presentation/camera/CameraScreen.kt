package com.nexters.fooddiary.presentation.camera

import android.content.Context
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
internal fun CameraScreen(onClose: () -> Unit) {
    val context = LocalContext.current
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
            context = context,
            onClose = onClose,
            onCapture = { 
                takePhoto(context, imageCapture) { photoPath ->
                    Toast.makeText(
                        context,
                        "사진 저장: $photoPath",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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
    context: Context,
    onClose: () -> Unit,
    onCapture: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        CloseButton(
            context = context,
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
    context: Context,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        containerColor = Color.White.copy(alpha = CameraConstants.CAPTURE_BUTTON_ALPHA)
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

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onPhotoSaved: (String) -> Unit
) {
    val photoFile = createImageFile(context)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onPhotoSaved(photoFile.absolutePath)
            }
            
            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(
                    context,
                    context.getString(R.string.camera_capture_failed),
                    Toast.LENGTH_SHORT
                ).show()
                exception.printStackTrace()
            }
        }
    )
}

private fun createImageFile(context: Context): File {
    val timestamp = SimpleDateFormat(
        CameraConstants.DATE_FORMAT_PATTERN,
        Locale.getDefault()
    ).format(System.currentTimeMillis())
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
    val fileName = "${CameraConstants.FILE_NAME_PREFIX}$timestamp${CameraConstants.FILE_EXTENSION}"
    return File(storageDir, fileName)
}
