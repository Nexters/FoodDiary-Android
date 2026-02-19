package com.nexters.fooddiary.presentation.search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.fooddiary.domain.model.RestaurantItem
import com.nexters.fooddiary.presentation.search.SearchScreen
import kotlinx.serialization.Serializable

@Serializable
data class SearchRoute(
    val diaryId: Long? = null,
)

fun NavGraphBuilder.searchScreen(
    onClose: () -> Unit,
    onSelectRestaurant: (RestaurantItem) -> Unit = {},
) {
    composable<SearchRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<SearchRoute>()
        SearchScreen(
            diaryId = route.diaryId,
            onClose = onClose,
            onSelectRestaurant = onSelectRestaurant,
        )
    }
}
