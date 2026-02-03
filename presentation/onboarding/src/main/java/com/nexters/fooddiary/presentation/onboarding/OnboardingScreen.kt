package com.nexters.fooddiary.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexters.fooddiary.core.ui.theme.Gray540
import com.nexters.fooddiary.core.ui.theme.PrimBase
import com.nexters.fooddiary.presentation.onboarding.R as OnboardingR
import kotlinx.coroutines.launch

private val OnboardingBackgroundColor = Color(0xFF191821)
private const val PAGE_COUNT = 6

@Composable
internal fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onComplete: () -> Unit = {},
) {
    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OnboardingBackgroundColor)
            .navigationBarsPadding()
    ) {
        // 중앙 - HorizontalPager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPage(
                pageNumber = page,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 우측 상단 - 건너뛰기 버튼 (마지막 페이지 제외)
        if (pagerState.currentPage < PAGE_COUNT - 1) {
            TextButton(
                onClick = onComplete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = stringResource(OnboardingR.string.onboarding_button_skip),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // 하단 - Pagination Dots + Button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pagination Dots
            PaginationDots(
                totalPages = PAGE_COUNT,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Button
            val buttonText = if (pagerState.currentPage == PAGE_COUNT - 1) {
                stringResource(OnboardingR.string.onboarding_button_start)
            } else {
                stringResource(OnboardingR.string.onboarding_button_next)
            }

            Button(
                onClick = {
                    if (pagerState.currentPage == PAGE_COUNT - 1) {
                        onComplete()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimBase,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun OnboardingPage(
    pageNumber: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 이미지 영역 Placeholder (사용자가 나중에 실제 이미지로 교체)
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "이미지 영역 ${pageNumber + 1}",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // 설명 텍스트
        Text(
            text = stringResource(getDescriptionForPage(pageNumber)),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )
    }
}

@Composable
private fun PaginationDots(
    totalPages: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (index == currentPage) PrimBase else Gray540,
                        shape = CircleShape
                    )
            )
        }
    }
}

private fun getDescriptionForPage(page: Int): Int {
    return when (page) {
        0 -> OnboardingR.string.onboarding_page_1_description
        1 -> OnboardingR.string.onboarding_page_2_description
        2 -> OnboardingR.string.onboarding_page_3_description
        3 -> OnboardingR.string.onboarding_page_4_description
        4 -> OnboardingR.string.onboarding_page_5_description
        5 -> OnboardingR.string.onboarding_page_6_description
        else -> OnboardingR.string.onboarding_page_1_description
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen()
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPagePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OnboardingBackgroundColor)
    ) {
        OnboardingPage(pageNumber = 0)
    }
}
