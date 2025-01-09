package com.example.brainbridge.sampledata

import com.example.brainbridge.Entity.Question

data class Test(
    val id: String = "",
    val course_id: String = "",
    val chapter_id: String = "",
    val questions: List<Question> = emptyList()
)