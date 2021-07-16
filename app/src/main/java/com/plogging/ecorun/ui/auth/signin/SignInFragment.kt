package com.plogging.ecorun.ui.auth.signin

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.network.RxAuthOperations
import com.kakao.sdk.common.model.ApiError
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.databinding.FragmentSignInBinding
import com.plogging.ecorun.util.constant.Constant
import com.plogging.ecorun.util.constant.Constant.CUSTOM
import com.plogging.ecorun.util.constant.Constant.GOOGLE
import com.plogging.ecorun.util.extension.*
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.net.URL
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding, SignInViewModel>() {
    override fun getViewBinding() = FragmentSignInBinding.inflate(layoutInflater)
    override val viewModel: SignInViewModel by viewModels()
    lateinit var oAuthLoginInstance: OAuthLogin     // Naver sign in
    private val googleResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!, requireActivity())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewModel.id.value = it?.email + ":" + GOOGLE
                    viewModel.userType.value = GOOGLE
                    viewModel.isSavedSocialUser()
                }, { showLoadingPage(false) })
                .addTo(disposables)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        observeEditText()
        responseApi()
        signInButtonEnable()
    }

    private fun initView() {
        binding.signInInclude.tapTitle.setText(R.string.sign_in)
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>("isSentPw")
            ?.observe(viewLifecycleOwner) {
                if (it) {
                    binding.signInInclude.tapTitle.text = getString(R.string.temp_sign_in_title)
                    binding.tvAuthSignInSubTitle.visibility = VISIBLE
                    binding.tvAuthSignInFindPw.visibility = INVISIBLE
                    binding.tvSignInQuestion.visibility = INVISIBLE
                    binding.btnSignInKakao.visibility = INVISIBLE
                    binding.btnSignInNaver.visibility = INVISIBLE
                    binding.cvGoogle.visibility = INVISIBLE
                }
            }
    }

    private fun responseApi() {
        viewModel.isSavedUser.observe(viewLifecycleOwner) {
            if (it) saveUserImage()
            else {
                showLoadingPage(false)
                val bundle =
                    bundleOf(
                        "email" to viewModel.id.value,
                        "socialType" to viewModel.userType.value
                    )
                findNavController().navigate(R.id.action_auth_home_to_auth_nick_name, bundle)
            }
        }
        viewModel.customSignInSuccess.observe(viewLifecycleOwner) {
            if (it) saveUserImage()
            else {
                binding.tvSignInErr.text = getString(R.string.wrong_sign_in)
                binding.tvSignInErr.visibility = VISIBLE
            }
        }
        viewModel.responseCode.observe(viewLifecycleOwner) {
            if (it != 200) showLoadingPage(false)
        }
    }

    private fun saveUserImage() {
        CoroutineScope(Dispatchers.Main).launch {
            var bitmap: Bitmap? = null
            val url = if(viewModel.uri.value!!.startsWith("http:"))
                URL("https://eco-run.duckdns.org/profile/base/profile-1.PNG")
            else URL(viewModel.uri.value)
            withContext(Dispatchers.IO) {
                bitmap = BitmapFactory.decodeStream(url.openStream())
            }
            bitmap?.saveImageIn(requireContext().contentResolver)
                ?.composeSchedulers()
                ?.doOnSubscribe { showLoadingPage(false) }
                ?.subscribe({
                    saveUserData(it)
                    findNavController().navigate(R.id.action_sign_in_to_main)
                },{
                    Log.e("error", "${it.stackTraceToString()}")
                })
                ?.addTo(disposables)
        }
    }

    private fun saveUserData(uri: Uri?) {
        SharedPreference.setUserImage(requireContext(), uri)
        SharedPreference.setUserName(requireContext(), viewModel.name.value!!)
        if (viewModel.pw.value != null)
            SharedPreference.setUserEmail(requireContext(), viewModel.id.value!! + ":${CUSTOM}")
        SharedPreference.setUserPw(requireContext(), viewModel.pw.value!!)
    }

    private fun showLoadingPage(show: Boolean) {
        binding.clSignInProgress.isVisible = show
        binding.clSignInProgress.setOnClickListener { !show }
    }

    private fun observeEditText() {
        binding.etSignInId.addTextChangedListener { input ->
            when (input.toString().isValidEmail()) {
                null -> {
                    viewModel.isValidIdSubject.onNext(false)
                    binding.ivAuthSignInId.visibility = INVISIBLE
                    binding.tvSignInIdErr.visibility = INVISIBLE
                    binding.etSignInId.setGrayBorder()
                }
                true -> {
                    viewModel.isValidIdSubject.onNext(true)
                    binding.ivAuthSignInId.visibility = VISIBLE
                    binding.tvSignInIdErr.visibility = INVISIBLE
                    binding.etSignInId.setGreenBorder()
                }
                false -> {
                    viewModel.isValidIdSubject.onNext(false)
                    binding.ivAuthSignInId.visibility = VISIBLE
                    binding.tvSignInIdErr.visibility = VISIBLE
                    binding.etSignInId.setRedBorder()
                }
            }
        }
        binding.etSignInPw.addTextChangedListener { input ->
            when (input.toString().isValidPassword()) {
                null -> {
                    viewModel.isValidPwSubject.onNext(false)
                    binding.ivAuthSignInPw.visibility = INVISIBLE
                    binding.tvSignInErr.visibility = INVISIBLE
                    binding.etSignInPw.setGrayBorder()
                }
                true -> {
                    viewModel.isValidPwSubject.onNext(true)
                    binding.ivAuthSignInPw.visibility = VISIBLE
                    binding.tvSignInErr.visibility = INVISIBLE
                    binding.etSignInPw.setGreenBorder()
                }
                false -> {
                    binding.tvSignInErr.text = getString(R.string.wrong_pw_type)
                    viewModel.isValidPwSubject.onNext(false)
                    binding.ivAuthSignInPw.visibility = VISIBLE
                    binding.tvSignInErr.visibility = VISIBLE
                    binding.etSignInPw.setRedBorder()
                }
            }
        }
    }

    private fun signInButtonEnable() {
        viewModel.isSignInButtonEnable()
        viewModel.isSignInButtonEnableSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ binding.btnAuthSignIn.isEnabled = it }, {})
            .addTo(disposables)
    }

    private fun googleSignIn() {
        googleResultLauncher.launch(googleIntent(requireContext(), requireActivity()))
    }

    private fun naverSignIn() {
        oAuthLoginInstance = OAuthLogin.getInstance()
        oAuthLoginInstance.init(
            requireContext(),
            getString(R.string.OAUTH_CLIENT_ID),
            getString(R.string.OAUTH_CLIENT_SECRET),
            getString(R.string.OAUTH_CLIENT_NAME)
        )
        @SuppressLint("HandlerLeak")
        val oAuthLoginHandler: OAuthLoginHandler = object : OAuthLoginHandler() {
            override fun run(success: Boolean) {
                if (success) {
                    val token = oAuthLoginInstance.getAccessToken(requireContext())
                    viewModel.naverSignIn(token)
                } else {
                    requireContext().toast(R.string.fail_naver_sign_in)
                    showLoadingPage(false)
                }
            }
        }
        oAuthLoginInstance.startOauthLoginActivity(requireActivity(), oAuthLoginHandler)
    }

    private fun kakaoSignIn() {
        Single.just(UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext()))
            .flatMap { available ->
                if (available) UserApiClient.rx.loginWithKakaoTalk(requireContext())
                else UserApiClient.rx.loginWithKakaoAccount(requireContext())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ token -> isAgreeKakaoEmail() }, { showLoadingPage(false) })
            .addTo(disposables)
    }

    private fun isAgreeKakaoEmail() {
        UserApiClient.rx.me()
            .flatMap { user ->
                val scopes = mutableListOf<String>()
                if (user.kakaoAccount?.emailNeedsAgreement == true) scopes.add("account_email")
                if (scopes.count() > 0) Single.error(ApiError.fromScopes(scopes))
                else Single.just(user)
            }
            .retryWhen(
                // InsufficientScope 에러에 대해 추가 동의 후 재요청
                RxAuthOperations.instance.incrementalAuthorizationRequired(requireContext())
            )
            .composeSchedulers()
            .subscribe({ user ->
                viewModel.id.value = user.kakaoAccount?.email + ":${Constant.KAKAO}"
                viewModel.isSavedSocialUser()
            }, { error ->
                Log.e(ContentValues.TAG, getString(R.string.fail_require_user_info), error)
                requireContext().toast(getString(R.string.request_kakao_account))
                UserApiClient.rx.unlink()
                    .composeSchedulers()
                    .subscribe()
                showLoadingPage(false)
            })
            .addTo(disposables)
    }

    override fun clickListener() {
        binding.btnSignInGoogle.setOnClickListener { showLoadingPage(true); googleSignIn() }
        binding.btnSignInKakao.setOnClickListener { showLoadingPage(true); kakaoSignIn() }
        binding.btnSignInNaver.setOnClickListener { showLoadingPage(true); naverSignIn() }
        binding.signInInclude.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.tvAuthSignInFindPw.setOnClickListener {
            hideKeyboard(binding.etSignInId)
            findNavController().navigate(R.id.action_sign_in_to_find_pw)
        }
        binding.ivAuthSignInId.setOnClickListener {
            binding.etSignInId.requestFocus()
            binding.etSignInId.text.clear()
        }
        binding.ivAuthSignInPw.setOnClickListener {
            binding.etSignInPw.requestFocus()
            binding.etSignInPw.text.clear()
        }
        binding.btnAuthSignIn.setOnClickListener {
            viewModel.id.value = binding.etSignInId.text.toString()
            viewModel.pw.value = binding.etSignInPw.text.toString()
            showLoadingPage(true)
            viewModel.signIn()
            hideKeyboard(it)
        }
    }
}