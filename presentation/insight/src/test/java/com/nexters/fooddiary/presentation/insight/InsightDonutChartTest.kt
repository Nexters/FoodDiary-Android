package com.nexters.fooddiary.presentation.insight

import com.nexters.fooddiary.presentation.insight.donut.calculateAnnularSectorCentroidRadius
import com.nexters.fooddiary.presentation.insight.donut.calculateDividerStrokeWidthPx
import com.nexters.fooddiary.presentation.insight.donut.calculateDonutLabelRadius
import com.nexters.fooddiary.presentation.insight.donut.calculateDonutSegmentGeometries
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InsightDonutChartTest {

    @Test
    fun `세그먼트의 sweep 합은 gap을 제외한 전체 각도와 같다`() {
        val gapAngle = 2f

        val geometries = calculateDonutSegmentGeometries(
            values = listOf(20f, 28f),
            startAngle = -90f,
            gapAngle = gapAngle,
        )

        val totalSweep = geometries.sumOf { it.sweepAngle.toDouble() }.toFloat()

        assertEquals(356f, totalSweep, 0.001f)
    }

    @Test
    fun `0 이하 값은 세그먼트 계산에서 제외된다`() {
        val geometries = calculateDonutSegmentGeometries(
            values = listOf(10f, 0f, -4f, 30f),
            startAngle = -90f,
            gapAngle = 2f,
        )

        assertEquals(2, geometries.size)
        assertTrue(geometries.all { it.sweepAngle > 0f })
    }

    @Test
    fun `양수 세그먼트가 하나만 있으면 전체 링을 사용한다`() {
        val geometries = calculateDonutSegmentGeometries(
            values = listOf(0f, 32f, -1f),
            startAngle = -90f,
            gapAngle = 2f,
        )

        assertEquals(1, geometries.size)
        assertEquals(360f, geometries.single().sweepAngle, 0.001f)
    }

    @Test
    fun `넓은 세그먼트의 무게중심 반지름은 링 내부에 위치한다`() {
        val centroidRadius = calculateAnnularSectorCentroidRadius(
            outerRadius = 123f,
            innerRadius = 44.28f,
            sweepAngle = 208f,
        )

        assertTrue(centroidRadius > 44.28f)
        assertTrue(centroidRadius < 83.64f)
    }

    @Test
    fun `넓은 세그먼트 라벨 반지름은 무게중심보다 바깥으로 이동한다`() {
        val centroidRadius = calculateAnnularSectorCentroidRadius(
            outerRadius = 123f,
            innerRadius = 44.28f,
            sweepAngle = 208f,
        )
        val labelRadius = calculateDonutLabelRadius(
            outerRadius = 123f,
            innerRadius = 44.28f,
            sweepAngle = 208f,
        )

        assertTrue(labelRadius > centroidRadius)
        assertTrue(labelRadius <= 83.64f)
    }

    @Test
    fun `좁은 세그먼트 라벨 반지름은 무게중심과 동일하다`() {
        val centroidRadius = calculateAnnularSectorCentroidRadius(
            outerRadius = 123f,
            innerRadius = 44.28f,
            sweepAngle = 100f,
        )
        val labelRadius = calculateDonutLabelRadius(
            outerRadius = 123f,
            innerRadius = 44.28f,
            sweepAngle = 100f,
        )

        assertEquals(centroidRadius, labelRadius, 0.001f)
    }

    @Test
    fun `divider 두께는 gapAngle이 0 이하면 0이다`() {
        assertEquals(0f, calculateDividerStrokeWidthPx(outerRadius = 123f, gapAngle = 0f), 0.001f)
        assertEquals(0f, calculateDividerStrokeWidthPx(outerRadius = 123f, gapAngle = -2f), 0.001f)
    }

    @Test
    fun `divider 두께는 바깥 반지름과 gapAngle에 비례한다`() {
        val dividerWidth = calculateDividerStrokeWidthPx(
            outerRadius = 123f,
            gapAngle = 2f,
        )

        assertEquals(4.29f, dividerWidth, 0.01f)
    }
}
