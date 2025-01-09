package com.example.brainbridge.CourseDetail.Theory

import android.util.Log
import com.example.brainbridge.Entity.UserProgress
import com.example.brainbridge.sampledata.Test
import com.example.brainbridge.sampledata.Theory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class UserProgress(
    val userId: String,
    val courseId: String,
    val completedItems: MutableMap<String, Boolean>
)

class TheoryRepository(private val firebaseDatabase: FirebaseDatabase) {

    fun getTheories(courseId: String, callback: (List<Theory>) -> Unit) {
        val theoriesRef = firebaseDatabase.reference.child("theory")
        theoriesRef.orderByChild("course_id").equalTo(courseId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val theories = mutableListOf<Theory>()
                for (theorySnapshot in snapshot.children) {
                    val theory = theorySnapshot.getValue(Theory::class.java)
                    if (theory != null) {
                        theories.add(theory)
                    }
                }
                callback(theories)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TheoryRepository", "Failed to fetch theories: ${error.message}")
                callback(emptyList())
            }
        })
    }

    fun getTestsForChapter(chapterId: String, callback: (List<Test>) -> Unit) {
        val testsRef = firebaseDatabase.reference.child("tests")
        testsRef.orderByChild("chapter_id").equalTo(chapterId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tests = mutableListOf<Test>()
                for (testSnapshot in snapshot.children) {
                    val test = testSnapshot.getValue(Test::class.java)
                    if (test != null) {
                        tests.add(test)
                    }
                }
                callback(tests)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TheoryRepository", "Failed to fetch tests: ${error.message}")
                callback(emptyList())
            }
        })
    }

    fun getNextChapter(currentChapterId: String, callback: (String?) -> Unit) {
        val chaptersRef = firebaseDatabase.reference.child("chapters")
        chaptersRef.orderByKey().startAfter(currentChapterId).limitToFirst(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nextChapterId = snapshot.children.firstOrNull()?.key
                callback(nextChapterId)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TheoryRepository", "Failed to fetch next chapter: ${error.message}")
                callback(null)
            }
        })
    }

    fun getUserProgress(userId: String, courseId: String, callback: (UserProgress) -> Unit) {
        val progressRef = firebaseDatabase.reference.child("userProgress").child(userId)
        progressRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val completedItems = mutableMapOf<String, Boolean>()
                val courseSnapshot = snapshot.child(courseId)
                if (courseSnapshot.exists()) {
                    for (itemSnapshot in courseSnapshot.children) {
                        val itemId = itemSnapshot.key ?: continue
                        val completed = itemSnapshot.value as? Boolean ?: false
                        completedItems[itemId] = completed
                    }
                }
                val userProgress = UserProgress(userId, courseId, completedItems)
                callback(userProgress)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun updateProgress(userProgress: UserProgress) {
        val progressRef = firebaseDatabase.reference.child("userProgress").child(userProgress.userId).child(userProgress.courseId)
        progressRef.setValue(userProgress.completedItems)
    }
}