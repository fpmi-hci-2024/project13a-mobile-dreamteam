package com.example.brainbridge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.brainbridge.Auth.AuthController
import com.example.brainbridge.Auth.AuthRepository
import com.example.brainbridge.Auth.LoginActivity
import com.example.brainbridge.CourseCatalog.CourseCatalogActivity
import com.example.brainbridge.UserCourses.UserCoursesActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var authController: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val authRepository = AuthRepository(FirebaseAuth.getInstance())
        authController = AuthController(authRepository)

        val currentUser = authController.checkCurrentUser()
        if (currentUser != null) {
            startActivity(Intent(this, UserCoursesActivity::class.java))
            //startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()
    }

}