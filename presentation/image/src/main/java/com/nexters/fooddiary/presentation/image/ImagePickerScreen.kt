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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.ui.unit.sp
import com.nexters.fooddiary.core.common.permission.PermissionUtil
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray400
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.core.ui.theme.SdBase

private const val PREVIEW_IMAGE_URL = "https://picsum.photos/200/200?random="

private object ImagePickerDimens {
    val screenPaddingHorizontal: Dp = 16.dp
    val headerPaddingVertical: Dp = 18.dp
    val headerPaddingHorizontal: Dp = 20.dp
    val hintTopPadding: Dp = 12.dp
    val contentTopPadding: Dp = 24.dp
    val sectionGap: Dp = 32.dp
    val sectionTitleBottomPadding: Dp = 16.dp
    val gridGap: Dp = 8.dp
    val gridItemPadding: Dp = 4.dp
    val gridItemCornerRadius: Dp = 16.dp
    val checkIconPadding: Dp = 8.dp
    val checkIconSize: Dp = 20.dp
    val doneButtonPaddingVertical: Dp = 14.dp
    val doneButtonPaddingHorizontal: Dp = 24.dp
    val doneButtonTopPadding: Dp = 12.dp
    val doneButtonHorizontalMargin: Dp = 16.dp
    val doneButtonElevation: Dp = 8.dp
    val doneButtonApproxHeight: Dp = 52.dp
    val permissionButtonTopPadding: Dp = 16.dp
    val dimHeight: Dp = 186.dp

    val bottomFloatingAreaHeight: Dp =
        doneButtonTopPadding + doneButtonApproxHeight + doneButtonPaddingVertical * 2

    val gridColumnCount: Int = 3
}

@Composable
fun ImagePickerScreen(
    modifier: Modifier = Modifier,
    selectedDateString: String? = null,
    onClose: () -> Unit,
    viewModel: ImagePickerViewModel = mavericksViewModel()
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(selectedDateString) {
        viewModel.loadPhotos(selectedDateString)
    }

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
        foodImageUris = state.foodImageUris,
        allImageUris = state.allImageUris,
        isLoading = state.isLoading,
        hasPermission = state.hasPermission,
        selectedUris = state.selectedUris,
        onImageClick = { uri -> viewModel.toggleImageSelection(uri) },
        onDeselectAll = { viewModel.clearSelection() },
        onDone = {
            viewModel.uploadImage { result ->
                when (result) {
                    is UploadResult.Success -> onClose()
                    is UploadResult.Failure -> { /* 실패 시 처리 없음 */ }
                }
            }
        },
        onClose = onClose,
        onRequestPermission = { permissionLauncher.launch(requiredPermission) }
    )
}

@Composable
fun ImagePickerContent(
    modifier: Modifier = Modifier,
    foodImageUris: List<Uri> = emptyList(),
    allImageUris: List<Uri> = emptyList(),
    isLoading: Boolean = false,
    hasPermission: Boolean = true,
    selectedUris: Set<Uri> = emptySet(),
    onImageClick: (Uri) -> Unit = {},
    onDeselectAll: () -> Unit = {},
    onDone: () -> Unit = {},
    onClose: () -> Unit = {},
    onRequestPermission: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SdBase)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ImagePickerDimens.screenPaddingHorizontal)
        ) {
            ImagePickerHeader(
                onClose = onClose,
                onDeselectAll = onDeselectAll
            )

            Text(
                text = stringResource(R.string.image_picker_hint_max),
                style = AppTypography.p12.copy(
                    lineHeight = (12 * 1.3f).sp
                ),
                color = Gray400,
                modifier = Modifier.padding(top = ImagePickerDimens.hintTopPadding)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                ImagePickerContentArea(
                    foodImageUris = foodImageUris,
                    allImageUris = allImageUris,
                    isLoading = isLoading,
                    hasPermission = hasPermission,
                    selectedUris = selectedUris,
                    onImageClick = onImageClick,
                    onRequestPermission = onRequestPermission
                )
                if (hasPermission && allImageUris.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(ImagePickerDimens.dimHeight)
                    )
                }
            }

            Spacer(modifier = Modifier.height(ImagePickerDimens.bottomFloatingAreaHeight))
        }

        ImagePickerDoneButton(
            selectedCount = selectedUris.size,
            onClick = onDone,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ImagePickerHeader(
    onClose: () -> Unit,
    onDeselectAll: () -> Unit
) {
    val backDesc = stringResource(R.string.image_picker_back)
    val deselectAllText = stringResource(R.string.image_picker_deselect_all)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = ImagePickerDimens.headerPaddingVertical,
                bottom = ImagePickerDimens.headerPaddingVertical,
                start = ImagePickerDimens.headerPaddingHorizontal - ImagePickerDimens.screenPaddingHorizontal,
                end = ImagePickerDimens.headerPaddingHorizontal - ImagePickerDimens.screenPaddingHorizontal
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.icon_back),
            contentDescription = backDesc,
            modifier = Modifier
                .size(24.dp)
                .clickable { onClose() }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.clickable { onDeselectAll() }
        ) {
            Text(
                text = deselectAllText,
                style = AppTypography.p15,
                color = Gray050
            )
        }
    }
}

