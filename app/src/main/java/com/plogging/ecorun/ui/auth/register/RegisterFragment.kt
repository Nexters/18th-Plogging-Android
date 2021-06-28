package com.plogging.ecorun.ui.auth.register

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.core.os.bundleOf
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
                    binding.tvRegisterEmailErr.visibility = VISIBLE
                    binding.etRegisterEmail.setRedBorder()
                }
            }
    }

    private fun verifiedEmail() {
        binding.etRegisterEmail.addTextChangedListener { input ->
            when (input.toString().isValidEmail()) {
                null -> {
                    binding.ivRegisterEmailCancel.visibility = INVISIBLE
                    binding.tvRegisterEmailErr.visibility = INVISIBLE
                    viewModel.isValidIdSubject.onNext(false)
                    binding.etRegisterEmail.setGrayBorder()
                }
                true -> {
                    binding.ivRegisterEmailCancel.visibility = VISIBLE
                    binding.tvRegisterEmailErr.visibility = INVISIBLE
                    viewModel.isValidIdSubject.onNext(true)
                    binding.etRegisterEmail.setGreenBorder()
                }
                false -> {
                    binding.ivRegisterEmailCancel.visibility = VISIBLE
                    binding.tvRegisterEmailErr.visibility = VISIBLE
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
                    binding.ivRegisterPwCancel.visibility = INVISIBLE
                    viewModel.isValidPwSubject.onNext(false)
                    binding.etRegisterPw.setGrayBorder()
                }
                true -> {
                    binding.tvRegisterPwErr.setTextColor(Color.parseColor("#898989"))
                    binding.ivRegisterPwCancel.visibility = VISIBLE
                    viewModel.isValidPwSubject.onNext(true)
                    binding.etRegisterPw.setGreenBorder()
                }
                false -> {
                    binding.tvRegisterPwErr.setTextColor(Color.parseColor("#ff697a"))
                    binding.ivRegisterPwCancel.visibility = VISIBLE
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
        when (isMatched(
            binding.etRegisterPw.text.toString(),
            binding.etRegisterPwConfirm.text.toString()
        )) {
            null -> {
                binding.ivRegisterPwConfirmCancel.visibility = INVISIBLE
                binding.tvRegisterPwConfirmErr.visibility = INVISIBLE
                viewModel.isMatchedPwSubject.onNext(false)
                binding.etRegisterPwConfirm.setGrayBorder()
            }
            true -> {
                binding.ivRegisterPwConfirmCancel.visibility = VISIBLE
                binding.tvRegisterPwConfirmErr.visibility = INVISIBLE
                viewModel.isMatchedPwSubject.onNext(true)
                binding.etRegisterPwConfirm.setGreenBorder()
            }
            false -> {
                binding.ivRegisterPwConfirmCancel.visibility = VISIBLE
                binding.tvRegisterPwConfirmErr.visibility = VISIBLE
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