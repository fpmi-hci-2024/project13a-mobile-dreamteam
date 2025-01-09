package com.example.brainbridge.CourseDetail.Test

import android.content.Context
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.brainbridge.Entity.Question
import com.example.brainbridge.databinding.ActivityTestBinding
import com.example.brainbridge.sampledata.Test
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TestController(
    private val context: Context,
    private val binding: ActivityTestBinding,
    private val activity: AppCompatActivity,
    private val courseId: String
) {

    private lateinit var testId: String
    private lateinit var userId: String
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private val userAnswers = mutableMapOf<Int, List<Int>>()

    fun initialize() {
        setupToolbar()
        loadTestData()
        setupButtonListeners()
    }

    private fun setupToolbar() {
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeButtonEnabled(true)
        activity.supportActionBar?.title = "Test"
    }

    private fun loadTestData() {
        testId = activity.intent.getStringExtra("testId") ?: run {
            activity.finish()
            return
        }
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(context, "You are not authorized", Toast.LENGTH_SHORT).show()
            activity.finish()
            return
        }

        val database = Firebase.database("https://brainbridge2025-default-rtdb.europe-west1.firebasedatabase.app")
        val testRef = database.getReference("tests").child(testId)

        testRef.get().addOnSuccessListener { snapshot ->
            val test = snapshot.getValue(Test::class.java)
            test?.let {
                questions = it.questions
                showQuestion(currentQuestionIndex)
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Ошибка загрузки теста", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupButtonListeners() {
        binding.prevButton.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                showQuestion(currentQuestionIndex)
            }
        }

        binding.nextButton.setOnClickListener {
            saveUserAnswers()
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                showQuestion(currentQuestionIndex)
            } else {
                showCompletionDialog()
            }
        }
    }

    private fun showQuestion(index: Int) {
        val question = questions[index]
        binding.questionText.text = question.question

        binding.optionsContainer.removeAllViews()

        question.options.forEachIndexed { optionIndex, option ->
            val checkBox = CheckBox(context)
            checkBox.text = option
            checkBox.isChecked = userAnswers[index]?.contains(optionIndex) == true
            binding.optionsContainer.addView(checkBox)
        }

        binding.nextButton.text = if (index == questions.size - 1) "Finish test" else "Next"
        binding.prevButton.isEnabled = index > 0
    }

    private fun saveUserAnswers() {
        val selectedIndices = mutableListOf<Int>()
        for (i in 0 until binding.optionsContainer.childCount) {
            val checkBox = binding.optionsContainer.getChildAt(i) as CheckBox
            if (checkBox.isChecked) {
                selectedIndices.add(i)
            }
        }
        userAnswers[currentQuestionIndex] = selectedIndices
    }

    private fun showCompletionDialog() {
        val score = calculateScore()
        val resultMessage = if (score >= 0.4) "Test completed! Your result: ${(score * 100).toInt()}%" else "Test failed. Your result: ${(score * 100).toInt()}%"

        AlertDialog.Builder(context)
            .setTitle("Test result")
            .setMessage(resultMessage)
            .setPositiveButton("OK") { _, _ ->
                markTestAsCompleted(userId, testId, score >= 0.4)
                returnToCourseDetail()
            }
            .setCancelable(false)
            .show()
    }

    private fun calculateScore(): Float {
        var correctAnswers = 0
        questions.forEachIndexed { index, question ->
            val selectedIndices = userAnswers[index] ?: emptyList()
            if (selectedIndices == question.correct_answers) {
                correctAnswers++
            }
        }
        return correctAnswers.toFloat() / questions.size
    }

    private fun markTestAsCompleted(userId: String, testId: String, isPassed: Boolean) {
        val database = Firebase.database("https://brainbridge2025-default-rtdb.europe-west1.firebasedatabase.app")
        val progressRef = database.getReference("userProgress").child(userId).child(courseId).child(testId) // Используем courseId

        progressRef.setValue(isPassed).addOnSuccessListener {
            Toast.makeText(context, if (isPassed) "Test completed" else "Test failed", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Ошибка сохранения прогресса", Toast.LENGTH_SHORT).show()
        }
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                returnToCourseDetail()
                true
            }
            else -> false
        }
    }

    fun onBackPressed() {
        returnToCourseDetail()
    }

    private fun returnToCourseDetail() {
        /*val intent = Intent(context, CourseDetailActivity::class.java)
        intent.putExtra("courseId", courseId)
        activity.startActivity(intent)*/
        activity.finish()
    }
}