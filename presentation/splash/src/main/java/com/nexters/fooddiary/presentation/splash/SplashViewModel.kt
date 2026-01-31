package com.nexters.fooddiary.presentation.splash

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.domain.usecase.GetCurrentUserUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SplashUiState(
    val navigationDestination: NavigationDestination? = null
) : MavericksState

sealed interface NavigationDestination {
    data object Home : NavigationDestination
    data object Login : NavigationDestination
}

class SplashViewModel @AssistedInject constructor(
    @Assisted initialState: SplashUiState,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : MavericksViewModel<SplashUiState>(initialState) {

    init {
        viewModelScope.launch {
            // 1초 표시 보장
            delay(1000)

            // 인증 상태 확인
            // TODO: /auth/verify API로 저장된 토큰의 유효성 검사 필요
            val user = getCurrentUserUseCase()
            val destination = if (user != null) {
                NavigationDestination.Home
            } else {
                NavigationDestination.Login
            }

            setState { copy(navigationDestination = destination) }
        }
    }

    fun consumeNavigation() {
        setState { copy(navigationDestination = null) }
    }

    @AssistedFactory
    interface Factory: AssistedViewModelFactory<SplashViewModel, SplashUiState> {
        override fun create(state: SplashUiState): SplashViewModel
    }

    companion object : MavericksViewModelFactory<SplashViewModel, SplashUiState> by hiltMavericksViewModelFactory()
}
