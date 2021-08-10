package com.plogging.ecorun.ui.main.onboarding

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
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
class OnBoardingFragmentTest {
    private lateinit var navController: TestNavHostController

    @get: Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltAndroidRule.inject()
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        UiThreadStatement.runOnUiThread {
            navController.setGraph(R.navigation.nav_on_boarding)
            navController.setCurrentDestination(R.id.nav_on_boarding)
        }
        launchFragmentInHiltContainer<OnBoardingFragment>(navHostController = navController)
    }

    @Test
    fun clickButton_moveNextPage() {
        // GIVEN - Show on boarding fragment
        // WHEN - Click button
        // THEN - Move page

        // first page
        onView(withText(R.string.on_board_title_0)).check(matches(isDisplayed()))
        onView(withText(R.string.next_page)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_main_on_board)).perform(click())
        // second page
        onView(withText(R.string.on_board_title_1)).check(matches(isDisplayed()))
        onView(withText(R.string.next_page)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_main_on_board)).perform(click())
        // third page
        onView(withText(R.string.on_board_title_2)).check(matches(isDisplayed()))
        onView(withText(R.string.confirm)).check(matches(isDisplayed()))
        onView(withId(R.id.pager_main_on_board)).perform(swipeRight())
        // second page
        onView(withText(R.string.on_board_title_1)).check(matches(isDisplayed()))
        onView(withText(R.string.next_page)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_main_on_board)).perform(click())
        // third page
        onView(withText(R.string.on_board_title_2)).check(matches(isDisplayed()))
        onView(withText(R.string.confirm)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_main_on_board)).perform(click())
        Assert.assertEquals(R.id.main, navController.currentDestination?.id)
    }
}