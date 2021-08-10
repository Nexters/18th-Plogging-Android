package com.plogging.ecorun.ui.auth.home

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.plogging.ecorun.R
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.data.repository.auth.AuthRepository
import com.plogging.ecorun.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
class AuthHomeFragmentTest {
    private lateinit var navController: TestNavHostController

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: AuthRepository

    @Before
    fun init() {
        hiltRule.inject()
        // 권한을 수용했다고 가정한다.
        SharedPreference.setPermitLocation(ApplicationProvider.getApplicationContext(), true)
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        runOnUiThread {
            navController.setGraph(R.navigation.nav_auth)
        }
        // WHEN - Start AuthHomeFragment
        launchFragmentInHiltContainer<AuthHomeFragment>(navHostController = navController)
    }

    @Test
    fun startHomeFragment_showLoginButton() {
        // GIVEN - Show AuthHomeFragment
        // WHEN - Start AuthHomeFragment
        // THEN - Show SignIn Button
        onView(withText(R.string.kakao_sign_in)).check(matches(isDisplayed()))
        onView(withText(R.string.naver_sign_in)).check(matches(isDisplayed()))
        onView(withText(R.string.btn_google)).check(matches(isDisplayed()))
        onView(withText(R.string.custom_sign_in)).check(matches(isDisplayed()))
        onView(withText(R.string.sign_in)).check(matches(isDisplayed()))
    }

    @Test
    fun registerButtonClick_showRegisterFragment() {
        // GIVEN - Show AuthHomeFragment
        // WHEN - Click register Button
        onView(withId(R.id.btn_auth_home_register)).perform(click())
        // THEN - Show register Fragment
        Assert.assertEquals(navController.currentDestination?.id, R.id.nav_auth_register)
    }

    @Test
    fun signInButtonClick_showSignInFragment() {
        // GIVEN - Show AuthHomeFragment
        // WHEN - Click signIn button
        onView(withId(R.id.btn_auth_home_sign_in)).perform(click())
        // THEN - Show signIn Fragment
        Assert.assertEquals(navController.currentDestination?.id, R.id.nav_auth_sign_in)
    }

    @Test
    fun privatePolicyTextClick_showPrivatePolicyDialog() {
        // GIVEN - Show AuthHomeFragment
        // WHEN - Click private policy text
        onView(withId(R.id.tv_policy)).perform(click())
        // THEN - Show private policy dialog
        Assert.assertEquals(navController.currentDestination?.id, R.id.nav_dialog_policy)
    }
}