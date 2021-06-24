package com.plogging.ecorun.ui.main.rank

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragmentDialog
import com.plogging.ecorun.databinding.FragmentDialogBinding
import com.plogging.ecorun.ui.running.save.SaveViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankScoreDialog : BaseFragmentDialog<FragmentDialogBinding, SaveViewModel>() {
    override fun getViewBinding() = FragmentDialogBinding.inflate(layoutInflater)
    override val viewModel: SaveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun initView() {
        binding.tvDialogSubTitle.text = getString(R.string.dialog_rank_subtitle)
        binding.tvDialogTitle.text = getString(R.string.dialog_rank_title)
        binding.ivDialog.setImageResource(R.drawable.ic_dialog_rank)
    }

    override fun clickListener() {
        binding.btnDialogOne.setOnClickListener { dismiss() }
    }
}