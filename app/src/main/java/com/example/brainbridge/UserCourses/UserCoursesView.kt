package com.example.brainbridge.UserCourses

import com.example.brainbridge.Entity.Course
import com.example.brainbridge.Entity.UserCourse

interface UserCoursesView {
    fun showInProgressCourses(courses: List<UserCourse>)
    fun showFinishedCourses(courses: List<UserCourse>)
    fun setUpRecyclerView(courses: List<UserCourse>)
    fun showError(message: String)
    fun navigateToCourseDetail(course: Course, mode: String)
}