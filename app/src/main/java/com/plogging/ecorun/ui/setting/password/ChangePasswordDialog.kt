package com.plogging.ecorun.ui.setting.password

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragmentDialog
import com.plogging.ecorun.databinding.FragmentDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordDialog : BaseFragmentDialog<FragmentDialogBinding, ChangePasswordViewModel>() {
    override fun getViewBinding() = FragmentDialogBinding.inflate(layoutInflater)
    override val viewModel: ChangePasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun initView() {
        binding.tvDialogSubTitle.text = getString(R.string.complete_change_pw_sub)
        binding.ivDialog.setImageResource(R.drawable.ic_dialog_change_password)
        binding.tvDialogTitle.text = getString(R.string.complete_change_pw)
    }

    override fun clickListener() {
        binding.btnDialogOne.setOnClickListener { dismiss() }
    }
}