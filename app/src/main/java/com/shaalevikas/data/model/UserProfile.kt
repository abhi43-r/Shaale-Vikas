package com.shaalevikas.data.model

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = UserRole.ALUMNI.value,
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val userRole: UserRole
        get() = UserRole.fromValue(role)
}
