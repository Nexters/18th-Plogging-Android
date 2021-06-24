package com.plogging.ecorun.ui.main.rank.detail

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.plogging.ecorun.base.BaseFragmentDialog
import com.plogging.ecorun.databinding.FragmentImageDialogBinding
import com.plogging.ecorun.ui.main.rank.RankViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageDialogFragment : BaseFragmentDialog<FragmentImageDialogBinding, RankViewModel>() {
    override fun getViewBinding() = FragmentImageDialogBinding.inflate(layoutInflater)
    override val viewModel: RankViewModel by viewModels()

    override fun clickListener() {
        binding.ivImageDialogCancel.setOnClickListener { findNavController().popBackStack() }
    }

    override fun initView() {
        val imageUri = arguments?.getString("imageUri").toString()
        Glide.with(requireContext()).load(imageUri).into(binding.ivPloggingImage)
    }
}