package com.nexters.fooddiary.presentation.modify

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.nexters.fooddiary.core.ui.R.drawable
import com.nexters.fooddiary.core.ui.component.CommonChips
import com.nexters.fooddiary.core.ui.component.CommonCircleButton
import com.nexters.fooddiary.core.ui.component.DetailScreenHeader
import com.nexters.fooddiary.core.ui.dashBorder
import com.nexters.fooddiary.core.ui.theme.AppTypography
import com.nexters.fooddiary.core.ui.theme.Gray050
import com.nexters.fooddiary.core.ui.theme.Gray200
import com.nexters.fooddiary.core.ui.theme.Gray400
import com.nexters.fooddiary.core.ui.theme.Gray600
import com.nexters.fooddiary.core.ui.theme.Sd700
import com.nexters.fooddiary.core.ui.theme.Sd800
import com.nexters.fooddiary.core.ui.theme.Sd900
import com.nexters.fooddiary.core.ui.theme.SdBase
import com.nexters.fooddiary.core.ui.theme.White

private const val PLACEHOLDER_IMAGE_URL = "https://picsum.photos/200/300"

private val SectionTitleColor = Gray050
private val ChipInactiveBg = Sd700
private val InputBg = Sd900
private val ChipShape = RoundedCornerShape(999.dp)
private val InputShape = RoundedCornerShape(10.dp)

@Composable
fun ModifyScreen(
    diaryId: String,
    onBack: () -> Unit,
    onNavigateToImagePicker: () -> Unit = {},
    viewModel: ModifyViewModel = mavericksViewModel(),
) {
    LaunchedEffect(diaryId) {
        viewModel.syncDiaryId(diaryId)
    }
    val state by viewModel.collectAsState()
    ModifyScreenContent(
        onBack = onBack,
        onNavigateToImagePicker = onNavigateToImagePicker,
        state = state,
        onSelect = viewModel::selectCategory,
        onSearchChange = viewModel::updateAddressSearch,
        onRemoveTag = viewModel::removeTag,
        onRemovePhotoAt = viewModel::removePhotoAt,
        onDelete = { viewModel.onDelete(onSuccess = onBack) },
        onSave = { viewModel.onSave(onSuccess = onBack) },
        onAddChip = { }
    )
}

@Composable
private fun ModifyScreenContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNavigateToImagePicker: () -> Unit = {},
    onSelect: (String) -> Unit = {},
    onSearchChange: (String) -> Unit = {},
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SdBase)
            .padding(horizontal = 16.dp),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            item {
                DetailScreenHeader(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onBackButtonClick = onBack,
                ) {
                    Text(
                        text = modifyTitle,
                        style = AppTypography.p15,
                        color = Gray050,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AddPhotoBox(onClick = onNavigateToImagePicker)
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
                    CommonChips(
                        categories = state.categories,
                        selectedCategory = state.selectedCategory,
                        onSelect = onSelect,
                    )
                }
            }
            item {
                Section(
                    sectionTitle = sectionAddress,
                ) {
                    AddressSection(
                        searchQuery = state.addressSearchQuery,
                        onSearchChange = onSearchChange,
                        addressLines = state.addressLines,
                    )
                }
            }
            item {
                Section(
                    sectionTitle = sectionTag,
                ) {
                    TagChips(
                        tags = state.tags,
                        onRemove = onRemoveTag,
                        onAddChip = onAddChip
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        ModifyBottomButtons(
            onDelete = onDelete,
            onSave = onSave,
        )
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
            .clip(RoundedCornerShape(10.dp))
            .background(Sd700),
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
            painter = painterResource(drawable.ic_close),
            contentDescription = "Close",
        )
    }
}

@Composable
private fun AddPhotoBox(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .size(104.dp)
            .clip(RoundedCornerShape(10.dp))
            .dashBorder()
            .background(Sd700)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            tint = Color.White,
            contentDescription = "Add Photo",
        )
    }
}

@Composable
private fun AddressSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    addressLines: List<String>,
) {
    val searchPlaceholder = stringResource(R.string.modify_address_search_placeholder)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = searchPlaceholder,
                    style = AppTypography.p15,
                    color = Gray600,
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = White,
                )
            },
            singleLine = true,
            shape = InputShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                disabledContainerColor = InputBg,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Gray050,
                unfocusedTextColor = Gray050,
                cursorColor = Gray050,
            ),
        )
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
private fun TagChips(
    tags: List<String>,
    onRemove: (String) -> Unit,
    onAddChip: () -> Unit = {},
) {
    val deleteContentDesc = stringResource(R.string.modify_delete)
    val addTagContentDesc = stringResource(R.string.modify_tag_add)
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tags.forEach { tag ->
            key(tag) {
                TagChipItem(
                    tag = tag,
                    onRemove = onRemove,
                    deleteContentDesc = deleteContentDesc,
                )
            }
        }
        Box(
            modifier = Modifier
                .clip(ChipShape)
                .background(ChipInactiveBg)
                .clickable {
                    onAddChip()
                }
                .size(34.dp)
                .padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = addTagContentDesc,
                tint = Gray400,
            )
        }
    }
}

@Composable
private fun TagChipItem(
    tag: String,
    onRemove: (String) -> Unit,
    deleteContentDesc: String,
) {
    Row(
        modifier = Modifier
            .clip(ChipShape)
            .background(ChipInactiveBg)
            .padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = tag,
            style = AppTypography.p14,
            color = Gray400,
        )
        Image(
            painter = painterResource(drawable.ic_close),
            contentDescription = deleteContentDesc,
            modifier = Modifier
                .size(18.dp)
                .clickable { onRemove(tag) },
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
        modifier = Modifier.fillMaxWidth(),
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
            modifier = Modifier.weight(2f),
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
