package com.plogging.ecorun.ui.setting.nickname

import androidx.lifecycle.ViewModelStore
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
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
class ChangeNickNameFragmentTest {
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
            navController.setCurrentDestination(R.id.nav_change_nickname)
        }
        launchFragmentInHiltContainer<ChangeNickNameFragment>(navHostController = navController)
    }

    @Test
    fun inputRightNewNickName_verifiedNickName() {
        // GIVEN - New nick name
        val newNickName = "ecorun"
        // WHEN - Input on edittext view
        onView(withId(R.id.et_setting_nick_name)).perform(typeText(newNickName))
        // THEN - Enable Button
        onView(withId(R.id.btn_setting_nick_name)).check(matches(isEnabled()))
    }

    @Test
    fun inputEmptyNewNickName_verifiedNickName() {
        // GIVEN - New nick name
        val newNickName = ""
        // WHEN - Input on edittext view
        onView(withId(R.id.et_setting_nick_name)).perform(typeText(newNickName))
        // THEN - Disable Button
        onView(withId(R.id.btn_setting_nick_name)).check(matches(not(isEnabled())))
    }

    @Test
    fun inputBlankNewNickName_verifiedNickName() {
        // GIVEN - New nick name
        val newNickName = " "
        // WHEN - Input on edittext view
        onView(withId(R.id.et_setting_nick_name)).perform(typeText(newNickName))
        // THEN - Disable Button
        onView(withId(R.id.btn_setting_nick_name)).check(matches(not(isEnabled())))
    }
}