package com.plogging.ecorun.ui.setting.signout

import android.content.Intent
import android.net.Uri.EMPTY
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import com.nhn.android.naverlogin.OAuthLogin
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragmentDialog
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.databinding.FragmentDialogBinding
import com.plogging.ecorun.ui.auth.MainActivity
import com.plogging.ecorun.util.constant.Constant.CUSTOM
import com.plogging.ecorun.util.constant.Constant.GOOGLE
import com.plogging.ecorun.util.constant.Constant.KAKAO
import com.plogging.ecorun.util.constant.Constant.NAVER
import com.plogging.ecorun.util.extension.composeSchedulers
import com.plogging.ecorun.util.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxkotlin.addTo

@AndroidEntryPoint
class SignOutDialog : BaseFragmentDialog<FragmentDialogBinding, SignOutViewModel>() {
    override fun getViewBinding() = FragmentDialogBinding.inflate(layoutInflater)
    private lateinit var googleSignInClient: GoogleSignInClient
    override val viewModel: SignOutViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        responseApi()
    }

    private fun responseApi() {
        viewModel.responseCode.observe(viewLifecycleOwner) {
            when (it) {
                200 -> {
                    when (SharedPreference.getUserEmail(requireContext()).split(":")[1]) {
                        CUSTOM -> signOutProcess()
                        GOOGLE -> googleSignOut()
                        KAKAO -> kakaoSignOut()
                        NAVER -> naverSignOut()
                    }
                }
                else -> requireContext().toast(getString(R.string.fail_sign_out))
            }
        }
    }

    private fun initUserData() {
        SharedPreference.setUserEmail(requireContext(), "")
        SharedPreference.setUserName(requireContext(), "")
        SharedPreference.setUserCookie(requireContext(), "")
        SharedPreference.setUserImage(requireContext(), EMPTY)
    }

    private fun moveSignInPage() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        parentFragment?.activity?.finish()
        dismiss()
    }

    override fun initView() {
        binding.tvDialogSubTitle.text = getString(R.string.sign_out_sub_title)
        binding.ivDialog.setImageResource(R.drawable.ic_dialog_signout)
        binding.tvDialogTitle.text = getString(R.string.sign_out_title)
        binding.btnDialogSecond.isVisible = true
        binding.btnDialogFirst.isVisible = true
        binding.btnDialogOne.isVisible = false

    }

    private fun naverSignOut() {
        OAuthLogin.getInstance().logout(requireContext())
        signOutProcess()
    }

    private fun signOutProcess() {
        requireContext().toast(getString(R.string.success_sign_out))
        initUserData()
        moveSignInPage()
    }

    private fun googleSignOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        googleSignInClient.signOut().addOnCompleteListener {
            auth = FirebaseAuth.getInstance()
            auth.signOut()
            signOutProcess()
        }
    }

    private fun kakaoSignOut() {
        UserApiClient.rx.logout()
            .composeSchedulers()
            .subscribe({ signOutProcess() }, { })
            .addTo(disposables)
    }

    override fun clickListener() {
        binding.btnDialogFirst.setOnClickListener { dismiss() }
        binding.btnDialogSecond.setOnClickListener { viewModel.signOut() }
    }
}