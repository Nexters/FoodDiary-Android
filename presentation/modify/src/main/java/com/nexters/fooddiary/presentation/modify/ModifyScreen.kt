package com.nexters.fooddiary.presentation.modify

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.ui.R.drawable
import com.nexters.fooddiary.core.ui.alert.DialogData
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.core.ui.component.CommonCircleButton
import com.nexters.fooddiary.core.ui.component.DetailScreenHeader
import com.nexters.fooddiary.core.ui.component.EditableKeywordChipGroup
import com.nexters.fooddiary.core.ui.component.KeywordChipGroup
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray200
import com.nexters.fooddiary.core.ui.theme.Gray300
import com.nexters.fooddiary.core.ui.theme.Gray600
import com.nexters.fooddiary.core.ui.theme.Sd800
import com.nexters.fooddiary.core.ui.theme.Sd900
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.core.ui.theme.White
import com.nexters.fooddiary.presentation.modify.navigation.ModifySearchResult

private const val PLACEHOLDER_IMAGE_URL = "https://picsum.photos/200/300"

private val SectionTitleColor = Gray050
private val InputBg = Sd900
private val InputShape = RoundedCornerShape(10.dp)

@Composable
fun ModifyScreen(
    diaryId: String,
    onBack: () -> Unit,
    onNavigateToSearch: (String) -> Unit = {},
    searchResult: ModifySearchResult? = null,
    onSearchResultConsumed: () -> Unit = {},
    onShowDialog: (DialogData) -> Unit = {},
    onShowSnackBar: (SnackBarData) -> Unit = {},
    viewModel: ModifyViewModel = mavericksViewModel(),
) {
    LaunchedEffect(diaryId) {
        viewModel.syncDiaryId(diaryId)
    }
    val state by viewModel.collectAsState()
    val saveErrorMessage = stringResource(R.string.modify_save_error)
    val successMessage = stringResource(R.string.modify_save_success)
    LaunchedEffect(state.error) {
        when (val err = state.error) {
            ModifyError.Save -> {
                onShowDialog(DialogData(message = saveErrorMessage))
                viewModel.clearError()
            }
            null -> { }
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            when (event) {
                ModifyEvent.Saved -> {
                    onShowSnackBar(SnackBarData(message = successMessage))
                    onBack()
                }
                ModifyEvent.Deleted -> onBack()
            }
        }
    }
    LaunchedEffect(searchResult) {
        searchResult?.let { result ->
            viewModel.applySearchResult(
                name = result.name,
                roadAddress = result.roadAddress,
                url = result.url,
            )
            onSearchResultConsumed()
        }
    }
    var showTagDialog by remember { mutableStateOf(false) }
    if (showTagDialog) {
        TagInputDialog(
            onDismiss = { showTagDialog = false },
            onConfirm = { tag ->
                viewModel.addTag(tag)
                showTagDialog = false
            },
        )
    }
    ModifyScreenContent(
        onBack = onBack,
        state = state,
        onSelect = viewModel::selectCategory,
        onSearchClick = onNavigateToSearch,
        onRemoveTag = viewModel::removeTag,
        onRemovePhotoAt = viewModel::removePhotoAt,
        onDelete = viewModel::onDelete,
        onSave = viewModel::onSave,
        onAddChip = { showTagDialog = true },
    )
}

