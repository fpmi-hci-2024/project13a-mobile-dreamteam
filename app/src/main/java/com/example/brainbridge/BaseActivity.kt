package com.example.brainbridge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.example.brainbridge.CourseCatalog.CourseCatalogActivity
import com.example.brainbridge.CourseDetail.CourseDetailActivity
import com.example.brainbridge.Profile.ProfileActivity
import com.example.brainbridge.UserCourses.UserCoursesActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {

    protected lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_courses -> {
                    if (this !is UserCoursesActivity) {
                        navigateToActivity(UserCoursesActivity::class.java)
                    }
                    true
                }
                R.id.navigation_catalog -> {
                    if (this is CourseDetailActivity) {
                        true
                    } else if (this !is CourseCatalogActivity) {
                        navigateToActivity(CourseCatalogActivity::class.java)
                    }
                    true
                }
                R.id.navigation_profile -> {
                    if (this !is ProfileActivity) {
                        navigateToActivity(ProfileActivity::class.java)
                    }
                    true
                }
                else -> false
            }
        }

        updateBottomNavigationSelection()
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    fun setActivityContent(view: View) {
        val contentFrame = findViewById<FrameLayout>(R.id.contentFrame)
        contentFrame.removeAllViews()
        contentFrame.addView(view)
    }

    protected fun updateBottomNavigationSelection() {
        when (this) {
            is CourseDetailActivity -> bottomNavigationView.selectedItemId = R.id.navigation_catalog
            is UserCoursesActivity -> bottomNavigationView.selectedItemId = R.id.navigation_courses
            is CourseCatalogActivity -> bottomNavigationView.selectedItemId = R.id.navigation_catalog
            is ProfileActivity -> bottomNavigationView.selectedItemId = R.id.navigation_profile
            else -> bottomNavigationView.selectedItemId = R.id.navigation_courses
        }
    }
}