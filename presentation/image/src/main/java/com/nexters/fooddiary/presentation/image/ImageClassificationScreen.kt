package com.nexters.fooddiary.presentation.image

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsStateWithLifecycle
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.common.toPercentageString
import com.nexters.fooddiary.domain.model.ClassificationResult

@Composable
internal fun ImageClassificationScreen(
    onClose: () -> Unit,
    viewModel: ImageClassificationViewModel = mavericksViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.collectAsStateWithLifecycle()
    var showImagePicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            showToast(context, message, Toast.LENGTH_SHORT)
        }
    }

    if (showImagePicker) {
        ImagePickerScreen(
            onImagesSelected = { uris ->
                if (uris.isNotEmpty()) {
                    viewModel.loadImagesFromUris(uris)
                }
                showImagePicker = false
            },
            onClose = { showImagePicker = false }
        )
    } else {
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

                if (state.selectedItems.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(state.selectedItems) { index, item ->
                            key(item.bitmap.hashCode(), index) {
                                ImageDisplaySection(
                                    item = item,
                                    isClassifying = state.isLoading && item.classificationResult == null,
                                    context = context
                                )
                            }
                        }
                    }
                }

                AlbumSelectionButton(
                    hasSelectedImage = state.hasSelectedImage,
                    onClick = { showImagePicker = true }
                )

                if (!state.hasSelectedImage) {
                    SelectionHint()
                }
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
    item: ClassifiedImageItem,
    isClassifying: Boolean,
    context: Context
) {
    val displayHeight = integerResource(R.integer.image_display_height_dp)
    val imageBitmap = remember(item.bitmap) { item.bitmap.asImageBitmap() }
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
    Spacer(modifier = Modifier.height(8.dp))
    when {
        isClassifying -> ClassifyingText()
        item.classificationResult != null -> {
            ClassificationResultText(
                result = item.classificationResult,
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
    result: ClassificationResult,
    context: Context
) {
    val foodMessage = remember { context.getString(R.string.image_classification_food) }
    val notFoodMessage = remember { context.getString(R.string.image_classification_not_food) }
    val message = remember(result, foodMessage, notFoodMessage) {
        formatClassificationMessage(result, foodMessage, notFoodMessage)
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

private fun formatClassificationMessage(
    result: ClassificationResult,
    foodMessage: String,
    notFoodMessage: String
): String {
    val confidence = when {
        result.isFood -> result.foodConfidence
        else -> result.notFoodConfidence
    }.toPercentageString(1)
    return when {
        result.isFood -> foodMessage.format(confidence)
        else -> notFoodMessage.format(confidence)
    }
}

private fun showToast(context: Context, message: String, duration: Int) {
    Toast.makeText(context, message, duration).show()
}
