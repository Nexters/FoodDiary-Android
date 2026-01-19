package com.nexters.fooddiary.presentation.home.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.fooddiary.domain.usecase.GetAllPhotosUseCase
import com.nexters.fooddiary.domain.usecase.GetPhotosByMonthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getPhotosByMonthUseCase: GetPhotosByMonthUseCase,
    private val getAllPhotosUseCase: GetAllPhotosUseCase,
) : ViewModel() {

    private val _photoCountByDate = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val photoCountByDate: StateFlow<Map<LocalDate, Int>> = _photoCountByDate.asStateFlow()

    init {
        viewModelScope.launch {
            Log.d("CalendarViewModel", "Starting full scan...")
            val startTime = System.currentTimeMillis()
            val allPhotos = getAllPhotosUseCase()
            val endTime = System.currentTimeMillis()
            Log.d("CalendarViewModel", "Full scan completed in ${endTime - startTime}ms")
            
            _photoCountByDate.value = allPhotos
        }
    }

    fun loadPhotosForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val photoCount = getPhotosByMonthUseCase(yearMonth)
            val endTime = System.currentTimeMillis()
            Log.d("CalendarViewModel", "Month scan for $yearMonth completed in ${endTime - startTime}ms")
            
            _photoCountByDate.value += photoCount
        }
    }
}
