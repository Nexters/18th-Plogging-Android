package com.plogging.ecorun.ui.auth.password

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.databinding.FragmentFindPasswordBinding
import com.plogging.ecorun.util.extension.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindPasswordFragment : BaseFragment<FragmentFindPasswordBinding, FindPasswordViewModel>() {
    override fun getViewBinding() = FragmentFindPasswordBinding.inflate(layoutInflater)
    override val viewModel: FindPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initTitle()
        observingText()
        responseApi()
    }

    private fun initTitle() {
        binding.includeAuthFindPw.tapTitle.text = getString(R.string.find_password)
    }

    private fun responseApi() {
        viewModel.responseCode.observe(viewLifecycleOwner) {
            when (it) {
                200 -> {
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("isSentPw", true)
                    findNavController().popBackStack()
                }
                404 -> requireContext().toast(getString(R.string.no_email))
            }
        }
    }

    private fun observingText() {
        binding.etAuthFindPwEmail.addTextChangedListener { input ->
            when (input.toString().isValidEmail()) {
                null -> {
                    binding.etAuthFindPwEmail.setGrayBorder()
                    binding.tvAuthFindPwErr.isVisible = false
                    binding.btnAuthFindPw.isEnabled = false
                    binding.ivAuthFindPw.isVisible = false
                }
                true -> {
                    binding.etAuthFindPwEmail.setGreenBorder()
                    binding.tvAuthFindPwErr.isVisible = false
                    binding.btnAuthFindPw.isEnabled = true
                    binding.ivAuthFindPw.isVisible = true
                }
                false -> {
                    binding.etAuthFindPwEmail.setRedBorder()
                    binding.tvAuthFindPwErr.isVisible = true
                    binding.btnAuthFindPw.isEnabled = false
                    binding.ivAuthFindPw.isVisible = true
                }
            }
        }
    }

    override fun clickListener() {
        binding.includeAuthFindPw.ivBack.setOnClickListener {
            hideKeyboard(binding.etAuthFindPwEmail)
            findNavController().popBackStack()
        }
        binding.ivAuthFindPw.setOnClickListener { binding.etAuthFindPwEmail.text.clear() }
        binding.btnAuthFindPw.setOnClickListener {
            viewModel.email.value = binding.etAuthFindPwEmail.text.toString()
            hideKeyboard(it)
            viewModel.tempPassword()
        }
    }
}