package com.plogging.ecorun.ui.auth.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
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
import com.plogging.ecorun.databinding.FragmentAuthBinding
import com.plogging.ecorun.util.constant.Constant.GOOGLE
import com.plogging.ecorun.util.constant.Constant.KAKAO
import com.plogging.ecorun.util.extension.*
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

@AndroidEntryPoint
class AuthHomeFragment : BaseFragment<FragmentAuthBinding, AuthHomeViewModel>() {

    override fun getViewBinding() = FragmentAuthBinding.inflate(layoutInflater)
    override val viewModel: AuthHomeViewModel by viewModels()

    // Naver sign in
    lateinit var oAuthLoginInstance: OAuthLogin

    private val googleResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 0) {
                showLoadingPage(false)
                return@registerForActivityResult
            }
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!, requireActivity())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewModel.userId.value = it?.email + ":$GOOGLE"
                    viewModel.userType.value = GOOGLE
                    viewModel.isSavedSocialUser()
                }, { showLoadingPage(false) })
                .addTo(disposables)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        autoLogin()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        responseApi()
    }

    private fun responseApi() {
        viewModel.isSavedUser.observe(viewLifecycleOwner) {
            if (it) saveUserImage()
            else {
                showLoadingPage(false)
                val bundle =
                    bundleOf(
                        "email" to viewModel.userId.value,
                        "socialType" to viewModel.userType.value
                    )
                findNavController().navigate(R.id.action_auth_home_to_auth_nick_name, bundle)
            }
        }
        viewModel.responseCode.observe(viewLifecycleOwner) {
            if (it != 200) showLoadingPage(false)
        }
    }

    private fun saveUserImage() {
        CoroutineScope(Dispatchers.Main).launch {
            var bitmap: Bitmap? = null
            val url = if (viewModel.uri.value!!.startsWith("http:"))
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
                    findNavController().navigate(R.id.action_auth_to_main)
                }, {
                    Log.e("error", "${it.stackTraceToString()}")
                })
                ?.addTo(disposables)
        }
    }

    private fun saveUserData(uri: Uri?) {
        SharedPreference.setUserImage(requireContext(), uri)
        SharedPreference.setUserEmail(requireContext(), viewModel.userId.value!!)
        SharedPreference.setUserName(requireContext(), viewModel.userName.value!!)
    }

    private fun autoLogin() {
        if (SharedPreference.getUserEmail(requireContext()).isNotEmpty()) {
            findNavController().navigate(R.id.action_auth_to_main)
        }
    }

    private fun kakaoSignIn() {
        Single.just(UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext()))
            .flatMap { available ->
                if (available) UserApiClient.rx.loginWithKakaoTalk(requireContext())
                else UserApiClient.rx.loginWithKakaoAccount(requireContext())
            }
            .composeSchedulers()
            .subscribe({ isAgreeKakaoEmail() }, { showLoadingPage(false) })
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
                viewModel.userId.value = user.kakaoAccount?.email + ":$KAKAO"
                viewModel.userType.value = KAKAO
                viewModel.isSavedSocialUser()
            }, {
                requireContext().toast(getString(R.string.request_kakao_account))
                UserApiClient.rx.unlink()
                    .composeSchedulers()
                    .subscribe()
                showLoadingPage(false)
            })
            .addTo(disposables)
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
                    viewModel.naverSignIn(oAuthLoginInstance.getAccessToken(requireContext()))
                } else {
                    requireContext().toast(getString(R.string.fail_naver_sign_in))
                    showLoadingPage(false)
                }
            }
        }
        oAuthLoginInstance.startOauthLoginActivity(requireActivity(), oAuthLoginHandler)
    }

    private fun googleSignIn() {
        googleResultLauncher.launch(googleIntent(requireContext(), requireActivity()))
    }

    private fun showLoadingPage(show: Boolean) {
        binding.clAuthHomeProgress.isVisible = show
        binding.clAuthHomeProgress.setOnClickListener { !show }
    }

    override fun clickListener() {
        binding.btnAuthHomeRegister.setOnClickListener { findNavController().navigate(R.id.action_auth_to_register) }
        binding.btnAuthHomeSignIn.setOnClickListener { findNavController().navigate(R.id.action_auth_to_sign_in) }
        binding.tvPolicy.setOnClickListener { findNavController().navigate(R.id.action_auth_to_policy_dialog) }
        binding.btnAuthHomeGoogle.setOnClickListener { showLoadingPage(true); googleSignIn() }
        binding.btnAuthHomeKakao.setOnClickListener { showLoadingPage(true); kakaoSignIn() }
        binding.btnAuthHomeNaver.setOnClickListener { showLoadingPage(true); naverSignIn() }
    }
}