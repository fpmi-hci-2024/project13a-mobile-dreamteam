package com.example.brainbridge.CourseDetail

import com.example.brainbridge.CourseRepository

class CourseDetailController(private val repository: CourseRepository) {
    fun enrollUserToCourse(userId: String, courseId: String, callback: (Boolean) -> Unit) {
        repository.enrollUserToCourse(userId, courseId, callback)
    }

    fun isUserEnrolled(userId: String, courseId: String, callback: (Boolean) -> Unit) {
        repository.isUserEnrolled(userId, courseId, callback)
    }
}