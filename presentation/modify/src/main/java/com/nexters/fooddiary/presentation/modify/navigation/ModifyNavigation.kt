package com.nexters.fooddiary.presentation.modify.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.fooddiary.core.ui.alert.DialogData
import com.nexters.fooddiary.core.ui.alert.SnackBarData
import com.nexters.fooddiary.presentation.modify.ModifyScreen
import kotlinx.serialization.Serializable

@Serializable
data class ModifyRoute(val diaryId: String)

const val MODIFY_SEARCH_RESULT_NAME = "modify_search_result_name"
const val MODIFY_SEARCH_RESULT_ADDRESS_NAME = "modify_search_result_address_name"
const val MODIFY_SEARCH_RESULT_ROAD_ADDRESS = "modify_search_result_road_address"
const val MODIFY_SEARCH_RESULT_URL = "modify_search_result_url"

data class ModifySearchResult(
    val name: String,
    val addressName: String,
    val roadAddress: String,
    val url: String,
)

fun NavGraphBuilder.modifyScreen(
    onBack: () -> Unit,
    onNavigateToSearch: (String) -> Unit = {},
    onShowDialog: (DialogData) -> Unit = {},
    onShowSnackBar: (SnackBarData) -> Unit = {},
) {
    composable<ModifyRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ModifyRoute>()
        val resultName by backStackEntry.savedStateHandle
            .getStateFlow<String?>(MODIFY_SEARCH_RESULT_NAME, null)
            .collectAsState()
        val resultRoadAddress by backStackEntry.savedStateHandle
            .getStateFlow<String?>(MODIFY_SEARCH_RESULT_ROAD_ADDRESS, null)
            .collectAsState()
        val resultAddressName by backStackEntry.savedStateHandle
            .getStateFlow<String?>(MODIFY_SEARCH_RESULT_ADDRESS_NAME, null)
            .collectAsState()
        val resultUrl by backStackEntry.savedStateHandle
            .getStateFlow<String?>(MODIFY_SEARCH_RESULT_URL, null)
            .collectAsState()
        val searchResult = if (resultName != null && resultRoadAddress != null && resultUrl != null) {
            ModifySearchResult(
                name = resultName.orEmpty(),
                addressName = resultAddressName.orEmpty(),
                roadAddress = resultRoadAddress.orEmpty(),
                url = resultUrl.orEmpty(),
            )
        } else {
            null
        }
        ModifyScreen(
            diaryId = route.diaryId,
            onBack = onBack,
            onNavigateToSearch = onNavigateToSearch,
            searchResult = searchResult,
            onSearchResultConsumed = {
                backStackEntry.savedStateHandle.remove<String>(MODIFY_SEARCH_RESULT_NAME)
                backStackEntry.savedStateHandle.remove<String>(MODIFY_SEARCH_RESULT_ADDRESS_NAME)
                backStackEntry.savedStateHandle.remove<String>(MODIFY_SEARCH_RESULT_ROAD_ADDRESS)
                backStackEntry.savedStateHandle.remove<String>(MODIFY_SEARCH_RESULT_URL)
            },
            onShowDialog = onShowDialog,
            onShowSnackBar = onShowSnackBar,
        )
    }
}
