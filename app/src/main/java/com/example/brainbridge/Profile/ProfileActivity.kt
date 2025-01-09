package com.example.brainbridge.Profile

import android.content.Intent
import android.os.Bundle
import com.example.brainbridge.BaseActivity
import com.example.brainbridge.MainActivity
import com.example.brainbridge.databinding.ActivityProfileBinding
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.FirebaseAuth


class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var mAuth: FirebaseAuth? = null

    override fun onResume() {
        super.onResume()
        updateBottomNavigationSelection()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setActivityContent(binding.root)

        mAuth = FirebaseAuth.getInstance();
        binding.btnExit.setOnClickListener{
            mAuth!!.signOut()
            val intent = Intent(
                this,
                MainActivity::class.java
            )
            startActivity(intent)}
    }
}