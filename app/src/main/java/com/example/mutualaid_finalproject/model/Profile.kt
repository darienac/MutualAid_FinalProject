package com.example.mutualaid_finalproject.model

data class ProfileTimeAvailability(
    val morning: Boolean=false,
    val afternoon: Boolean=false,
    val evening: Boolean=false
)

data class Profile(
    val uid: String="",
    val phoneNumber: String="",
    val name: String="",
    val skills: List<String> = emptyList(),
    val resources: List<String> = emptyList(),
    val daysAvailable: List<ProfileTimeAvailability> = emptyList()
)