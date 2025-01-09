package com.example.brainbridge.CourseCatalog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brainbridge.BaseActivity
import com.example.brainbridge.CourseDetail.CourseDetailActivity
import com.example.brainbridge.Entity.Course
import com.example.brainbridge.FirebaseCourseRepository
import com.example.brainbridge.R
import com.example.brainbridge.UserCourses.UserCoursesActivity
import com.example.brainbridge.databinding.ActivityCourseCatalogBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.ref.WeakReference

class CourseCatalogActivity : BaseActivity(), CourseCatalogView {

    private lateinit var courseAdapter: CourseAdapter
    private lateinit var courseController: CourseController
    private lateinit var binding: ActivityCourseCatalogBinding

    override fun onResume() {
        super.onResume()
        updateBottomNavigationSelection()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseCatalogBinding.inflate(layoutInflater)
        setActivityContent(binding.root)

        courseAdapter = CourseAdapter(emptyList())
        binding.coursesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.coursesRecyclerView.adapter = courseAdapter

        courseController = CourseController(FirebaseCourseRepository(), WeakReference(this))
        courseController.loadCourses()

        setupFilterButton(binding.filterButton)
    }

    override fun setUpRecyclerView(courses: List<Course>) {
        courseAdapter = CourseAdapter(courses)
        binding.coursesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.coursesRecyclerView.adapter = courseAdapter
        courseAdapter.onItemClickListener = { course ->
            courseController.handleCourseItemClick(course)
        }
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToCourseDetail(course: Course) {
        val intent = Intent(this, CourseDetailActivity::class.java).apply {
            putExtra("course", course)
        }
        startActivity(intent)
    }

    private fun setupFilterButton(filterButton: ImageButton) {
        filterButton.setOnClickListener { showFilterMenu(it) }
    }

    private fun showFilterMenu(view: View) {
        val popupMenu = PopupMenu(this, view).apply {
            menuInflater.inflate(R.menu.filter_menu, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_search -> showSearchDialog()
                    R.id.action_filter -> showCategoryFilterDialog()
                    R.id.action_reset -> resetFilters()
                    else -> false
                }
                true
            }
        }
        popupMenu.show()
    }

    private fun resetFilters() {
        courseController.loadCourses()
    }

    private fun showSearchDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search, null)
        val editTextSearch = dialogView.findViewById<EditText>(R.id.editTextSearch)

        AlertDialog.Builder(this)
            .setTitle("Search by Name")
            .setView(dialogView)
            .setPositiveButton("Search") { _, _ ->
                val query = editTextSearch.text.toString()
                val filteredCourses = courseController.filterCoursesByName(courseAdapter.courses, query)
                courseAdapter.courses = filteredCourses
                courseAdapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCategoryFilterDialog() {
        val categories = listOf("Topic 1", "Topic 2", "Topic 4")
        val checkedItems = BooleanArray(categories.size) { false }

        AlertDialog.Builder(this)
            .setTitle("Filter by Category")
            .setMultiChoiceItems(categories.toTypedArray(), checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Apply") { _, _ ->
                val selectedCategories = categories.filterIndexed { index, _ -> checkedItems[index] }
                val filteredCourses = courseController.filterCoursesByCategory(courseAdapter.courses, selectedCategories)
                courseAdapter.courses = filteredCourses
                courseAdapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}