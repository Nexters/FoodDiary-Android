package com.nexters.fooddiary.presentation.image

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.fooddiary.core.common.permission.PermissionUtil

private const val PREVIEW_IMAGE_URL = "https://picsum.photos/200/200?random="

private object ImagePickerDimens {
    val screenPadding: Dp = 20.dp
    val headerBottomPadding: Dp = 16.dp
    val headerIconStartPadding: Dp = 30.dp
    val gridMinCellSize: Dp = 104.dp
    val gridPadding: Dp = 4.dp
    val gridItemPadding: Dp = 4.dp
    val gridItemCornerRadius: Dp = 8.dp
    val checkIconPadding: Dp = 6.dp
    val checkIconSize: Dp = 24.dp
    val doneButtonTopPadding: Dp = 12.dp
    val permissionButtonTopPadding: Dp = 16.dp
}

@Composable
fun ImagePickerScreen(
    modifier: Modifier = Modifier,
    onImagesSelected: (List<Uri>) -> Unit,
    onClose: () -> Unit,
    viewModel: ImagePickerViewModel = mavericksViewModel()
) {
    val state by viewModel.collectAsState()

    val requiredPermission = PermissionUtil.getRequiredMediaPermission()
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onPermissionGranted()
        }
    }

    LaunchedEffect(state.hasPermission) {
        if (!state.hasPermission) {
            permissionLauncher.launch(requiredPermission)
        }
    }

    ImagePickerContent(
        modifier = modifier,
        imageUris = state.imageUris,
        isLoading = state.isLoading,
        hasPermission = state.hasPermission,
        selectedUris = state.selectedUris,
        onImageClick = { uri -> viewModel.toggleImageSelection(uri) },
        onDone = {
            onImagesSelected(state.selectedUris.toList())
        },
        onClose = onClose,
        onRequestPermission = { permissionLauncher.launch(requiredPermission) }
    )
}


@Composable
fun ImagePickerContent(
    modifier: Modifier = Modifier,
    imageUris: List<Uri> = emptyList(),
    isLoading: Boolean = false,
    hasPermission: Boolean = true,
    selectedUris: Set<Uri> = emptySet(),
    onImageClick: (Uri) -> Unit = {},
    onDone: () -> Unit = {},
    onClose: () -> Unit = {},
    onRequestPermission: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(ImagePickerDimens.screenPadding)
    ) {
        ImagePickerHeader(onClose = onClose)
        
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            ImagePickerContentArea(
                imageUris = imageUris,
                isLoading = isLoading,
                hasPermission = hasPermission,
                selectedUris = selectedUris,
                onImageClick = onImageClick,
                onRequestPermission = onRequestPermission
            )
        }

        ImagePickerDoneButton(onClick = onDone)
    }
}

@Composable
private fun ImagePickerHeader(onClose: () -> Unit) {
    val backDesc = stringResource(R.string.image_picker_back)
    val iconDesc = stringResource(R.string.image_picker_icon)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = ImagePickerDimens.headerBottomPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.icon_back),
            contentDescription = backDesc,
            modifier = Modifier.clickable { onClose() }
        )
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_drawer),
            contentDescription = iconDesc,
            modifier = Modifier.padding(start = ImagePickerDimens.headerIconStartPadding)
        )
    }
}

@Composable
private fun ImagePickerContentArea(
    imageUris: List<Uri>,
    isLoading: Boolean,
    hasPermission: Boolean,
    selectedUris: Set<Uri>,
    onImageClick: (Uri) -> Unit,
    onRequestPermission: () -> Unit
) {
    when {
        !hasPermission -> {
            PermissionRequestView(onRequestPermission = onRequestPermission)
        }
        isLoading -> {
            LoadingView()
        }
        imageUris.isEmpty() -> {
            EmptyImageView()
        }
        else -> {
            ImageGrid(
                imageUris = imageUris,
                selectedUris = selectedUris,
                onImageClick = onImageClick
            )
        }
    }
}

@Composable
private fun PermissionRequestView(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.image_picker_permission_message))
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.padding(top = ImagePickerDimens.permissionButtonTopPadding)
            ) {
                Text(stringResource(R.string.image_picker_request_permission))
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.image_picker_loading))
    }
}

@Composable
private fun EmptyImageView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.image_picker_empty))
    }
}

@Composable
private fun ImageGrid(
    imageUris: List<Uri>,
    selectedUris: Set<Uri>,
    onImageClick: (Uri) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = ImagePickerDimens.gridMinCellSize),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(ImagePickerDimens.gridPadding),
    ) {
        items(imageUris) { uri ->
            ImageGridItem(
                uri = uri,
                isSelected = selectedUris.contains(uri),
                onClick = { onImageClick(uri) }
            )
        }
    }
}

@Composable
private fun ImageGridItem(
    uri: Uri,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val galleryDesc = stringResource(R.string.image_picker_gallery_image)
    val selectedDesc = stringResource(R.string.image_picker_selected)
    val unselectedDesc = stringResource(R.string.image_picker_unselected)
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(ImagePickerDimens.gridItemPadding)
    ) {
        AsyncImage(
            model = uri,
            contentDescription = galleryDesc,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(ImagePickerDimens.gridItemCornerRadius))
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(ImagePickerDimens.gridItemCornerRadius)
                )
                .clickable(onClick = onClick)
        )
        Image(
            imageVector = ImageVector.vectorResource(
                if (isSelected) R.drawable.ic_checked else R.drawable.ic_unchecked
            ),
            contentDescription = if (isSelected) selectedDesc else unselectedDesc,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(ImagePickerDimens.checkIconPadding)
                .size(ImagePickerDimens.checkIconSize)
        )
    }
}

@Composable
private fun ImagePickerDoneButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = ImagePickerDimens.doneButtonTopPadding)
            .navigationBarsPadding()
    ) {
        Text(stringResource(R.string.image_picker_done))
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ImagePickerPreview() {
    var selectedUris by remember { mutableStateOf(setOf<Uri>()) }
    ImagePickerContent(
        modifier = Modifier
            .height(640.dp)
            .fillMaxWidth(),
        imageUris = (1..9).map { index ->
            Uri.parse("$PREVIEW_IMAGE_URL$index")
        },
        selectedUris = selectedUris,
        onImageClick = { uri ->
            selectedUris = if (selectedUris.contains(uri)) {
                selectedUris - uri
            } else {
                selectedUris + uri
            }
        },
        onDone = {}
    )
}
