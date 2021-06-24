package com.plogging.ecorun.ui.running.save.dialog

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.plogging.ecorun.base.BaseFragmentDialog
import com.plogging.ecorun.databinding.FragmentDialogBinding
import com.plogging.ecorun.ui.running.save.SaveViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScoreDialog : BaseFragmentDialog<FragmentDialogBinding, SaveViewModel>() {
    override fun getViewBinding() = FragmentDialogBinding.inflate(layoutInflater)
    override val viewModel: SaveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun initView() {}
    override fun clickListener() {
        binding.btnDialogOne.setOnClickListener { dismiss() }
    }
}