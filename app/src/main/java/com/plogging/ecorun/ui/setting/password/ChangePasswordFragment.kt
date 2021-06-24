package com.plogging.ecorun.ui.setting.password

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.databinding.FragmentChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment :
    BaseFragment<FragmentChangePasswordBinding, ChangePasswordViewModel>() {
    override fun getViewBinding() = FragmentChangePasswordBinding.inflate(layoutInflater)
    override val viewModel: ChangePasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTitle()
        verifiedButton()
        verifiedPassword()
        responseApi()
    }

    private fun initTitle() {
        binding.include.tapTitle.text = getString(R.string.change_password)
    }

    private fun responseApi() {
        viewModel.responseCode.observe(viewLifecycleOwner) {
            when (it) {
                200 -> findNavController().navigate(R.id.action_change_password_to_dialog)
                402 -> binding.tvChangeCurrentPwErr.isVisible = true
            }
        }
    }

    private fun verifiedButton() {
        viewModel.clickableButton.observe(viewLifecycleOwner) {
            binding.btnChangePwNext.isEnabled = it
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun verifiedPassword() {
        binding.etChangePw.addTextChangedListener {
            binding.tvChangeCurrentPwErr.isVisible = false
            binding.ivChangeCurrentPwCancel.isVisible = binding.etChangePw.text.isNotBlank()
            viewModel.isClickableButton()
        }
        binding.etChangeNewPw.addTextChangedListener {
            viewModel.oldPassword.value = binding.etChangeNewPw.text.toString()
            binding.ivChangePwCancel.isVisible = binding.etChangeNewPw.text.isNotBlank()
            when {
                binding.etChangeNewPw.text.isNotEmpty() -> viewModel.isPassword(binding.etChangeNewPw.text.toString())
                else -> {
                    binding.etChangeNewPw.setBackgroundResource(R.drawable.bg_round_border_gray_4)
                    binding.tvChangePwErr.setTextColor(R.color.brown_gray)
                }
            }
            when {
                binding.etChangePwConfirm.text.isNotEmpty() -> viewModel.isPasswordMatched()
            }
            viewModel.isClickableButton()
        }
        binding.etChangePwConfirm.addTextChangedListener {
            viewModel.newPassword.value = binding.etChangePwConfirm.text.toString()
            binding.ivChangePwConfirmCancel.isVisible = binding.etChangePwConfirm.text.isNotBlank()
            when {
                binding.etChangePwConfirm.text.isNotEmpty() -> viewModel.isPasswordMatched()
                else -> {
                    binding.etChangePwConfirm.setBackgroundResource(R.drawable.bg_round_border_gray_4)
                    binding.tvChangePwConfirmErr.isVisible = false
                    viewModel.passwordMatch.value = null
                }
            }
            viewModel.isClickableButton()
        }
        viewModel.passwordCheck.observe(this.viewLifecycleOwner, {
            when (it) {
                true -> {
                    binding.tvChangePwErr.setTextColor(R.color.brown_gray)
                    binding.etChangeNewPw.setBackgroundResource(R.drawable.bg_round_border_green_4)
                }
                false -> {
                    binding.tvChangePwErr.setTextColor(Color.RED)
                    binding.etChangeNewPw.setBackgroundResource(R.drawable.bg_round_border_red_4)
                }
            }
        })
        viewModel.passwordMatch.observe(this.viewLifecycleOwner, {
            when (it) {
                true -> {
                    binding.tvChangePwConfirmErr.isVisible = false
                    binding.etChangePwConfirm.setBackgroundResource(R.drawable.bg_round_border_green_4)
                }
                false -> {
                    binding.tvChangePwConfirmErr.isVisible = true
                    binding.etChangePwConfirm.setBackgroundResource(R.drawable.bg_round_border_red_4)
                }
            }
        })
    }

    override fun clickListener() {
        binding.include.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnChangePwNext.setOnClickListener {
            viewModel.oldPassword.value = binding.etChangePw.text.toString()
            viewModel.newPassword.value = binding.etChangeNewPw.text.toString()
            viewModel.changePassword()
        }
        binding.ivChangeCurrentPwCancel.setOnClickListener {
            binding.etChangePw.requestFocus()
            binding.etChangePw.text.clear()
        }
        binding.ivChangePwCancel.setOnClickListener {
            binding.etChangeNewPw.requestFocus()
            binding.etChangeNewPw.text.clear()
        }
        binding.ivChangePwConfirmCancel.setOnClickListener {
            binding.etChangePwConfirm.requestFocus()
            binding.etChangePwConfirm.text.clear()
        }
    }
}