@Composable
private fun ImagePickerContentArea(
    foodImageUris: List<Uri>,
    allImageUris: List<Uri>,
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
        allImageUris.isEmpty() -> {
            EmptyImageView()
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = ImagePickerDimens.contentTopPadding)
            ) {
                ImagePickerSection(
                    title = stringResource(R.string.image_picker_section_food),
                    imageUris = foodImageUris,
                    selectedUris = selectedUris,
                    onImageClick = onImageClick
                )
                Spacer(modifier = Modifier.height(ImagePickerDimens.sectionGap))
                ImagePickerSection(
                    title = stringResource(R.string.image_picker_section_all),
                    imageUris = allImageUris,
                    selectedUris = selectedUris,
                    onImageClick = onImageClick
                )
            }
        }
    }
}

@Composable
private fun ImagePickerSection(
    title: String,
    imageUris: List<Uri>,
    selectedUris: Set<Uri>,
    onImageClick: (Uri) -> Unit
) {
    Column {
        Text(
            text = title,
            style = AppTypography.p15.copy(fontWeight = FontWeight.SemiBold),
            color = Gray050,
            modifier = Modifier.padding(bottom = ImagePickerDimens.sectionTitleBottomPadding)
        )
        ImageGrid(
            imageUris = imageUris,
            selectedUris = selectedUris,
            onImageClick = onImageClick
        )
    }
}

@Composable
private fun ImageGrid(
    imageUris: List<Uri>,
    selectedUris: Set<Uri>,
    onImageClick: (Uri) -> Unit
) {
    val columnCount = ImagePickerDimens.gridColumnCount
    val rows = imageUris.chunked(columnCount)
    Column(
        verticalArrangement = Arrangement.spacedBy(ImagePickerDimens.gridGap)
    ) {
        rows.forEach { rowUris ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ImagePickerDimens.gridGap)
            ) {
                rowUris.forEach { uri ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    ) {
                        ImageGridItem(
                            uri = uri,
                            isSelected = selectedUris.contains(uri),
                            onClick = { onImageClick(uri) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                repeat(columnCount - rowUris.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PermissionRequestView(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(ImagePickerDimens.screenPaddingHorizontal),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ImagePickerDimens.permissionButtonTopPadding)
        ) {
            Text(
                text = stringResource(R.string.image_picker_permission_message),
                style = AppTypography.p15,
                color = Gray050
            )
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(containerColor = PrimBase),
                shape = RoundedCornerShape(999.dp)
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
        Text(
            stringResource(R.string.image_picker_loading),
            color = Gray050
        )
    }
}

@Composable
private fun EmptyImageView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.image_picker_empty),
            color = Gray050
        )
    }
}

@Composable
private fun ImageGridItem(
    uri: Uri,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.aspectRatio(1f)
) {
    val galleryDesc = stringResource(R.string.image_picker_gallery_image)
    val selectedDesc = stringResource(R.string.image_picker_selected)
    val unselectedDesc = stringResource(R.string.image_picker_unselected)
    val borderColor = if (isSelected) PrimBase else Color.Transparent
    Box(
        modifier = modifier.padding(ImagePickerDimens.gridItemPadding)
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
                .align(Alignment.TopStart)
                .padding(ImagePickerDimens.checkIconPadding)
                .size(ImagePickerDimens.checkIconSize)
        )
    }
}

private fun Modifier.doneButtonSurface(): Modifier = this
    .fillMaxWidth()
    .padding(horizontal = ImagePickerDimens.doneButtonHorizontalMargin)
    .padding(bottom = ImagePickerDimens.doneButtonTopPadding)
    .navigationBarsPadding()
    .shadow(
        elevation = ImagePickerDimens.doneButtonElevation,
        shape = RoundedCornerShape(999.dp),
        spotColor = Color.Black.copy(alpha = 0.2f)
    )

@Composable
private fun ImagePickerDoneButton(
    selectedCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.doneButtonSurface(),
        shape = RoundedCornerShape(999.dp),
        color = PrimBase
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(
                vertical = ImagePickerDimens.doneButtonPaddingVertical,
                horizontal = ImagePickerDimens.doneButtonPaddingHorizontal
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
        ) {
            Text(
                text = stringResource(R.string.image_picker_select_count, selectedCount),
                style = AppTypography.p15.copy(fontWeight = FontWeight.SemiBold),
                color = Gray050
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun ImagePickerPreview() {
    var selectedUris by remember { mutableStateOf(setOf<Uri>()) }
    val allUris = (1..9).map { index -> Uri.parse("$PREVIEW_IMAGE_URL$index") }
    val foodUris = allUris.take(4)
    ImagePickerContent(
        modifier = Modifier
            .height(780.dp)
            .fillMaxWidth(),
        foodImageUris = foodUris,
        allImageUris = allUris,
        selectedUris = selectedUris,
        onImageClick = { uri ->
            selectedUris = nextSelectionAfterToggle(
                selectedUris,
                uri,
                ImagePickerViewModel.MAX_SELECTION_COUNT
            )
        },
        onDeselectAll = { selectedUris = emptySet() },
        onDone = {}
    )
}
