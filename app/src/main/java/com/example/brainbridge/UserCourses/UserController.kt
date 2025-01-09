package com.example.brainbridge.UserCourses

import android.util.Log
import com.example.brainbridge.Entity.UserCourse
import com.example.brainbridge.FirebaseCourseRepository
import java.lang.ref.WeakReference

class UserController(private val view: WeakReference<UserCoursesView>) {

    private val courseRepository = FirebaseCourseRepository()
    private var userCourses: List<UserCourse> = emptyList()

    fun loadUserCourses(userId: String) {
        courseRepository.loadUserCourses(userId) { courses ->
            if (courses != null) {
                userCourses = courses
                view.get()?.setUpRecyclerView(userCourses)
                showInProgressCourses()
            } else {
                view.get()?.showError("Failed to load courses")
            }
        }
    }

    fun onTabSelected(tabPosition: Int) {
        when (tabPosition) {
            0 -> showInProgressCourses()
            1 -> showFinishedCourses()
        }
    }

    private fun showInProgressCourses() {
        val inProgressCourses = userCourses.filter { !it.isFinished }
        view.get()?.showInProgressCourses(inProgressCourses)
    }

    private fun showFinishedCourses() {
        val finishedCourses = userCourses.filter { it.isFinished }
        view.get()?.showFinishedCourses(finishedCourses)
    }

    fun handleCourseItemClick(userCourse: UserCourse) {
        courseRepository.getCourseById(userCourse.courseId) { course ->
            if (course != null) {
                view.get()?.navigateToCourseDetail(course, "tasks")
            } else {
                view.get()?.showError("Курс не найден")
            }
        }
    }
}