package com.shaalevikas.data.model

enum class NeedSortOption {
    LATEST,
    HIGHEST_COST,
    MOST_FUNDED,
    MOST_URGENT
}

data class NeedFilter(
    val query: String = "",
    val sortOption: NeedSortOption = NeedSortOption.LATEST,
    val status: String = "All"
)
