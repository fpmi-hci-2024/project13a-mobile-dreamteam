package com.example.brainbridge.CourseCatalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brainbridge.Entity.Course
import com.example.brainbridge.R

class CourseAdapter(
    var courses: List<Course>
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    var onItemClickListener: ((Course) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course)
        holder.itemView.setOnClickListener { onItemClickListener?.invoke(course) }
    }

    override fun getItemCount(): Int = courses.size

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseName: TextView = itemView.findViewById(R.id.courseName)
        private val courseDescription: TextView = itemView.findViewById(R.id.courseDescription)
        private val courseSubject: TextView = itemView.findViewById(R.id.courseSubject)

        fun bind(course: Course) {
            courseName.text = course.name
            courseDescription.text = course.description
            courseSubject.text = course.topics.joinToString(", ")
        }
    }
}