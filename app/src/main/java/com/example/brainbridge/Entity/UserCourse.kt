package com.example.brainbridge.Entity

import android.os.Parcel
import android.os.Parcelable

data class UserCourse(
    val courseId: String,
    val name: String,
    val progress: Int,
    val isFinished: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(courseId)
        parcel.writeString(name)
        parcel.writeInt(progress)
        parcel.writeByte(if (isFinished) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserCourse> {
        override fun createFromParcel(parcel: Parcel): UserCourse {
            return UserCourse(parcel)
        }

        override fun newArray(size: Int): Array<UserCourse?> {
            return arrayOfNulls(size)
        }
    }
}