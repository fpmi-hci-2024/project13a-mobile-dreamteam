package com.example.brainbridge.CourseCatalog

import android.util.Log
import com.example.brainbridge.CourseRepository
import com.example.brainbridge.Entity.Course
import java.lang.ref.WeakReference

class CourseController(
    private val repository: CourseRepository,
    private val view: WeakReference<CourseCatalogView>
) {

    fun loadCourses() {

        repository.loadCourses { courses ->
            if (courses != null) {
                view.get()?.setUpRecyclerView(courses)
            } else {
                view.get()?.showError("Failed to load courses")
            }
        }
    }

    fun handleCourseItemClick(course: Course) {
        view.get()?.navigateToCourseDetail(course)
    }

    fun filterCoursesByName(courses: List<Course>, query: String): List<Course> {
        return courses.filter { it.name.contains(query, true) }
    }

    fun filterCoursesByCategory(courses: List<Course>, categories: List<String>): List<Course> {
        return courses.filter { course -> categories.any { course.topics.contains(it) } }
    }
}