package com.plogging.ecorun.ui.auth.nickname

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.databinding.FragmentNickNameBinding
import com.plogging.ecorun.ui.auth.MainActivity
import com.plogging.ecorun.util.constant.Constant.CUSTOM
import com.plogging.ecorun.util.extension.composeSchedulers
import com.plogging.ecorun.util.extension.saveImageIn
import com.plogging.ecorun.util.extension.toBitmap
import com.plogging.ecorun.util.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.addTo
import java.io.FileNotFoundException
import java.net.URL
import java.util.concurrent.TimeUnit


@Suppress("UNCHECKED_CAST")
@AndroidEntryPoint
class NickNameFragment : BaseFragment<FragmentNickNameBinding, NickNameViewModel>() {

    override fun getViewBinding() = FragmentNickNameBinding.inflate(layoutInflater)
    private var socialType: String? = arguments?.get("socialType").toString()
    override val viewModel: NickNameViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        responseApi()
        observingText()
    }

    private fun initView() {
        binding.include.tapTitle.text = getString(R.string.setting_nickname)
        viewModel.userId.value = arguments?.get("email").toString()
        viewModel.secretKey.value = arguments?.get("password").toString()
    }

    private fun observingText() {
        binding.etAuthNickName.addTextChangedListener {
            binding.btnNickName.isEnabled = it.toString().isNotBlank() && it.toString().length <= 9
        }
    }

    private fun responseApi() {
        viewModel.responseCode.observe(viewLifecycleOwner) {
            showLoadingPage(false)
            when (it) {
                200, 201 -> saveUserImage()
                409 -> binding.tvAuthNickNameAlert.isVisible = true
                410 -> {
                    requireContext().toast(getString(R.string.already_id))
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("IdConflict", 410)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun saveUserImage() {
        URL(viewModel.userUri.value).toBitmap()
            .flatMap { it.saveImageIn(requireContext().contentResolver) }
            .composeSchedulers()
            .retryWhen { attempts ->
                Flowables.zip(
                    attempts.map { error -> if (error is FileNotFoundException) error else throw error },
                    Flowable.interval(1, TimeUnit.SECONDS)
                ).map { (error, retryCount) -> if (retryCount >= 3) throw error }
            }
            .subscribe({
                requireContext().toast(getString(R.string.success_register))
                saveUserData(it)
                moveNextPage()
            }, {})
            .addTo(disposables)
    }

    private fun saveUserData(uri: Uri?) {
        if (!viewModel.userId.value?.contains(":")!!) viewModel.userId.value += ":$CUSTOM"
        SharedPreference.setUserName(requireContext(), viewModel.userName.value!!)
        SharedPreference.setUserEmail(requireContext(), viewModel.userId.value!!)
        SharedPreference.setUserImage(requireContext(), uri)
    }

    private fun moveNextPage() {
        if (SharedPreference.getIsFirstUser(requireContext()))
            findNavController().navigate(R.id.action_register_to_on_boarding)
        else findNavController().navigate(R.id.action_register_to_main)
    }

    private fun showLoadingPage(show: Boolean) {
        binding.clNickNameProgress.isVisible = show
        binding.clNickNameProgress.setOnClickListener { !show }
    }

    override fun clickListener() {
        binding.include.ivBack.setOnClickListener {
            if (socialType == null) findNavController().popBackStack()
            else {
                activity?.finish()
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
            hideKeyboard(binding.etAuthNickName)
        }
        binding.btnNickName.setOnClickListener {
            hideKeyboard(binding.etAuthNickName)
            binding.tvAuthNickNameAlert.isVisible = false
            viewModel.userName.value = binding.etAuthNickName.text.toString()
            showLoadingPage(true)
            if (viewModel.userId.value?.contains(":") == false) viewModel.saveUser()
            else viewModel.saveSocialUser()
        }
    }
}
