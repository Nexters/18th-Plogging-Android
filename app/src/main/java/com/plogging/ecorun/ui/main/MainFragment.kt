package com.plogging.ecorun.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.databinding.FragmentMainBinding
import com.plogging.ecorun.util.constant.Constant.GOOGLE
import com.plogging.ecorun.util.constant.Constant.KAKAO
import com.plogging.ecorun.util.constant.Constant.NAVER
import com.plogging.ecorun.util.extension.firebaseAuthWithGoogle
import com.plogging.ecorun.util.extension.googleLoginSingle
import com.plogging.ecorun.util.extension.isCustomType
import com.plogging.ecorun.util.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {
    override fun getViewBinding() = FragmentMainBinding.inflate(layoutInflater)
    override val viewModel: MainViewModel by viewModels()
    lateinit var oAuthLoginInstance: OAuthLogin

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomView()
        setBottomNav()
        signIn()
    }

    private fun signIn() {
        viewModel.name.value = SharedPreference.getUserName(requireContext())
        viewModel.id.value = SharedPreference.getUserEmail(requireContext())
        viewModel.pw.value = SharedPreference.getUserPw(requireContext())
        if (viewModel.id.value.isCustomType()) viewModel.customSignIn()
        else {
            when (viewModel.id.value!!.split(":")[1]) {
                GOOGLE -> googleSignIn()
                KAKAO -> kakaoSignIn()
                NAVER -> naverSignIn()
            }
        }
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
                } else requireContext().toast(getString(R.string.fail_naver_sign_in))
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
            .subscribe({ viewModel.socialSignIn() },
                { requireContext().toast(getString(R.string.fail_kakao_sign_in)) })
            .addTo(disposables)
    }

    private fun googleSignIn() {
        googleLoginSingle(requireContext(), this@MainFragment)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { firebaseAuthWithGoogle(it, requireActivity()) }
            .subscribe({
                viewModel.id.value = it?.email + ":" + GOOGLE
                viewModel.socialSignIn()
            }, { requireContext().toast(getString(R.string.fail_google_sign_in)) })
            .addTo(disposables)
    }

    private fun showBottomView() {
        viewModel.showBottomNav.observe(viewLifecycleOwner) { isShow ->
            when (isShow) {
                null -> binding.clMain.transitionToStart()
                true -> binding.clMain.transitionToStart()
                false -> binding.clMain.transitionToEnd()
            }
        }
    }

    private fun setBottomNav() {
        binding.bottomNav.itemIconTintList = null
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        val navController = navHostFragment.navController
        // 반드시 navigation id와 menu id가 같아야한다.
        binding.bottomNav.setupWithNavController(navController)
        var selectedId = binding.bottomNav.selectedItemId
        parentFragmentManager.primaryNavigationFragment
        binding.bottomNav.setOnItemSelectedListener {
            if (selectedId != it.itemId) {
                selectedId = it.itemId
                it.onNavDestinationSelected(navController)
            } else false
        }
    }

    override fun clickListener() {}
}