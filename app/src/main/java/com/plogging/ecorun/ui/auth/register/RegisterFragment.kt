package com.plogging.ecorun.ui.auth.register

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.databinding.FragmentRegisterBinding
import com.plogging.ecorun.util.extension.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo


class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel>() {
    override fun getViewBinding() = FragmentRegisterBinding.inflate(layoutInflater)
    override val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.include.tapTitle.setText(R.string.register)
        verifiedEmail()
        verifiedPassword()
        verifiedPasswordConfirm()
        isMatchedPassword()
        verifiedButton()
        idConflictView()
    }

    private fun idConflictView() {
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Int>("IdConflict")
            ?.observe(viewLifecycleOwner) {
                if (it == 410) {
                    binding.tvRegisterEmailErr.setText(R.string.already_id)
                    binding.tvRegisterEmailErr.isVisible = true
                    binding.etRegisterEmail.setRedBorder()
                }
            }
    }

    private fun verifiedEmail() {
        binding.etRegisterEmail.addTextChangedListener { input ->
            when (input.toString().isValidEmail()) {
                null -> {
                    binding.ivRegisterEmailCancel.isVisible = false
                    binding.tvRegisterEmailErr.isVisible = false
                    viewModel.isValidIdSubject.onNext(false)
                    binding.etRegisterEmail.setGrayBorder()
                }
                true -> {
                    binding.ivRegisterEmailCancel.isVisible = true
                    binding.tvRegisterEmailErr.isVisible = false
                    viewModel.isValidIdSubject.onNext(true)
                    binding.etRegisterEmail.setGreenBorder()
                }
                false -> {
                    binding.ivRegisterEmailCancel.isVisible = true
                    binding.tvRegisterEmailErr.isVisible = true
                    viewModel.isValidIdSubject.onNext(false)
                    binding.etRegisterEmail.setRedBorder()
                }
            }
        }
    }

    private fun verifiedPassword() {
        binding.etRegisterPw.addTextChangedListener { input ->
            when (input.toString().isValidPassword()) {
                null -> {
                    binding.tvRegisterPwErr.setTextColor(Color.parseColor("#898989"))
                    binding.ivRegisterPwCancel.isVisible = false
                    viewModel.isValidPwSubject.onNext(false)
                    binding.etRegisterPw.setGrayBorder()
                }
                true -> {
                    binding.tvRegisterPwErr.setTextColor(Color.parseColor("#898989"))
                    binding.ivRegisterPwCancel.isVisible = true
                    viewModel.isValidPwSubject.onNext(true)
                    binding.etRegisterPw.setGreenBorder()
                }
                false -> {
                    binding.tvRegisterPwErr.setTextColor(Color.parseColor("#ff697a"))
                    binding.ivRegisterPwCancel.isVisible = true
                    viewModel.isValidPwSubject.onNext(false)
                    binding.etRegisterPw.setRedBorder()
                }
            }
            isMatchedPassword()
        }
    }

    private fun verifiedPasswordConfirm() {
        binding.etRegisterPwConfirm.addTextChangedListener { isMatchedPassword() }
    }

    private fun isMatchedPassword() {
        when (viewModel.isMatchedPw(
            binding.etRegisterPw.text.toString(),
            binding.etRegisterPwConfirm.text.toString()
        )) {
            null -> {
                binding.ivRegisterPwConfirmCancel.isVisible = false
                binding.tvRegisterPwConfirmErr.isVisible = false
                viewModel.isMatchedPwSubject.onNext(false)
                binding.etRegisterPwConfirm.setGrayBorder()
            }
            true -> {
                binding.ivRegisterPwConfirmCancel.isVisible = true
                binding.tvRegisterPwConfirmErr.isVisible = false
                viewModel.isMatchedPwSubject.onNext(true)
                binding.etRegisterPwConfirm.setGreenBorder()
            }
            false -> {
                binding.ivRegisterPwConfirmCancel.isVisible = true
                binding.tvRegisterPwConfirmErr.isVisible = true
                viewModel.isMatchedPwSubject.onNext(false)
                binding.etRegisterPwConfirm.setRedBorder()
            }
        }
    }

    private fun verifiedButton() {
        viewModel.resisterButtonEnable()
        viewModel.buttonEnableSubject.observeOn(AndroidSchedulers.mainThread())
            .subscribe({ binding.btnRegisterNext.isEnabled = it }, {})
            .addTo(disposables)
    }

    override fun clickListener() {
        binding.include.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnRegisterNext.setOnClickListener {
            val bundle = bundleOf(
                "email" to binding.etRegisterEmail.text.toString(),
                "password" to binding.etRegisterPw.text.toString()
            )
            findNavController().navigate(R.id.action_register_to_nick_name, bundle)
        }
        binding.ivRegisterEmailCancel.setOnClickListener {
            binding.etRegisterEmail.requestFocus()
            binding.etRegisterEmail.text.clear()
        }
        binding.ivRegisterPwCancel.setOnClickListener {
            binding.etRegisterPw.requestFocus()
            binding.etRegisterPw.text.clear()
        }
        binding.ivRegisterPwConfirmCancel.setOnClickListener {
            binding.etRegisterPwConfirm.requestFocus()
            binding.etRegisterPwConfirm.text.clear()
        }
    }
}