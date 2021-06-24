package com.plogging.ecorun.ui.running.active

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragmentDialog
import com.plogging.ecorun.databinding.FragmentDialogBinding
import com.plogging.ecorun.ui.auth.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunningFinishDialog : BaseFragmentDialog<FragmentDialogBinding, RunningViewModel>() {
    override fun getViewBinding() = FragmentDialogBinding.inflate(layoutInflater)
    override val viewModel by lazy { ViewModelProvider(this).get(RunningViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun initView() {
        binding.btnDialogOne.isVisible = false
        binding.btnDialogFirst.isVisible = true
        binding.btnDialogSecond.isVisible = true
        binding.ivDialog.setImageResource(R.drawable.ic_dialog_finish_plogging)
        binding.tvDialogTitle.text = getString(R.string.plogging_finish_title)
        if (arguments?.get("stop") == "stop") {
            binding.tvDialogSubTitle.text = getString(R.string.running_out)
        } else {
            binding.tvDialogSubTitle.text = getString(R.string.plogging_finish)
        }
    }

    override fun clickListener() {
        if (arguments?.get("stop") == "stop") {
            binding.btnDialogFirst.setOnClickListener { dismiss() }
            binding.btnDialogSecond.setOnClickListener {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                parentFragment?.activity?.finish()
            }
        } else {
            binding.btnDialogFirst.setOnClickListener { dismiss() }
            binding.btnDialogSecond.setOnClickListener {
                val bundle = bundleOf(
                    getString(R.string.distance) to arguments?.get(getString(R.string.distance)),
                    getString(R.string.trash_type) to arguments?.get(getString(R.string.trash_type)),
                    getString(R.string.running_time) to arguments?.get(getString(R.string.running_time))
                )
                findNavController().navigate(R.id.action_running_finish_to_save, bundle)
            }
        }
    }
}