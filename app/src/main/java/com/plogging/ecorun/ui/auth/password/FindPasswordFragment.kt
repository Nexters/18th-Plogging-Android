package com.plogging.ecorun.ui.auth.password

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
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
                    binding.tvAuthFindPwErr.visibility = INVISIBLE
                    binding.ivAuthFindPw.visibility = INVISIBLE
                    binding.btnAuthFindPw.isEnabled = false
                }
                true -> {
                    binding.etAuthFindPwEmail.setGreenBorder()
                    binding.tvAuthFindPwErr.visibility = INVISIBLE
                    binding.ivAuthFindPw.visibility = VISIBLE
                    binding.btnAuthFindPw.isEnabled = true
                }
                false -> {
                    binding.etAuthFindPwEmail.setRedBorder()
                    binding.tvAuthFindPwErr.visibility = VISIBLE
                    binding.ivAuthFindPw.visibility = VISIBLE
                    binding.btnAuthFindPw.isEnabled = false
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