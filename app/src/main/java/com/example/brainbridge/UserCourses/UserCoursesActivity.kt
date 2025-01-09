package com.example.brainbridge.UserCourses

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brainbridge.BaseActivity
import com.example.brainbridge.CourseCatalog.CourseAdapter
import com.example.brainbridge.CourseDetail.CourseDetailActivity
import com.example.brainbridge.Entity.Course
import com.example.brainbridge.Entity.UserCourse
import com.example.brainbridge.databinding.ActivityUserCoursesBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import java.lang.ref.WeakReference

class UserCoursesActivity : BaseActivity(), UserCoursesView {

    private lateinit var adapter: UserCourseAdapter
    private lateinit var controller: UserController
    private lateinit var binding: ActivityUserCoursesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserCoursesBinding.inflate(layoutInflater)
        setActivityContent(binding.root)

        adapter = UserCourseAdapter(emptyList())
        binding.coursesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.coursesRecyclerView.adapter = adapter

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(this, "Вы не авторизованы", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        controller = UserController(WeakReference(this))
        controller.loadUserCourses(userId)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("In Progress"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Finished"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { controller.onTabSelected(it) }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        updateBottomNavigationSelection()

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        controller.loadUserCourses(userId)
    }

    override fun setUpRecyclerView(courses: List<UserCourse>) {
        adapter = UserCourseAdapter(courses)
        binding.coursesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.coursesRecyclerView.adapter = adapter
        adapter.setOnItemClickListener { userCourse ->
            controller.handleCourseItemClick(userCourse)
        }
    }

    override fun showInProgressCourses(courses: List<UserCourse>) {
        adapter.courses = courses
        adapter.notifyDataSetChanged()
    }

    override fun showFinishedCourses(courses: List<UserCourse>) {
        adapter.courses = courses
        adapter.notifyDataSetChanged()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToCourseDetail(course: Course, mode: String) {
        val intent = Intent(this, CourseDetailActivity::class.java).apply {
            putExtra("course", course)
            putExtra("mode", mode)
        }
        startActivity(intent)
    }
}