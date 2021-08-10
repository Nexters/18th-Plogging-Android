package com.plogging.ecorun.ui.auth.password

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.plogging.ecorun.R
import com.plogging.ecorun.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@HiltAndroidTest
class FindPasswordFragmentTest {
    private lateinit var navController: TestNavHostController
    private lateinit var findPasswordViewModel: FindPasswordViewModel

    @get: Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        runOnUiThread {
            navController.setGraph(R.navigation.nav_auth)
            navController.setCurrentDestination(R.id.nav_auth_find_pw)
        }
        launchFragmentInHiltContainer<FindPasswordFragment>(navHostController = navController)
    }

    @Test
    fun inputWrongEmail_showErrorText() {
        // GIVEN - Wrong email
        val wrongEmail = "1234"
        // WHEN - Input
        onView(withId(R.id.et_auth_find_pw_email)).perform(typeText(wrongEmail))
        // THEN - Show error text and disable button
        onView(withId(R.id.tv_auth_find_pw_err)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_auth_find_pw)).check(matches(not(isEnabled())))
    }

    @Test
    fun inputRightEmail_enableButton() {
        // GIVEN - right email
        val rightEmail = "ploggingteam@gmail.com"
        // WHEN - Input
        onView(withId(R.id.et_auth_find_pw_email)).perform(typeText(rightEmail))
        // THEN - Show error text
        onView(withId(R.id.btn_auth_find_pw)).check(matches(isEnabled()))
    }
}