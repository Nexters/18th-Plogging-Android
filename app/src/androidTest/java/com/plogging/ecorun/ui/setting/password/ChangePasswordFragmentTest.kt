package com.plogging.ecorun.ui.setting.password

import androidx.lifecycle.ViewModelStore
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
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@HiltAndroidTest
class ChangePasswordFragmentTest {
    private lateinit var navController: TestNavHostController

    @get: Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setViewModelStore(ViewModelStore())
        runOnUiThread {
            navController.setGraph(R.navigation.nav_setting)
            navController.setCurrentDestination(R.id.nav_change_password)
        }
        launchFragmentInHiltContainer<ChangePasswordFragment>(navHostController = navController)
    }

    @Test
    fun inputNewPasswords_checkMatching() {
        val newPassword = "password@"
        val confirmPassword = "password@"
        onView(withId(R.id.et_change_new_pw)).perform(typeText(newPassword))
        onView(withId(R.id.et_change_confirm_pw)).perform(typeText(confirmPassword))
        onView(withId(R.id.tv_change_confirm_pw_err)).check(matches(not(isDisplayed())))
        onView(withId(R.id.btn_change_pw)).check(matches(isNotEnabled()))
    }

    @Test
    fun inputNewPasswords_checkNotMatching() {
        val newPassword = "password@"
        val confirmPassword = "notMatching@"
        onView(withId(R.id.et_change_new_pw)).perform(typeText(newPassword))
        onView(withId(R.id.et_change_confirm_pw)).perform(typeText(confirmPassword))
        onView(withId(R.id.tv_change_confirm_pw_err)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_change_pw)).check(matches(isNotEnabled()))
    }

    @Test
    fun inputPasswords_enableButton() {
        val currentPassword = "beforePassword@"
        val newPassword = "password@"
        val confirmPassword = "password@"
        onView(withId(R.id.et_change_current_pw)).perform(typeText(currentPassword))
        onView(withId(R.id.et_change_new_pw)).perform(typeText(newPassword))
        onView(withId(R.id.et_change_confirm_pw)).perform(typeText(confirmPassword))
        onView(withId(R.id.btn_change_pw)).check(matches(isEnabled()))
    }

    @Test
    fun inputPasswords_DisableButton() {
        // GIVEN - Not matched password
        val currentPassword = "beforePassword@"
        val newPassword = "password@"
        val confirmPassword = "notMatched@"
        // WHEN - Input on EditTest
        onView(withId(R.id.et_change_current_pw)).perform(typeText(currentPassword))
        onView(withId(R.id.et_change_new_pw)).perform(typeText(newPassword))
        onView(withId(R.id.et_change_confirm_pw)).perform(typeText(confirmPassword))
        // THEN - Disable button
        onView(withId(R.id.btn_change_pw)).check(matches(isNotEnabled()))
    }

    @Test
    fun inputBlankPassword_DisableButton() {
        // GIVEN - BlankPassword
        val currentPassword = " "
        val newPassword = " "
        val confirmPassword = " "
        // WHEN - Input on Edittext
        onView(withId(R.id.et_change_current_pw)).perform(typeText(currentPassword))
        onView(withId(R.id.et_change_new_pw)).perform(typeText(newPassword))
        onView(withId(R.id.et_change_confirm_pw)).perform(typeText(confirmPassword))
        // THEN - Show Images
        onView(withId(R.id.iv_change_current_pw_cancel)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_change_pw_new_cancel)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_change_confirm_pw_cancel)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_change_pw)).check(matches(isNotEnabled()))
    }
}