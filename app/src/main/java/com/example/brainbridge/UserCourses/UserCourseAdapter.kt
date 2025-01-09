package com.example.brainbridge.UserCourses

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.brainbridge.Entity.UserCourse
import com.example.brainbridge.R

class UserCourseAdapter(var courses: List<UserCourse>) : RecyclerView.Adapter<UserCourseAdapter.UserCourseViewHolder>() {

    private var onItemClick: ((UserCourse) -> Unit)? = null

    fun setOnItemClickListener(listener: (UserCourse) -> Unit) {
        onItemClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_course, parent, false)
        return UserCourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserCourseViewHolder, position: Int) {
        val userCourse = courses[position]
        holder.bind(userCourse)
        holder.itemView.setOnClickListener { onItemClick?.invoke(userCourse) }
    }

    override fun getItemCount(): Int = courses.size

    class UserCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseName: TextView = itemView.findViewById(R.id.courseName)
        private val courseProgress: ProgressBar = itemView.findViewById(R.id.courseProgress)
        private val courseStatus: TextView = itemView.findViewById(R.id.courseStatus)

        fun bind(userCourse: UserCourse) {
            courseName.text = userCourse.name
            courseProgress.progress = userCourse.progress

            if (userCourse.progress == 100) {
                courseStatus.text = "Completed"
                courseStatus.setTextColor(Color.parseColor("#388E3C"))

                val progressDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.custom_progress_bar_completed)
                courseProgress.progressDrawable = progressDrawable
            } else {
                courseStatus.text = "${userCourse.progress}% In Progress"
                val textColor = ContextCompat.getColor(itemView.context, com.google.android.material.R.color.m3_default_color_secondary_text)
                courseStatus.setTextColor(textColor)

                val progressDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.custom_progress_bar)
                courseProgress.progressDrawable = progressDrawable
            }
        }
    }
}