@Composable
private fun ModifyScreenContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSelect: (String) -> Unit = {},
    onSearchClick: (String) -> Unit = {},
    onRemoveTag: (String) -> Unit = {},
    onRemovePhotoAt: (Int) -> Unit = {},
    onAddChip: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSave: () -> Unit = {},
    state: ModifyState = ModifyState(),
) {
    val modifyTitle = stringResource(R.string.modify_title)
    val sectionCategory = stringResource(R.string.modify_section_category)
    val sectionAddress = stringResource(R.string.modify_section_address)
    val sectionTag = stringResource(R.string.modify_section_tag)
    val deleteContentDesc = stringResource(R.string.modify_delete)
    val addTagContentDesc = stringResource(R.string.modify_tag_add)
    val selectedCategories = remember(state.selectedCategory) {
        state.selectedCategory
            .takeIf { it.isNotBlank() }
            ?.let { setOf(it) }
            ?: emptySet()
    }

    Scaffold(
        modifier = modifier,
        containerColor = SdBase,
        topBar = {
            DetailScreenHeader(
                onBackButtonClick = onBack,
            ) {
                Text(
                    text = modifyTitle,
                    style = AppTypography.p15,
                    color = Gray050,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        },
        bottomBar = {
            ModifyBottomButtons(
                onDelete = onDelete,
                onSave = onSave,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.photoUrls.forEachIndexed { index, imageUrl ->
                        SelectBox(
                            imageUrl = imageUrl,
                            onDelete = { onRemovePhotoAt(index) },
                        )
                    }
                }
            }
            item {
                Section(
                    sectionTitle = sectionCategory,
                ) {
                    KeywordChipGroup(
                        keywords = state.categories,
                        selectedKeywords = selectedCategories,
                        onKeywordClick = onSelect,
                        unselectedContentColor = Gray300,
                    )
                }
            }
            item {
                Section(
                    sectionTitle = sectionAddress,
                ) {
                    AddressSection(
                        searchQuery = state.addressSearchQuery,
                        onSearchClick = onSearchClick,
                        addressLines = state.addressLines,
                    )
                }
            }
            item {
                Section(
                    sectionTitle = sectionTag,
                ) {
                    EditableKeywordChipGroup(
                        keywords = state.tags,
                        onKeywordRemove = onRemoveTag,
                        onAddClick = onAddChip,
                        removeContentDescription = deleteContentDesc,
                        addContentDescription = addTagContentDesc,
                    )
                }
            }
        }
    }
}

@Composable
private fun Section(
    modifier: Modifier = Modifier,
    sectionTitle: String,
    content: @Composable () -> Unit
) {
    Column() {
        Text(
            modifier = modifier.padding(bottom = 16.dp),
            text = sectionTitle,
            style = AppTypography.p14,
            color = SectionTitleColor,
        )
        content()
    }
}

@Composable
private fun SelectBox(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onDelete: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .size(104.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Sd800),
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Image(
            modifier = modifier
                .clickable {
                    onDelete()
                }
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp),
            painter = painterResource(drawable.ic_circle_close),
            contentDescription = "Close",
        )
    }
}

@Composable
private fun AddressSection(
    searchQuery: String,
    onSearchClick: (String) -> Unit,
    addressLines: List<String>,
) {
    val searchPlaceholder = stringResource(R.string.modify_address_search_placeholder)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSearchClick(searchQuery) }
        ) {
            StyledInputField(
                value = searchQuery,
                onValueChange = {},
                modifier = Modifier.defaultMinSize(minHeight = 42.dp),
                placeholder = searchPlaceholder,
                enabled = false,
                readOnly = true,
                trailingIcon = @Composable {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = White,
                    )
                },
            )
        }
        addressLines.forEachIndexed { index, line ->
            key(index) {
                AddressLineItem(line = line)
            }
        }
    }
}

@Composable
private fun AddressLineItem(line: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(InputShape)
            .background(InputBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = line,
            style = AppTypography.p15,
            color = Gray600,
        )
    }
}

@Composable
private fun ModifyBottomButtons(
    onDelete: () -> Unit,
    onSave: () -> Unit,
) {
    val deleteText = stringResource(R.string.modify_delete)
    val saveText = stringResource(R.string.modify_save)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CommonCircleButton(
            modifier = Modifier.weight(1f),
            onClick = onDelete,
            buttonColors = ButtonDefaults.buttonColors(
                contentColor = Gray200,
                containerColor = SdBase,
            ),
            border = BorderStroke(1.dp, Sd800),
            buttonText = deleteText,
            contentColor = Gray200,
        )
        CommonCircleButton(
            modifier = Modifier
                .weight(2f),
            onClick = onSave,
            buttonText = saveText,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModifyScreenPreview() {
    ModifyScreenContent(
        state = ModifyState(
            diaryId = "preview",
            selectedCategory = "한식",
            categories = setOf("한식", "일식", "중식", "양식", "카페·디저트"),
            addressSearchQuery = "서울 강남구",
            addressLines = listOf("서울특별시 강남구 테헤란로 123", "역삼동 456-7"),
            roadAddress = "서울특별시 강남구 테헤란로 123",
            restaurantName = "맛있는 밥집",
            restaurantUrl = "https://example.com/restaurant",
            note = "점심에 친구들이랑 같이 왔어요. 김치찌개가 특히 맛있었습니다!",
            photoIds = listOf(1, 2),
            photoUrls = listOf(PLACEHOLDER_IMAGE_URL, PLACEHOLDER_IMAGE_URL),
            coverPhotoId = 1,
            tags = listOf("맛집", "친구모임", "점심"),
        ),
        onRemovePhotoAt = {},
    )
}
