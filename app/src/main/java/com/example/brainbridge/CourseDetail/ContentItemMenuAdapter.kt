package com.example.brainbridge.CourseDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brainbridge.Entity.ContentItem
import com.example.brainbridge.R

class ContentItemMenuAdapter(
    private val contentItems: List<ContentItem>,
    private val onItemClick: (ContentItem) -> Unit
) : RecyclerView.Adapter<ContentItemMenuAdapter.ContentItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_content_menu, parent, false)
        return ContentItemViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ContentItemViewHolder, position: Int) {
        holder.bind(contentItems[position])
    }

    override fun getItemCount(): Int = contentItems.size

    class ContentItemViewHolder(
        itemView: View,
        private val onItemClick: (ContentItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val contentText: TextView = itemView.findViewById(R.id.contentText)

        fun bind(contentItem: ContentItem) {
            contentText.text = contentItem.name

            itemView.setOnClickListener {
                onItemClick(contentItem)
            }
        }
    }
}