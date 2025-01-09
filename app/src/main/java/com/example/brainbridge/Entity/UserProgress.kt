package com.example.brainbridge.Entity

data class UserProgress(
    val userId: String,
    val courseId: String,
    val completedItems: MutableMap<String, Boolean>
)