package com.nexters.fooddiary.presentation.insight

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nexters.fooddiary.presentation.insight.rankingbubble.bubbleColor
import com.nexters.fooddiary.presentation.insight.rankingbubble.bubbleSize
import org.junit.Assert.assertEquals
import org.junit.Test

class InsightRankingBubbleCardTest {

    @Test
    fun `1등 버블은 160dp와 기본 주황색을 사용한다`() {
        val item = InsightRankingBubbleItemUiModel(
            rank = 1,
            regionName = "합정동",
            visitCount = 99,
        )

        assertEquals(160.dp, item.bubbleSize())
        assertEquals(Color(0xFFFE670E), item.bubbleColor())
    }

    @Test
    fun `2등 버블은 110dp와 주황색 60퍼센트 투명도를 사용한다`() {
        val item = InsightRankingBubbleItemUiModel(
            rank = 2,
            regionName = "성수동",
            visitCount = 40,
        )

        assertEquals(110.dp, item.bubbleSize())
        assertEquals(Color(0xFFFE670E).copy(alpha = 0.6f), item.bubbleColor())
    }

    @Test
    fun `3등 버블은 80dp와 주황색 20퍼센트 투명도를 사용한다`() {
        val item = InsightRankingBubbleItemUiModel(
            rank = 3,
            regionName = "연남동",
            visitCount = 30,
        )

        assertEquals(80.dp, item.bubbleSize())
        assertEquals(Color(0xFFFE670E).copy(alpha = 0.2f), item.bubbleColor())
    }
}
