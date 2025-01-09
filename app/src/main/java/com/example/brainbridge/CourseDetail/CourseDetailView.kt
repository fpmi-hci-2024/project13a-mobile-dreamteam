package com.example.brainbridge.CourseDetail

import android.text.SpannableString
import com.example.brainbridge.Entity.ContentItem

interface CourseDetailView {
    fun setCourseName(name: String)
    fun setCourseDescription(description: String)
    fun setWhatYouWillLearnContent(content: SpannableString)
    fun setupRecyclerView(chapters: Map<String, List<ContentItem>>, expandedChapters: Set<String>)
    fun openTheory(theoryId: String, theoryName: String, courseId: String, chapterId: String)
    fun openTest(testId: String)
    fun toggleCourseMode(isChecked: Boolean)
    fun showEnrollButton()
    fun hideEnrollButton()
    fun showCourseToggle()
    fun hideCourseToggle()
    fun updateExpandedChapters(expandedChapters: Set<String>)
}