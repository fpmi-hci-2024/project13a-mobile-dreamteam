package com.example.brainbridge.CourseDetail.Theory

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.brainbridge.CourseDetail.Test.TestActivity
import com.example.brainbridge.Entity.UserProgress
import com.example.brainbridge.sampledata.Theory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class TheoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TheoryRepository(FirebaseDatabase.getInstance())
    private val _currentTheory = MutableLiveData<Theory?>()
    val currentTheory: LiveData<Theory?> get() = _currentTheory

    private val theories = mutableListOf<Theory>()
    private var currentPosition = 0
    private var userProgress: UserProgress? = null

    fun loadTheories(courseId: String, theoryId: String) {
        repository.getTheories(courseId) { theoriesList ->
            theories.clear()
            theories.addAll(theoriesList.sortedBy { it.chapter_id })
            currentPosition = theories.indexOfFirst { it.id == theoryId }
            if (currentPosition != -1) {
                loadTheory(currentPosition)
            }
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            repository.getUserProgress(user.uid, courseId) { progress ->
                userProgress = progress
                if (theories.isNotEmpty()) {
                    loadTheory(currentPosition)
                }
            }
        }
    }

    fun loadPreviousTheory() {
        if (currentPosition > 0) {
            currentPosition--
            loadTheory(currentPosition)
        }
    }

    fun loadNextTheory() {
        if (isLastTheoryInChapter()) {
            openNextTest(theories[currentPosition].chapter_id)
        } else if (currentPosition < theories.size - 1) {
            currentPosition++
            loadTheory(currentPosition)
        }
    }

    private fun loadTheory(position: Int) {
        val theory = theories[position]
        _currentTheory.value = theory

        userProgress?.let { progress ->
            progress.completedItems[theory.chapter_id] = true
            repository.updateProgress(progress)
        }
    }

    fun isLastTheoryInChapter(): Boolean {
        return currentPosition == theories.size - 1 || theories[currentPosition].chapter_id != theories[currentPosition + 1].chapter_id
    }

    fun isFirstTheoryInChapter(): Boolean {
        return currentPosition == 0 || theories[currentPosition - 1].chapter_id != theories[currentPosition].chapter_id
    }

    private fun openNextTest(chapterId: String) {
        repository.getTestsForChapter(chapterId) { tests ->
            val currentCourseId = theories[currentPosition].course_id
            val relevantTest = tests.find { test ->
                test.chapter_id == chapterId && test.course_id == currentCourseId
            }

            if (relevantTest != null) {
                val intent = Intent(getApplication(), TestActivity::class.java)
                intent.putExtra("testId", relevantTest.id)
                intent.putExtra("courseId", currentCourseId) // Передаем courseId
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                getApplication<Application>().startActivity(intent)
            } else {
                openNextChapter(chapterId)
            }
        }
    }

    private fun openNextChapter(currentChapterId: String) {
        repository.getNextChapter(currentChapterId) { nextChapterId ->
            if (nextChapterId != null) {
                val nextTheory = theories.firstOrNull { it.chapter_id == nextChapterId }
                if (nextTheory != null) {
                    val intent = Intent(getApplication(), TheoryActivity::class.java)
                    intent.putExtra("theoryId", nextTheory.id)
                    intent.putExtra("theoryName", nextTheory.name)
                    intent.putExtra("courseId", nextTheory.course_id)
                    intent.putExtra("chapterId", nextTheory.chapter_id)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    getApplication<Application>().startActivity(intent)
                } else {
                    Toast.makeText(getApplication(), "No theories available for the next chapter.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(getApplication(), "Course completed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}