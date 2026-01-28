package com.nexters.fooddiary.presentation.home.calendar

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.fooddiary.core.common.permission.PermissionUtil
import com.nexters.fooddiary.domain.usecase.GetAllPhotosUseCase
import com.nexters.fooddiary.domain.usecase.GetPhotosByMonthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getPhotosByMonthUseCase: GetPhotosByMonthUseCase,
    private val getAllPhotosUseCase: GetAllPhotosUseCase,
) : ViewModel() {

    private val _photoCountByDate = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val photoCountByDate: StateFlow<Map<LocalDate, Int>> = _photoCountByDate.asStateFlow()

    init {
        if (PermissionUtil.hasMediaPermission(context)) {
            loadAllPhotos()
        }
    }

    fun loadAllPhotos() {
        viewModelScope.launch {
            val allPhotos = getAllPhotosUseCase()
            _photoCountByDate.value = allPhotos
        }
    }

    fun loadPhotosForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            val photoCount = getPhotosByMonthUseCase(yearMonth)
            _photoCountByDate.value += photoCount
        }
    }
}
