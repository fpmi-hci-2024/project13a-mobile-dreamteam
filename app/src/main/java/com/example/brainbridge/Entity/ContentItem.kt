package com.example.brainbridge.Entity

import android.os.Parcel
import android.os.Parcelable

data class ContentItem(
    val type: String = "",
    val name: String = "",
    val id: String = ""
) : Parcelable {

    constructor() : this("", "", "")

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(name)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContentItem> {
        override fun createFromParcel(parcel: Parcel): ContentItem {
            return ContentItem(parcel)
        }

        override fun newArray(size: Int): Array<ContentItem?> {
            return arrayOfNulls(size)
        }
    }
}