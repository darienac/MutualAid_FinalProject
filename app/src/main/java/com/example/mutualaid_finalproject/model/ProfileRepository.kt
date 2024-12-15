package com.example.mutualaid_finalproject.model

import com.example.mutualaid_finalproject.model.firestore.RemoteProfilesDao
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.map

class ProfileRepository(private val user: UserData = UserData("NO_USER"), onCreate: () -> Unit = {}) {
    private var remoteProfilesDao = RemoteProfilesDao(user.uid)

    var currentProfile = remoteProfilesDao.getCurrentUserFlow().map {
            value: DocumentSnapshot? -> remoteProfilesDao.toProfile(value)
    }

    init {
        get(user.uid) { profile->
            val times = ProfileTimeAvailability()
            if (profile == null) {
                set(Profile(
                    uid=user.uid,
                    email=user.email,
                    name=user.email,
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

    fun getList(uids: List<String>, onResult:(Profile?)->Unit) {
        for (uid in uids) {
            remoteProfilesDao.get(uid, onResult={
                onResult(remoteProfilesDao.toProfile(it))
            })
        }
    }

    fun set(profile: Profile, onResult:()->Unit) {
        remoteProfilesDao.set(profile, onResult=onResult)
    }

    fun delete(uid: String, onResult:()->Unit) {
        remoteProfilesDao.delete(uid, onResult=onResult)
    }
}

// TODO: add live view of this user's profile with LiveData, use RemoteProfilesDao to create snapshot listener