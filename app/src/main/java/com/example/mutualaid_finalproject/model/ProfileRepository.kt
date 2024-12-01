package com.example.mutualaid_finalproject.model

import androidx.lifecycle.MutableLiveData
import com.example.mutualaid_finalproject.model.firestore.RemoteProfilesDao
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProfileRepository(private val uid: String = "NO_USER", onCreate: () -> Unit = {}) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var remoteProfilesDao = RemoteProfilesDao(uid)

    var currentProfile = remoteProfilesDao.getCurrentUserFlow().map {
            value: DocumentSnapshot? -> remoteProfilesDao.toProfile(value)
    }

    init {
        get(uid) { profile->
            val times = ProfileTimeAvailability()
            if (profile == null) {
                set(Profile(
                    uid=uid,
                    name="",
                    skills=emptyList<String>(),
                    resources=emptyList<String>(),
                    daysAvailable=listOf(times, times, times, times, times, times, times)
                )) {
                    onCreate()
                }
            } else {
                onCreate()
            }
        }
    }

    fun setListeningProfile(uid: String = "NO_USER") {
        remoteProfilesDao.close()
        remoteProfilesDao = RemoteProfilesDao(uid)
        currentProfile = remoteProfilesDao.getCurrentUserFlow().map {
            value: DocumentSnapshot? -> remoteProfilesDao.toProfile(value)
        }
    }

    fun get(uid: String, onResult:(Profile?)->Unit) {
        remoteProfilesDao.get(uid, onResult={
            onResult(remoteProfilesDao.toProfile(it))
        })
    }

    fun set(profile: Profile, onResult:()->Unit) {
        remoteProfilesDao.set(profile, onResult=onResult)
    }
}

// TODO: add live view of this user's profile with LiveData, use RemoteProfilesDao to create snapshot listener