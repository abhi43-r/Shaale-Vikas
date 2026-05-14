package com.shaalevikas.data.model

data class HallOfFameEntry(
    val userId: String = "",
    val donorName: String = "",
    val donorEmail: String = "",
    val totalContribution: Double = 0.0,
    val pledgeCount: Int = 0
)
