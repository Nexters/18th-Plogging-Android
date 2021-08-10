package com.plogging.ecorun.ui.auth.register

import androidx.lifecycle.ViewModelStore
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.plogging.ecorun.R
import com.plogging.ecorun.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@HiltAndroidTest
class RegisterFragmentTest {
    private lateinit var navController: TestNavHostController

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setViewModelStore(ViewModelStore())
        runOnUiThread {
            navController.setGraph(R.navigation.nav_auth)
            navController.setCurrentDestination(R.id.nav_auth_register)
        }
        launchFragmentInHiltContainer<RegisterFragment>(navHostController = navController)
    }

    @Test
    fun inputWrongEmail_showErrorText() {
        // GIVEN - Wrong email
        val wrongEmail = "wrongEmail"
        // WHEN - Input
        onView(withId(R.id.et_register_email)).perform(typeText(wrongEmail))
        // THEN - Show error text
        onView(withId(R.id.tv_register_email_err)).check(matches(isDisplayed()))
    }

    @Test
    fun inputWrongPassword_showErrorText() {
        // GIVEN - Wrong password
        val wrongPassword = "wrongPassword"
        // WHEN - Input
        onView(withId(R.id.et_register_pw)).perform(typeText(wrongPassword))
        // THEN - Show error text
        onView(withId(R.id.tv_register_pw_err)).check(matches(isDisplayed()))
    }

    @Test
    fun inputNotMatchedPassword_showErrorText() {
        // GIVEN - Different password
        val firstPassword = "firstPassword1"
        val secondPassword = "secondPassword2"
        // WHEN - Input
        onView(withId(R.id.et_register_pw)).perform(typeText(firstPassword))
        onView(withId(R.id.et_register_pw_confirm)).perform(typeText(secondPassword))
        // THEN - Show error text
        onView(withId(R.id.tv_register_pw_confirm_err)).check(matches(isDisplayed()))
    }

    @Test
    fun inputRightEmailAndPassword_returnAbleButton() {
        // GIVEN - right email, right password
        val email = "ploggingteam@gmail.com"
        val firstPassword = "password1"
        val secondPassword = "password1"
        // WHEN - Input
        onView(withId(R.id.et_register_email)).perform(typeText(email))
        onView(withId(R.id.et_register_pw)).perform(typeText(firstPassword))
        onView(withId(R.id.et_register_pw_confirm)).perform(typeText(secondPassword))
        // THEN - Show able button
        onView(withId(R.id.btn_register_next)).check(matches(isEnabled()))
    }

    @Test
    fun clickAbleNextButton_goNickNameFragment() {
        // GIVEN - right email, right password
        val email = "ploggingteam@gmail.com"
        val firstPassword = "password1"
        val secondPassword = "password1"
        // WHEN - Input
        onView(withId(R.id.et_register_email)).perform(typeText(email))
        onView(withId(R.id.et_register_pw)).perform(typeText(firstPassword))
        onView(withId(R.id.et_register_pw_confirm)).perform(typeText(secondPassword))
        onView(isRoot()).perform(ViewActions.pressBack())
        onView(withId(R.id.btn_register_next)).perform(click())
        // THEN - Show NicknameFragment
        Assert.assertEquals(navController.currentDestination?.id, R.id.nav_auth_nick_name)
    }
}