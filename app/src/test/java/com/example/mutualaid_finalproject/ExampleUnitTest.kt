package com.example.mutualaid_finalproject

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.mutualaid_finalproject.ui.ProfileScreen
import com.example.mutualaid_finalproject.ui.SignInScreen
import com.example.mutualaid_finalproject.model.ProfileTimeAvailability
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testProfileScreenUIComponents() {
        // Set up the ProfileScreen for testing
        composeTestRule.setContent {
            ProfileScreen(
                email = "test_user@example.com",
                phoneNumber = "1234567890",
                name = "John Doe",
                description = "Friendly neighbor",
                skills = listOf("Cooking", "Gardening"),
                resources = listOf("Lawn Mower"),
                availability = List(7) { ProfileTimeAvailability(false, false, false) },
                onPhoneNumberChange = {},
                onNameChange = {},
                onDescriptionChange = {},
                addSkill = {},
                removeSkill = {},
                addResource = {},
                removeResource = {},
                changeAvailability = { _, _ -> }
            )
        }

        // Test that the profile screen is displayed
        composeTestRule.onNodeWithTag("profile_screen").assertIsDisplayed()

        // Test that the name input field is displayed
        composeTestRule.onNodeWithTag("name_input").assertExists()

        // Test that the email input field is displayed
        composeTestRule.onNodeWithTag("email_input").assertExists()
    }

    @Test
    fun testLoginSuccess() {
        composeTestRule.setContent {
            SignInScreen(
                onLogin = { email, password ->
                    // Simulate successful login
                    assertEquals("testuser@example.com", email)
                    assertEquals("password123", password)
                },
                onSignup = { _, _ -> },
                onGoogleLogin = {}
            )
        }

        // Enter email
        composeTestRule.onNodeWithTag("email_input")
            .performTextInput("testuser@example.com")

        // Enter password
        composeTestRule.onNodeWithTag("password_input")
            .performTextInput("password123")

        // Click login button
        composeTestRule.onNodeWithTag("login_button").performClick()
    }

    @Test
    fun testLoginFailure() {
        composeTestRule.setContent {
            SignInScreen(
                onLogin = { email, password ->
                    // Simulate failed login
                    assertNotEquals("testuser@example.com", email)
                    assertNotEquals("password123", password)
                },
                onSignup = { _, _ -> },
                onGoogleLogin = {}
            )
        }

        // Enter incorrect email
        composeTestRule.onNodeWithTag("email_input")
            .performTextInput("wronguser@example.com")

        // Enter incorrect password
        composeTestRule.onNodeWithTag("password_input")
            .performTextInput("wrongpassword")

        // Click login button
        composeTestRule.onNodeWithTag("login_button").performClick()
    }
}
