package com.nexters.fooddiary.presentation.home.calendar

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.fooddiary.core.common.permission.PermissionUtil
import com.nexters.fooddiary.presentation.component.calendar.MonthlyCalendar
import com.nexters.fooddiary.presentation.home.R
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val initialDate = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var hasPermission by remember { mutableStateOf(false) }

    val photoCountByDate by viewModel.photoCountByDate.collectAsState()

    // 필요 권한
    val requiredPermission = PermissionUtil.getRequiredMediaPermission()

    // 권한 체크 함수
    val checkPermission: () -> Boolean = {
        PermissionUtil.hasMediaPermission(context)
    }

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.loadPhotosForMonth(YearMonth.from(initialDate))
            viewModel.loadAllPhotos()
        }
    }

    // 권한 체크
    LaunchedEffect(Unit) {
        hasPermission = checkPermission()
        if (!hasPermission) {
            permissionLauncher.launch(requiredPermission)
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2E))
            .padding(16.dp)
    ) {
        if (hasPermission) {
            MonthlyCalendar(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
                photoCountByDate = photoCountByDate,
                onMonthChanged = { yearMonth ->
                    viewModel.loadPhotosForMonth(yearMonth)
                },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // 권한 없음 메시지
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.permission_required_message),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        permissionLauncher.launch(requiredPermission)
                    }
                ) {
                    Text(stringResource(id = R.string.permission_request_button))
                }
            }
        }
    }
}
