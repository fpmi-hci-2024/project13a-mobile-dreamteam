package com.example.brainbridge.CourseDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brainbridge.Entity.ContentItem
import com.example.brainbridge.R

class ChapterMenuAdapter(
    private val chapters: Map<String, List<ContentItem>>,
    private val onItemClick: (ContentItem, String) -> Unit,
    private val expandedChapters: MutableSet<String>
) : RecyclerView.Adapter<ChapterMenuAdapter.ChapterViewHolder>() {

    private val sortedChapterKeys = chapters.keys.sorted()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chapter_menu, parent, false)
        return ChapterViewHolder(view, onItemClick, expandedChapters) { notifyItemChanged(it) }
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = sortedChapterKeys[position]
        val contentItems = chapters[chapter] ?: emptyList()
        holder.bind(chapter, contentItems)
    }

    override fun getItemCount(): Int = sortedChapterKeys.size

    class ChapterViewHolder(
        itemView: View,
        private val onItemClick: (ContentItem, String) -> Unit,
        private val expandedChapters: MutableSet<String>,
        private val onChapterClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val chapterTitle: TextView = itemView.findViewById(R.id.chapterTitle)
        private val iconExpand: ImageView = itemView.findViewById(R.id.iconExpand)
        private val contentItemsRecyclerView: RecyclerView = itemView.findViewById(R.id.contentItemsRecyclerView)

        fun bind(chapter: String, contentItems: List<ContentItem>) {
            chapterTitle.text = chapter

            contentItemsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            contentItemsRecyclerView.adapter = ContentItemMenuAdapter(contentItems) { contentItem ->
                onItemClick(contentItem, chapter)
            }
            contentItemsRecyclerView.visibility = if (expandedChapters.contains(chapter)) View.VISIBLE else View.GONE

            iconExpand.setImageResource(
                if (expandedChapters.contains(chapter)) R.drawable.ic_expand_less else R.drawable.ic_expand_more
            )

            chapterTitle.setOnClickListener {
                if (expandedChapters.contains(chapter)) {
                    expandedChapters.remove(chapter)
                } else {
                    expandedChapters.add(chapter)
                }
                onChapterClick(adapterPosition)
            }

            iconExpand.setOnClickListener {
                if (expandedChapters.contains(chapter)) {
                    expandedChapters.remove(chapter)
                } else {
                    expandedChapters.add(chapter)
                }
                onChapterClick(adapterPosition)
            }
        }
    }
}