package com.example.brainbridge.CourseDetail

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brainbridge.BaseActivity
import com.example.brainbridge.Entity.Course
import com.example.brainbridge.FirebaseCourseRepository
import com.example.brainbridge.R
import com.example.brainbridge.CourseDetail.Test.TestActivity
import com.example.brainbridge.CourseDetail.Theory.TheoryActivity
import com.example.brainbridge.databinding.ActivityCourseDetailBinding
import com.google.firebase.auth.FirebaseAuth

class CourseDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityCourseDetailBinding
    private lateinit var course: Course
    private val expandedChapters = mutableSetOf<String>()
    private lateinit var courseDetailController: CourseDetailController

    override fun onResume() {
        super.onResume()
        updateBottomNavigationSelection()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setActivityContent(binding.root)

        courseDetailController = CourseDetailController(FirebaseCourseRepository())

        course = intent.getParcelableExtra<Course>("course") ?: run {
            finish()
            return
        }

        val mode = intent.getStringExtra("mode")
        if (mode == "tasks") {
            toggleCourseMode(true)
            updateModeButtons(true)
        } else {
            updateModeButtons(false)
        }

        checkIfUserEnrolled()

        initUI()

        setupRecyclerView()

        binding.backButton.setOnClickListener { onBackPressed() }

        binding.enrollButton.setOnClickListener { enrollUserToCourse() }

        binding.descriptionModeButton.setOnClickListener {
            toggleCourseMode(false)
            updateModeButtons(false)
        }
        binding.tasksModeButton.setOnClickListener {
            toggleCourseMode(true)
            updateModeButtons(true)
        }
    }

    private fun checkIfUserEnrolled() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        courseDetailController.isUserEnrolled(user.uid, course.id) { isEnrolled ->
            if (isEnrolled) {
                binding.enrollButton.visibility = View.GONE
                binding.modeToggleContainer.visibility = View.VISIBLE
            } else {
                binding.enrollButton.visibility = View.VISIBLE
                binding.modeToggleContainer.visibility = View.GONE
            }
        }
    }

    private fun initUI() {
        binding.courseName.text = course.name
        binding.courseDescription.text = course.description

        val spannableString = SpannableString(course.topics.joinToString("\n\n"))
        var startIndex = 0
        for (topic in course.topics) {
            val endIndex = startIndex + topic.length
            spannableString.setSpan(
                CustomBulletSpan(
                    bulletRadius = 10,
                    gapWidth = 16,
                    bulletColor = resources.getColor(R.color.orange)
                ),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            startIndex = endIndex + 2
        }
        binding.whatYouWillLearnContent.text = spannableString
    }

    private fun setupRecyclerView() {
        binding.contentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.contentsRecyclerView.adapter = ChapterMenuAdapter(course.contents, { contentItem, chapterId ->

            when (contentItem.type) {
                "theory" -> openTheory(contentItem.id, contentItem.name, course.id, chapterId)
                "test" -> openTest(contentItem.id, course.id)
            }
        }, expandedChapters)
    }

    private fun openTheory(theoryId: String, theoryName: String, courseId: String, chapterId: String) {

        val intent = Intent(this, TheoryActivity::class.java).apply {
            putExtra("theoryId", theoryId)
            putExtra("theoryName", theoryName)
            putExtra("courseId", courseId)
            putExtra("chapterId", chapterId)
        }
        startActivity(intent)
    }

    private fun openTest(testId: String, courseId: String) {
        val intent = Intent(this, TestActivity::class.java)
        intent.putExtra("testId", testId)
        intent.putExtra("courseId", courseId)
        startActivity(intent)
    }

    private fun enrollUserToCourse() {
        val user = FirebaseAuth.getInstance().currentUser ?: run {
            Toast.makeText(this, "Вы не авторизованы", Toast.LENGTH_SHORT).show()
            return
        }

        courseDetailController.enrollUserToCourse(user.uid, course.id) { success ->
            if (success) {
                binding.enrollButton.visibility = View.GONE
                binding.modeToggleContainer.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Ошибка записи на курс", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleCourseMode(isTasksMode: Boolean) {
        binding.courseDescription.visibility = if (isTasksMode) View.GONE else View.VISIBLE
        binding.whatYouWillLearnLabel.visibility = if (isTasksMode) View.GONE else View.VISIBLE
        binding.whatYouWillLearnContent.visibility = if (isTasksMode) View.GONE else View.VISIBLE
        binding.contentsLabel.visibility = View.VISIBLE
        binding.contentsRecyclerView.visibility = View.VISIBLE
    }

    private fun updateModeButtons(isTasksMode: Boolean) {
        if (isTasksMode) {
            binding.descriptionModeButton.setTextColor(ContextCompat.getColor(this, com.google.android.material.R.color.m3_default_color_secondary_text))
            binding.tasksModeButton.setTextColor(ContextCompat.getColor(this, com.google.android.material.R.color.m3_default_color_primary_text))
        } else {
            binding.descriptionModeButton.setTextColor(ContextCompat.getColor(this, com.google.android.material.R.color.m3_default_color_primary_text))
            binding.tasksModeButton.setTextColor(ContextCompat.getColor(this, com.google.android.material.R.color.m3_default_color_secondary_text))
        }
    }
}