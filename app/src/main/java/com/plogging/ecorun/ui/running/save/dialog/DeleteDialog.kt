package com.plogging.ecorun.ui.running.save.dialog

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragmentDialog
import com.plogging.ecorun.databinding.FragmentDialogBinding
import com.plogging.ecorun.ui.auth.MainActivity
import com.plogging.ecorun.ui.running.save.SaveViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteDialog : BaseFragmentDialog<FragmentDialogBinding, SaveViewModel>() {
    override fun getViewBinding() = FragmentDialogBinding.inflate(layoutInflater)
    override val viewModel: SaveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun initView() {
        binding.tvDialogSubTitle.text = getString(R.string.dialog_delete_sub_title)
        binding.tvDialogTitle.text = getString(R.string.dialog_delete_title)
        binding.ivDialog.setImageResource(R.drawable.ic_dialog_delete)
        binding.btnDialogSecond.isVisible = true
        binding.btnDialogFirst.isVisible = true
        binding.btnDialogOne.isVisible = false
    }

    override fun clickListener() {
        binding.btnDialogFirst.setOnClickListener { dismiss() }
        binding.btnDialogSecond.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
            parentFragment?.activity?.finish()
        }
    }
}