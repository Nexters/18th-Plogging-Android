package com.plogging.ecorun.ui.setting.password

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.databinding.FragmentChangePasswordBinding
import com.plogging.ecorun.ui.main.MainViewModel
import com.plogging.ecorun.util.extension.isMatched
import com.plogging.ecorun.util.extension.isValidPassword
import com.plogging.ecorun.util.extension.setGrayBorder
import com.plogging.ecorun.util.extension.setRedBorder
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo

@AndroidEntryPoint
class ChangePasswordFragment :
    BaseFragment<FragmentChangePasswordBinding, ChangePasswordViewModel>() {
    override fun getViewBinding() = FragmentChangePasswordBinding.inflate(layoutInflater)
    override val viewModel: ChangePasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTitle()
        bottomViewDown()
        verifiedButton()
        verifiedCurrentPassword()
        verifiedNewPassword()
        verifiedPasswordConfirm()
        isMatchedPassword()
        verifiedButton()
        responseApi()
    }

    private fun bottomViewDown() {
        parentFragment?.parentFragment?.let {
            ViewModelProvider(it).get(MainViewModel::class.java).showBottomNav.value = false
        }
    }

    private fun initTitle() {
        binding.include.tapTitle.text = getString(R.string.change_password)
    }

    private fun verifiedCurrentPassword() {
        binding.etChangeCurrentPw.addTextChangedListener {
            binding.ivChangeCurrentPwCancel.isVisible = it.toString().isNotEmpty()
            viewModel.isNotEmptyCurrentPwSubject.onNext(it.toString().isNotEmpty())
            binding.tvChangeCurrentPwErr.visibility = INVISIBLE
        }
    }

    private fun verifiedNewPassword() {
        binding.etChangeNewPw.addTextChangedListener { input ->
            when (input.toString().isValidPassword()) {
                null -> {
                    binding.tvChangeNewPwErr.setTextColor(Color.parseColor("#898989"))
                    binding.ivChangePwNewCancel.visibility = INVISIBLE
                    viewModel.isValidNewPwSubject.onNext(false)
                    binding.etChangeNewPw.setGrayBorder()
                }
                true -> {
                    binding.tvChangeNewPwErr.setTextColor(Color.parseColor("#898989"))
                    binding.ivChangePwNewCancel.visibility = VISIBLE
                    viewModel.isValidNewPwSubject.onNext(true)
                    binding.etChangeNewPw.setGrayBorder()
                }
                false -> {
                    binding.tvChangeNewPwErr.setTextColor(Color.parseColor("#ff697a"))
                    binding.ivChangePwNewCancel.visibility = VISIBLE
                    viewModel.isValidNewPwSubject.onNext(false)
                    binding.etChangeNewPw.setRedBorder()
                }
            }
            isMatchedPassword()
        }
    }

    private fun verifiedPasswordConfirm() {
        binding.etChangeConfirmPw.addTextChangedListener { isMatchedPassword() }
    }

    private fun isMatchedPassword() {
        when (isMatched(
            binding.etChangeNewPw.text.toString(),
            binding.etChangeConfirmPw.text.toString()
        )) {
            null -> {
                binding.ivChangeConfirmPwCancel.visibility = INVISIBLE
                binding.tvChangeConfirmPwErr.visibility = INVISIBLE
                viewModel.isMatchedPwSubject.onNext(false)
                binding.etChangeConfirmPw.setGrayBorder()
            }
            true -> {
                binding.ivChangeConfirmPwCancel.visibility = VISIBLE
                binding.tvChangeConfirmPwErr.visibility = INVISIBLE
                viewModel.isMatchedPwSubject.onNext(true)
                binding.etChangeConfirmPw.setGrayBorder()
            }
            false -> {
                binding.ivChangeConfirmPwCancel.visibility = VISIBLE
                binding.tvChangeConfirmPwErr.visibility = VISIBLE
                viewModel.isMatchedPwSubject.onNext(false)
                binding.etChangeConfirmPw.setRedBorder()
            }
        }
    }

    private fun responseApi() {
        viewModel.responseCode.observe(viewLifecycleOwner) {
            when (it) {
                200 -> findNavController().navigate(R.id.action_change_password_to_dialog)
                402 -> binding.tvChangeCurrentPwErr.visibility = VISIBLE
            }
        }
    }

    private fun verifiedButton() {
        viewModel.changePwButtonEnable()
        viewModel.buttonEnableSubject.observeOn(AndroidSchedulers.mainThread())
            .subscribe { binding.btnChangePw.isEnabled = it }
            .addTo(disposables)
    }

    override fun clickListener() {
        binding.include.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnChangePw.setOnClickListener {
            viewModel.oldPassword.value = binding.etChangeCurrentPw.text.toString()
            viewModel.newPassword.value = binding.etChangeNewPw.text.toString()
            viewModel.changePassword()
        }
        binding.ivChangeCurrentPwCancel.setOnClickListener {
            binding.etChangeCurrentPw.requestFocus()
            binding.etChangeCurrentPw.text.clear()
        }
        binding.ivChangePwNewCancel.setOnClickListener {
            binding.etChangeNewPw.requestFocus()
            binding.etChangeNewPw.text.clear()
        }
        binding.ivChangeConfirmPwCancel.setOnClickListener {
            binding.etChangeConfirmPw.requestFocus()
            binding.etChangeConfirmPw.text.clear()
        }
    }
}