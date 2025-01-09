package com.example.brainbridge.CourseCatalog

import com.example.brainbridge.Entity.Course

interface CourseCatalogView {
    fun setUpRecyclerView(courses: List<Course>)
    fun showError(message: String)
    fun navigateToCourseDetail(course: Course)
}