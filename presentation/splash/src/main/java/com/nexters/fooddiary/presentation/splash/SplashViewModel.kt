package com.nexters.fooddiary.presentation.splash

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.domain.usecase.SignOutUseCase
import com.nexters.fooddiary.domain.usecase.VerifyTokenUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
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
    private val verifyTokenUseCase: VerifyTokenUseCase,
    private val signOutUseCase: SignOutUseCase,
) : MavericksViewModel<SplashUiState>(initialState) {

    init {
        viewModelScope.launch {
            // 1초 표시 보장
            val minDelayDeferred = async { delay(1000)  }
            // 토큰 검증 후 네비게이션 결정
            val destinationDeferred = async { determineNavigationDestination() }

            minDelayDeferred.await()
            val destination = destinationDeferred.await()

            setState { copy(navigationDestination = destination) }
        }
    }

    fun consumeNavigation() {
        setState { copy(navigationDestination = null) }
    }

    private suspend fun determineNavigationDestination(): NavigationDestination {
        val verificationResult = verifyTokenUseCase()

        return if (verificationResult.isSuccess) {
            NavigationDestination.Home
        } else {
            // 서버 토큰이 유효하지 않으면 Firebase와 로컬 토큰 모두 정리
            signOutUseCase()
            NavigationDestination.Login
        }
    }

    @AssistedFactory
    interface Factory: AssistedViewModelFactory<SplashViewModel, SplashUiState> {
        override fun create(state: SplashUiState): SplashViewModel
    }

    companion object : MavericksViewModelFactory<SplashViewModel, SplashUiState> by hiltMavericksViewModelFactory()
}
