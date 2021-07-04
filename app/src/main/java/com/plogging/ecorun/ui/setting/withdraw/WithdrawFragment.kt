package com.plogging.ecorun.ui.setting.withdraw

import android.content.ContentValues
import android.content.Intent
import android.net.Uri.EMPTY
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import com.nhn.android.naverlogin.OAuthLogin
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.databinding.FragmentWithdrawBinding
import com.plogging.ecorun.ui.auth.MainActivity
import com.plogging.ecorun.ui.main.MainViewModel
import com.plogging.ecorun.util.constant.Constant.CUSTOM
import com.plogging.ecorun.util.constant.Constant.GOOGLE
import com.plogging.ecorun.util.constant.Constant.KAKAO
import com.plogging.ecorun.util.constant.Constant.NAVER
import com.plogging.ecorun.util.extension.composeSchedulers
import com.plogging.ecorun.util.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxkotlin.addTo

@AndroidEntryPoint
class WithdrawFragment : BaseFragment<FragmentWithdrawBinding, WithdrawViewModel>() {
    override fun getViewBinding() = FragmentWithdrawBinding.inflate(layoutInflater)
    private lateinit var googleSignInClient: GoogleSignInClient
    override val viewModel: WithdrawViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomViewDown()
        initTitle()
        responseApi()
    }

    private fun bottomViewDown() {
        parentFragment?.parentFragment?.let {
            ViewModelProvider(it).get(MainViewModel::class.java).showBottomNav.value = false
        }
    }

    private fun initTitle() {
        binding.includeWithdraw.tapTitle.text = getString(R.string.withdraw)
    }

    private fun responseApi() {
        viewModel.responseCode.observe(viewLifecycleOwner) {
            showLoadingPage(false)
            when (it) {
                200 -> {
                    when (SharedPreference.getUserEmail(requireContext()).split(":")[1]) {
                        GOOGLE -> googleWithdraw()
                        KAKAO -> kakaoWithdraw()
                        NAVER -> naverWithdraw()
                        CUSTOM -> withdrawProcess()
                    }
                }
                else -> requireContext().toast(getString(R.string.fail_withdraw))
            }
        }
    }

    private fun initUserData() {
        SharedPreference.setUserEmail(requireContext(), "")
        SharedPreference.setUserName(requireContext(), "")
        SharedPreference.setUserPw(requireContext(), "")
        SharedPreference.setUserImage(requireContext(), EMPTY)
    }

    private fun showLoadingPage(show: Boolean) {
        binding.clWithdrawProgress.isVisible = show
        binding.clWithdrawProgress.setOnClickListener { !show }
    }

    private fun moveSignInPage() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        parentFragment?.activity?.finish()
    }

    private fun withdrawProcess() {
        requireContext().toast(getString(R.string.success_withdraw))
        initUserData()
        moveSignInPage()
    }

    private fun googleWithdraw() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        googleSignInClient.revokeAccess().addOnCompleteListener {
            auth = FirebaseAuth.getInstance()
            auth.currentUser?.delete()
            withdrawProcess()
        }
    }

    private fun kakaoWithdraw() {
        UserApiClient.rx.unlink()
            .composeSchedulers()
            .subscribe({ withdrawProcess() },
                { error -> Log.e(ContentValues.TAG, getString(R.string.error_disconnect), error) })
            .addTo(disposables)
    }

    private fun naverWithdraw() {
        OAuthLogin.getInstance().logoutAndDeleteToken(requireContext())
        withdrawProcess()
    }

    override fun clickListener() {
        binding.btnWithdraw.setOnClickListener { showLoadingPage(true); viewModel.withdraw() }
        binding.includeWithdraw.ivBack.setOnClickListener { findNavController().popBackStack() }
    }
}