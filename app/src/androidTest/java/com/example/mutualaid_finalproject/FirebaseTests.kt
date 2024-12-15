package com.example.mutualaid_finalproject

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mutualaid_finalproject.model.Post
import com.example.mutualaid_finalproject.model.PostRepository
import com.example.mutualaid_finalproject.model.Profile
import com.example.mutualaid_finalproject.model.ProfileRepository
import com.example.mutualaid_finalproject.model.ProfileTimeAvailability
import com.example.mutualaid_finalproject.model.UserData
import com.google.firebase.Timestamp

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class FirebaseTests {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.mutualaid_finalproject", appContext.packageName)
    }

    @Test
    fun testValidEditPost() {
        var postRepository = PostRepository("TEST_USER")
        val testPost = Post(
            pid="TEST_POST",
            accepted=true,
            date_expires= Timestamp(100000, 0),
            date_posted= Timestamp(100,0),
            description= UUID.randomUUID().toString(),
            location="",
            title= UUID.randomUUID().toString(),
            type="request",
            uid="TEST_USER"
        )

        var latch = CountDownLatch(1)
        postRepository.set(testPost) {
            latch.countDown();
        }
        assertTrue(latch.await(10, TimeUnit.SECONDS))

        var receivedPost = Post()
        latch = CountDownLatch(1)
        postRepository.get(testPost.pid) {
            if (it != null) {
                receivedPost = it
            }
            latch.countDown()
        }
        assertTrue(latch.await(10, TimeUnit.SECONDS))
        Log.d("TEST_POST", testPost.toString())
        Log.d("TEST_POST", receivedPost.toString())
        assertTrue(testPost == receivedPost) // should be deep equality this way with kotlin
    }

    @Test
    fun testValidEditProfile() {
        var profileRepository = ProfileRepository(UserData("TEST_USER", "", false))
        val testProfile = Profile(
            uid="TEST_USER",
            email="",
            phoneNumber="123-4567",
            name=UUID.randomUUID().toString(),
            description=UUID.randomUUID().toString(),
            skills=listOf(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
            resources=listOf(UUID.randomUUID().toString()),
            daysAvailable=listOf(
                ProfileTimeAvailability(false, false, false),
                ProfileTimeAvailability(false, false, false),
                ProfileTimeAvailability(false, false, false),
                ProfileTimeAvailability(false, false, false),
                ProfileTimeAvailability(false, false, false),
                ProfileTimeAvailability(false, false, false),
                ProfileTimeAvailability(false, false, false)
            )
        )

        var latch = CountDownLatch(1)
        profileRepository.set(testProfile) {
            latch.countDown();
        }
        assertTrue(latch.await(10, TimeUnit.SECONDS))

        var receivedProfile = Profile()
        latch = CountDownLatch(1)
        profileRepository.get(testProfile.uid) {
            if (it != null) {
                receivedProfile = it
            }
            latch.countDown()
        }
        assertTrue(latch.await(10, TimeUnit.SECONDS))
        Log.d("TEST_PROFILE", testProfile.toString())
        Log.d("TEST_PROFILE", receivedProfile.toString())
        assertTrue(testProfile == receivedProfile) // should be deep equality this way with kotlin
    }
}