package com.nexters.fooddiary.presentation.image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.classification.FoodClassificationResult

@Composable
internal fun ImageClassificationScreen(
    onClose: () -> Unit,
    viewModel: ImageClassificationViewModel = mavericksViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.collectAsState()
    
    val mimeType = remember { context.getString(R.string.image_mime_type) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.loadImageFromUri(it) }
    }

    LaunchedEffect(state.classificationResult, state.errorMessage) {
        state.classificationResult?.let { result ->
            when (result) {
                is ClassificationResult.Complete -> showClassificationResultToast(context, result.result)
            }
        }
        
        state.errorMessage?.let { message ->
            showToast(context, message, Toast.LENGTH_SHORT)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CloseButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.Start)
            )

            state.selectedImage?.let { image ->
                key(image) {
                    ImageDisplaySection(
                        image = image,
                        state = state,
                        context = context
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            AlbumSelectionButton(
                hasSelectedImage = state.hasSelectedImage,
                onClick = { imagePickerLauncher.launch(mimeType) }
            )

            if (!state.hasSelectedImage) {
                SelectionHint()
            }
        }
    }
}

@Composable
private fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.padding(bottom = 16.dp)
    ) {
        Icon(
            painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
            contentDescription = stringResource(R.string.image_close),
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun SelectionHint() {
    Text(
        text = stringResource(R.string.image_select_hint),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        modifier = Modifier.padding(top = 16.dp)
    )
}

@Composable
private fun ImageDisplaySection(
    image: Bitmap,
    state: ImageClassificationState,
    context: Context
) {
    val displayHeight = integerResource(R.integer.image_display_height_dp)
    val imageBitmap = remember(image) { image.asImageBitmap() }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(displayHeight.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(12.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = stringResource(R.string.image_selected),
            modifier = Modifier.fillMaxSize()
        )
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    when {
        state.isClassifying -> {
            ClassifyingText()
        }
        state.classificationResult is ClassificationResult.Complete -> {
            ClassificationResultText(
                result = (state.classificationResult as ClassificationResult.Complete).result,
                context = context
            )
        }
        else -> Unit
    }
}

@Composable
private fun ClassifyingText() {
    Text(
        text = stringResource(R.string.image_classifying),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun ClassificationResultText(
    result: FoodClassificationResult,
    context: Context
) {
    val foodMessage = remember { context.getString(R.string.image_classification_food) }
    val notFoodMessage = remember { context.getString(R.string.image_classification_not_food) }
    val message = remember(result, foodMessage, notFoodMessage) {
        result.toDisplayMessage(foodMessage, notFoodMessage)
    }
    val textColor = when {
        result.isFood -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }
    
    Text(
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        color = textColor,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun AlbumSelectionButton(
    hasSelectedImage: Boolean,
    onClick: () -> Unit
) {
    val buttonText = when {
        hasSelectedImage -> stringResource(R.string.image_select_another)
        else -> stringResource(R.string.image_select_from_album)
    }
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp)
            .size(width = 200.dp, height = 56.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = buttonText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun showClassificationResultToast(
    context: Context,
    result: FoodClassificationResult
) {
    val foodMessage = context.getString(R.string.image_classification_food)
    val notFoodMessage = context.getString(R.string.image_classification_not_food)
    val message = result.toDisplayMessage(foodMessage, notFoodMessage)
    showToast(context, message, Toast.LENGTH_LONG)
}

private fun showToast(context: Context, message: String, duration: Int) {
    Toast.makeText(context, message, duration).show()
}
