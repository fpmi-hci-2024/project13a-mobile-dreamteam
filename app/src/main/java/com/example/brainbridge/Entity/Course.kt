package com.example.brainbridge.Entity

import android.os.Parcel
import android.os.Parcelable

data class Course(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var topics: List<String> = emptyList(),
    var contents: Map<String, List<ContentItem>> = emptyMap()
) : Parcelable {

    constructor() : this("", "", "", emptyList(), emptyMap())

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        readContentsFromParcel(parcel)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeStringList(topics)
        writeContentsToParcel(parcel, contents)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Course> {
        override fun createFromParcel(parcel: Parcel): Course {
            return Course(parcel)
        }

        override fun newArray(size: Int): Array<Course?> {
            return arrayOfNulls(size)
        }

        private fun readContentsFromParcel(parcel: Parcel): Map<String, List<ContentItem>> {
            val size = parcel.readInt()
            val contents = mutableMapOf<String, List<ContentItem>>()
            for (i in 0 until size) {
                val key = parcel.readString() ?: ""
                val listSize = parcel.readInt()
                val contentItems = mutableListOf<ContentItem>()
                for (j in 0 until listSize) {
                    contentItems.add(parcel.readParcelable<ContentItem>(ContentItem::class.java.classLoader)!!)
                }
                contents[key] = contentItems
            }
            return contents
        }

        private fun writeContentsToParcel(parcel: Parcel, contents: Map<String, List<ContentItem>>) {
            parcel.writeInt(contents.size)
            for ((key, value) in contents) {
                parcel.writeString(key)
                parcel.writeInt(value.size)
                for (item in value) {
                    parcel.writeParcelable(item, 0)
                }
            }
        }
    }
}