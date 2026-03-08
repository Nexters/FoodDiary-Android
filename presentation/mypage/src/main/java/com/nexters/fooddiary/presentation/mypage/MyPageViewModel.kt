package com.nexters.fooddiary.presentation.mypage

import android.util.Log
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.nexters.fooddiary.domain.usecase.DeleteAccountUseCase
import com.nexters.fooddiary.domain.usecase.GetUserInfoUseCase
import com.nexters.fooddiary.domain.usecase.SignOutUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

data class MyPageState(
    val nickName: String = "",
    val signOutResult: Async<Unit> = Uninitialized,
    val deleteAccountResult: Async<Unit> = Uninitialized
) : MavericksState

class MyPageViewModel @AssistedInject constructor(
    @Assisted initialState: MyPageState,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val signOutUseCase: SignOutUseCase,
) : MavericksViewModel<MyPageState>(initialState) {

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<MyPageViewModel, MyPageState> {
        override fun create(state: MyPageState): MyPageViewModel
    }

    fun loadUserMe() {
        suspend { getUserInfoUseCase().getOrNull() }
            .execute { result ->
                when (result) {
                    is Success -> {
                        val name = result.invoke().orEmpty()
                        copy(nickName = name)
                    }
                    else -> this
                }
            }
    }

    fun signOut() = executeAsync(
        action = { signOutUseCase() },
        updateState = { copy(signOutResult = it) }
    )

    fun deleteAccount() = executeAsync(
        action = {
            deleteAccountUseCase().getOrThrow()
        },
        updateState = {
            copy(deleteAccountResult = it)
        }
    )

    private inline fun executeAsync(
        crossinline action: suspend () -> Unit,
        crossinline updateState: MyPageState.(Async<Unit>) -> MyPageState
    ) = suspend { action() }.execute { result ->
        updateState(result)
    }
    fun resetSignOutResult() {
        setState { copy(signOutResult = Uninitialized) }
    }

    fun resetDeleteAccountResult() {
        setState { copy(deleteAccountResult = Uninitialized) }
    }

    companion object :
        MavericksViewModelFactory<MyPageViewModel, MyPageState> by hiltMavericksViewModelFactory()
}