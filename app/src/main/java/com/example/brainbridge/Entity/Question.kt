package com.example.brainbridge.Entity

data class Question(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correct_answers: List<Int> = emptyList()
)