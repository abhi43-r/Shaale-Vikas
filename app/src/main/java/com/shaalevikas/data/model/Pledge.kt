package com.shaalevikas.data.model

data class Pledge(
    val id: String = "",
    val needId: String = "",
    val userId: String = "",
    val donorName: String = "",
    val donorEmail: String = "",
    val amount: Double = 0.0,
    val note: String = "",
    val pledgedAt: Long = System.currentTimeMillis()
)
