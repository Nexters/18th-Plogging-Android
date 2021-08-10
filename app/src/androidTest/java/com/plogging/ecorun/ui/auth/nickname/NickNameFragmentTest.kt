package com.plogging.ecorun.ui.auth.nickname

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
class NickNameFragmentTest {
    private lateinit var navController: TestNavHostController

    @get: Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setViewModelStore(ViewModelStore())
        runOnUiThread {
            navController.setGraph(R.navigation.nav_auth)
            navController.setCurrentDestination(R.id.nav_auth_nick_name)
        }
        launchFragmentInHiltContainer<NickNameFragment>(navHostController = navController)
    }

    @Test
    fun nickName_input_activeButton() {
        // GIVEN - NickName
        val nickName = "ecorun"
        // WHEN - Input
        onView(withId(R.id.et_auth_nick_name)).perform(typeText(nickName))
        // THEN - Show enable button
        onView(withId(R.id.btn_nick_name)).check(matches(isEnabled()))
    }

    @Test
    fun emptyNickName_input_inactiveButton() {
        // GIVEN - Empty NickName
        val nickName = ""
        // WHEN - Input
        onView(withId(R.id.et_auth_nick_name)).perform(typeText(nickName))
        // THEN - Show disable button
        onView(withId(R.id.btn_nick_name)).check(matches(not(isEnabled())))
    }

    @Test
    fun blankNickName_input_inactiveButton() {
        // GIVEN - blank NickName
        val nickName = " "
        // WHEN - Input
        onView(withId(R.id.et_auth_nick_name)).perform(typeText(nickName))
        // THEN - Show disable button
        onView(withId(R.id.btn_nick_name)).check(matches(not(isEnabled())))
    }
}