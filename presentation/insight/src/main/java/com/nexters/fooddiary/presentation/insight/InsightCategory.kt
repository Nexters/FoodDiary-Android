package com.nexters.fooddiary.presentation.insight

internal enum class InsightCategoryType(
    val raw: String,
    val label: String,
) {
    KOREAN(raw = "korean", label = "한식"),
    JAPANESE(raw = "japanese", label = "일식"),
    CHINESE(raw = "chinese", label = "중식"),
    WESTERN(raw = "western", label = "양식"),
    HOME_COOKED(raw = "home_cooked", label = "집밥"),
    ETC(raw = "etc", label = "기타");

    companion object {
        fun fromRaw(raw: String): InsightCategoryType {
            return entries.firstOrNull { it.raw == raw } ?: ETC
        }
    }
}

internal fun String.toInsightCategoryLabel(): String {
    return InsightCategoryType.fromRaw(this).label
}
