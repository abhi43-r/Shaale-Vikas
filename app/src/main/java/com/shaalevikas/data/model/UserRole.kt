package com.shaalevikas.data.model

enum class UserRole(val value: String) {
    ADMIN("admin"),
    ALUMNI("alumni");

    companion object {
        fun fromValue(value: String?): UserRole = entries.firstOrNull { it.value == value } ?: ALUMNI
    }
}
