package com.plogging.ecorun.ui.auth.register

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plogging.ecorun.util.extension.isMatched
import com.plogging.ecorun.util.extension.isValidEmail
import com.plogging.ecorun.util.extension.isValidPassword
import io.reactivex.observers.TestObserver
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterViewModelTest : TestCase() {
    private lateinit var registerViewModel: RegisterViewModel

    @Before
    fun setupViewModel() {
        registerViewModel = RegisterViewModel()
    }

    @Test
    fun emailAndPassword_input_checkValid() {
        val email = "ploggingteam@gmail.com"
        val firstPw = "1q2w3e4r"
        val secondPw = "1q2w3e4r"
        val isValidId = email.isValidEmail()
        val isValidPw = firstPw.isValidPassword()
        val isMatchedPw = isMatched(firstPw, secondPw)
        val isValidSubject: TestObserver<Boolean> = TestObserver.create()
        registerViewModel.buttonEnableSubject.subscribe(isValidSubject)
        registerViewModel.resisterButtonEnable()
        registerViewModel.isMatchedPwSubject.onNext(isMatchedPw!!)
        registerViewModel.isValidIdSubject.onNext(isValidId!!)
        registerViewModel.isValidPwSubject.onNext(isValidPw!!)
        assertEquals(true, isValidSubject.values().last())
    }

    @Test
    fun emptyIdAndPassword_input_checkValid() {
        val email = ""
        val firstPw = ""
        val secondPw = ""
        val isValidId = email.isValidEmail()
        val isValidPw = firstPw.isValidPassword()
        val isMatchedPw = isMatched(firstPw, secondPw)
        val isValidSubject: TestObserver<Boolean> = TestObserver.create()
        registerViewModel.buttonEnableSubject.subscribe(isValidSubject)
        registerViewModel.resisterButtonEnable()
        registerViewModel.isMatchedPwSubject.onNext(isMatchedPw == true)
        registerViewModel.isValidIdSubject.onNext(isValidId == true)
        registerViewModel.isValidPwSubject.onNext(isValidPw == true)
        assertEquals(false, isValidSubject.values().last())
    }

    @Test
    fun blankIdAndPassword_input_checkValid() {
        val email = " "
        val firstPw = " "
        val secondPw = " "
        val isValidId = email.isValidEmail()
        val isValidPw = firstPw.isValidPassword()
        val isMatchedPw = isMatched(firstPw, secondPw)
        val isValidSubject: TestObserver<Boolean> = TestObserver.create()
        registerViewModel.buttonEnableSubject.subscribe(isValidSubject)
        registerViewModel.resisterButtonEnable()
        registerViewModel.isMatchedPwSubject.onNext(isMatchedPw == true)
        registerViewModel.isValidIdSubject.onNext(isValidId == true)
        registerViewModel.isValidPwSubject.onNext(isValidPw == true)
        assertEquals(false, isValidSubject.values().last())
    }

    @Test
    fun invalidId_input_checkValid() {
        val email = "qwer"
        val firstPw = "1q2w3e4r"
        val secondPw = "1q2w3e4r"
        val isValidId = email.isValidEmail()
        val isValidPw = firstPw.isValidPassword()
        val isMatchedPw = isMatched(firstPw, secondPw)
        val isValidSubject: TestObserver<Boolean> = TestObserver.create()
        registerViewModel.buttonEnableSubject.subscribe(isValidSubject)
        registerViewModel.resisterButtonEnable()
        registerViewModel.isMatchedPwSubject.onNext(isMatchedPw == true)
        registerViewModel.isValidIdSubject.onNext(isValidId == true)
        registerViewModel.isValidPwSubject.onNext(isValidPw == true)
        assertEquals(false, isValidSubject.values().last())
    }

    @Test
    fun invalidPw_input_checkValid() {
        val email = "ploggingteam@gmail.com"
        val firstPw = "12345678"
        val secondPw = "12345678"
        val isValidId = email.isValidEmail()
        val isValidPw = firstPw.isValidPassword()
        val isMatchedPw = isMatched(firstPw, secondPw)
        val isValidSubject: TestObserver<Boolean> = TestObserver.create()
        registerViewModel.buttonEnableSubject.subscribe(isValidSubject)
        registerViewModel.resisterButtonEnable()
        registerViewModel.isMatchedPwSubject.onNext(isMatchedPw == true)
        registerViewModel.isValidIdSubject.onNext(isValidId == true)
        registerViewModel.isValidPwSubject.onNext(isValidPw == true)
        assertEquals(false, isValidSubject.values().last())
    }

    @Test
    fun notMatchedPw_input_checkValid() {
        val email = "ploggingteam@gmail.com"
        val firstPw = "1111qqqq"
        val secondPw = "1q2w3e4r"
        val isValidId = email.isValidEmail()
        val isValidPw = firstPw.isValidPassword()
        val isMatchedPw = isMatched(firstPw, secondPw)
        val isValidSubject: TestObserver<Boolean> = TestObserver.create()
        registerViewModel.buttonEnableSubject.subscribe(isValidSubject)
        registerViewModel.resisterButtonEnable()
        registerViewModel.isMatchedPwSubject.onNext(isMatchedPw == true)
        registerViewModel.isValidIdSubject.onNext(isValidId == true)
        registerViewModel.isValidPwSubject.onNext(isValidPw == true)
        assertEquals(false, isValidSubject.values().last())
    }
}