package com.example.mutualaid_finalproject.model

import android.net.Uri
import com.google.firebase.Timestamp

data class Post(
    val pid: String = "",
    val accepted: Boolean = true,
    val date_expires: Timestamp = Timestamp(0, 0),
    val date_posted: Timestamp = Timestamp(0, 0),
    val description: String = "",
    val location: String = "",
    val title: String = "",
    val type: String = "",
    val uid: String = "NO_USER"
)