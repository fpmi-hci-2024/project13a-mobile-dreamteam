package com.example.brainbridge

import com.example.brainbridge.Entity.Course
import com.example.brainbridge.Entity.UserCourse
import com.example.brainbridge.Entity.UserProgress
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

interface CourseRepository {
    fun loadCourses(callback: (List<Course>) -> Unit)
    fun enrollUserToCourse(userId: String, courseId: String, callback: (Boolean) -> Unit)
    fun isUserEnrolled(userId: String, courseId: String, callback: (Boolean) -> Unit) // Добавляем метод
}

class FirebaseCourseRepository : CourseRepository {
    private val database = Firebase.database("https://brainbridge2025-default-rtdb.europe-west1.firebasedatabase.app")
    private val coursesRef = database.getReference("courses")
    private val userCoursesRef = database.getReference("userCourses")

    override fun loadCourses(callback: (List<Course>) -> Unit) {
        coursesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courses = snapshot.children.mapNotNull { it.getValue(Course::class.java)?.copy(id = it.key ?: "") }
                callback(courses)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
            }
        })
    }

    fun getCourseById(courseId: String, callback: (Course?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val courseRef = database.getReference("courses").child(courseId)

        courseRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val course = dataSnapshot.getValue(Course::class.java)
                callback(course)
            } else {
                callback(null)
            }
        }.addOnFailureListener {
            callback(null)
        }
    }


    override fun enrollUserToCourse(userId: String, courseId: String, callback: (Boolean) -> Unit) {
        userCoursesRef.child(userId).child(courseId).setValue(true).addOnCompleteListener {
            callback(it.isSuccessful)
        }
    }
    override fun isUserEnrolled(userId: String, courseId: String, callback: (Boolean) -> Unit) {
        userCoursesRef.child(userId).child(courseId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val value = task.result?.getValue(Any::class.java)
                when (value) {
                    is Boolean -> callback(value)
                    is HashMap<*, *> -> callback(value.isNotEmpty())
                    else -> callback(false)
                }
            } else {
                callback(false)
            }
        }
    }

    fun loadUserCourses(userId: String, callback: (List<UserCourse>) -> Unit) {
        val userCoursesRef = database.getReference("userCourses").child(userId)
        val coursesRef = database.getReference("courses")
        val userProgressRef = database.getReference("userProgress").child(userId)

        userCoursesRef.get().addOnSuccessListener { userCoursesSnapshot ->
            val userCourses = mutableListOf<UserCourse>()
            val courseIds = userCoursesSnapshot.children.mapNotNull { it.key }

            courseIds.forEach { courseId ->
                coursesRef.child(courseId).get().addOnSuccessListener { courseSnapshot ->
                    val course = courseSnapshot.getValue(Course::class.java)
                    if (course != null) {
                        userProgressRef.child(courseId).get().addOnSuccessListener { progressSnapshot ->
                            val completedCount = progressSnapshot.children.count { it.getValue(Boolean::class.java) == true }
                            val totalItems = course.contents.values.flatten().size
                            val progress = (completedCount.toFloat() / totalItems * 100).toInt()
                            val isFinished = completedCount == totalItems

                            val userCourse = UserCourse(
                                courseId = courseId,
                                name = course.name,
                                progress = progress,
                                isFinished = isFinished
                            )
                            userCourses.add(userCourse)

                            if (userCourses.size == courseIds.size) {
                                callback(userCourses)
                            }
                        }
                    }
                }
            }
        }.addOnFailureListener {
            callback(emptyList())
        }
    }
}