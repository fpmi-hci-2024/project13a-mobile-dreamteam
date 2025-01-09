package com.example.brainbridge.CourseDetail.Theory

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.brainbridge.databinding.ActivityTheoryBinding

class TheoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTheoryBinding
    private lateinit var viewModel: TheoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTheoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(TheoryViewModel::class.java)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val theoryId = intent.getStringExtra("theoryId") ?: ""
        val courseId = intent.getStringExtra("courseId") ?: "course1"

        viewModel.loadTheories(courseId, theoryId)

        binding.buttonBack.setOnClickListener {
            viewModel.loadPreviousTheory()
        }

        binding.buttonNext.setOnClickListener {
            viewModel.loadNextTheory()
        }

        viewModel.currentTheory.observe(this) { theory ->
            theory?.let {
                binding.webViewTheory.loadDataWithBaseURL(
                    null,
                    it.content.split("\n").joinToString("<br>"),
                    "text/html",
                    "UTF-8",
                    null
                )
                binding.buttonBack.visibility =
                    if (viewModel.isFirstTheoryInChapter()) View.GONE else View.VISIBLE
                binding.buttonNext.text =
                    if (viewModel.isLastTheoryInChapter()) "Go to the test" else "Next"

                supportActionBar?.title = it.name
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}