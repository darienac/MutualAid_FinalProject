package com.example.mutualaid_finalproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.mutualaid_finalproject.model.ProfileTimeAvailability
import com.example.mutualaid_finalproject.ui.ProfileScreen
import org.junit.Rule
import org.junit.Test

class UITests {
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

        composeTestRule.onNodeWithTag("name_enable_input").assertExists().performClick()

        // Test that the name input field is displayed
        composeTestRule.onNodeWithTag("name_input").assertExists()
    }
}