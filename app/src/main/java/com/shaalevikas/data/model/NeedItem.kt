package com.shaalevikas.data.model

data class NeedItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val category: String = "",
    val priority: String = "Medium",
    val estimatedCost: Double = 0.0,
    val amountCollected: Double = 0.0,
    val heroImageUrl: String? = null,
    val beforeImageUrl: String? = null,
    val afterImageUrl: String? = null,
    val status: String = "Open",
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val progress: Float
        get() = if (estimatedCost <= 0.0) 0f else (amountCollected / estimatedCost).toFloat().coerceIn(0f, 1f)

    val amountRemaining: Double
        get() = (estimatedCost - amountCollected).coerceAtLeast(0.0)
}
