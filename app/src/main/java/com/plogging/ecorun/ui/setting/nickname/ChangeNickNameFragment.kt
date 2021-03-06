package com.plogging.ecorun.ui.setting.nickname

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.databinding.FragmentChangeNickNameBinding
import com.plogging.ecorun.ui.main.MainViewModel
import com.plogging.ecorun.util.extension.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeNickNameFragment :
    BaseFragment<FragmentChangeNickNameBinding, ChangeNickNameViewModel>() {
    override fun getViewBinding() = FragmentChangeNickNameBinding.inflate(layoutInflater)
    override val viewModel: ChangeNickNameViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bottomViewDown()
        observingText()
        responseApi()
    }

    private fun bottomViewDown() {
        parentFragment?.parentFragment?.let {
            ViewModelProvider(it).get(MainViewModel::class.java).showBottomNav.value = false
        }
    }

    private fun initView() {
        binding.include.tapTitle.text = getString(R.string.change_nickname)
        binding.etSettingNickName.hint = SharedPreference.getUserName(requireContext())
    }

    private fun showLoadingPage(show: Boolean) {
        binding.clSettingNickNameProgress.isVisible = show
        binding.clSettingNickNameProgress.setOnClickListener { !show }
    }

    private fun responseApi() {
        viewModel.responseCode.observe(viewLifecycleOwner) {
            showLoadingPage(false)
            when (it) {
                200 -> {
                    requireContext().toast(getString(R.string.changed_nickname))
                    SharedPreference.setUserName(requireContext(), viewModel.nickname.value!!)
                    findNavController().popBackStack()
                }
                409 -> binding.tvSettingNickNameAlert.visibility = VISIBLE
            }
        }
    }

    private fun observingText() {
        binding.etSettingNickName.doOnTextChanged { _, _, _, _ ->
            binding.tvSettingNickNameAlert.visibility = INVISIBLE
            binding.btnSettingNickName.isEnabled =
                binding.etSettingNickName.text.isNotBlank() && binding.etSettingNickName.text.length <= 9
        }
    }

    override fun clickListener() {
        binding.include.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnSettingNickName.setOnClickListener {
            showLoadingPage(true)
            viewModel.nickname.value = binding.etSettingNickName.text.toString()
            viewModel.changeNickname()
            hideKeyboard(it)
        }
    }
}