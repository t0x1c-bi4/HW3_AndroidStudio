package com.example.hw3_androidstudio

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hw3_androidstudio.data.api.SwapiApi
import com.example.hw3_androidstudio.data.local.FakeApi
import com.example.hw3_androidstudio.data.model.PersonDto
import com.example.hw3_androidstudio.di.AppModule
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(AppModule::class)
@RunWith(AndroidJUnit4::class)
class UiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val api: SwapiApi = FakeApi().apply {
        shouldThrowError = false
        people = listOf(
            PersonDto(
                name = "Luke",
                height = "172",
                mass = "77",
                hair_color = "",
                skin_color = "",
                eye_color = "",
                birth_year = "",
                gender = "",
                url = "https://swapi.dev/api/people/1/"
            )
        )
    }

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun list_to_detail_navigation() {
        composeTestRule.waitUntil(5_000) {
            composeTestRule.onAllNodesWithText("Luke").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Luke")
            .assertExists()
            .performClick()

        composeTestRule
            .onNodeWithText("Назад")
            .assertExists()
    }
}