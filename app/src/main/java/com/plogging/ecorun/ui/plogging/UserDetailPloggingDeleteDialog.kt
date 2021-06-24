package com.plogging.ecorun.ui.plogging

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragmentDialog
import com.plogging.ecorun.databinding.FragmentDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDetailPloggingDeleteDialog :
    BaseFragmentDialog<FragmentDialogBinding, UserDetailPloggingViewModel>() {
    override fun getViewBinding() = FragmentDialogBinding.inflate(layoutInflater)
    override val viewModel: UserDetailPloggingViewModel by viewModels()
    private lateinit var userDetailPloggingViewModel: UserDetailPloggingViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSharedViewModel()
        responseApi()
        initView()
    }

    private fun initSharedViewModel() {
        parentFragmentManager.fragments[0].let {
            userDetailPloggingViewModel =
                ViewModelProvider(it).get(UserDetailPloggingViewModel::class.java)
        }
    }

    private fun responseApi() {
        userDetailPloggingViewModel.responseCode.observe(viewLifecycleOwner) {
            if (it == 200) dismiss()
        }
    }

    override fun initView() {
        binding.tvDialogSubTitle.text = getString(R.string.plogging_finish)
        binding.ivDialog.setImageResource(R.drawable.ic_dialog_finish_plogging)
        binding.tvDialogTitle.text = getString(R.string.plogging_finish_title)
        binding.btnDialogSecond.text = getString(R.string.delete)
        binding.btnDialogSecond.isVisible = true
        binding.btnDialogFirst.isVisible = true
        binding.btnDialogOne.isVisible = false
    }

    override fun clickListener() {
        binding.btnDialogFirst.setOnClickListener { dismiss() }
        binding.btnDialogSecond.setOnClickListener {
            userDetailPloggingViewModel.ploggingId.value = arguments?.get("ploggingId").toString()
            userDetailPloggingViewModel.ploggingImageName.value =
                arguments?.get("ploggingImgName").toString()
            userDetailPloggingViewModel.deleteMyPlogging()
        }
    }
}
