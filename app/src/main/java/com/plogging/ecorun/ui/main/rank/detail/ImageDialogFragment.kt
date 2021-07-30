package com.plogging.ecorun.ui.main.rank.detail

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.plogging.ecorun.base.BaseFragmentDialog
import com.plogging.ecorun.databinding.FragmentDialogImageBinding
import com.plogging.ecorun.ui.main.rank.RankViewModel
import com.plogging.ecorun.util.glide.GlideApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageDialogFragment : BaseFragmentDialog<FragmentDialogImageBinding, RankViewModel>() {
    override fun getViewBinding() = FragmentDialogImageBinding.inflate(layoutInflater)
    override val viewModel: RankViewModel by viewModels()

    override fun clickListener() {
        binding.ivImageDialogCancel.setOnClickListener { findNavController().popBackStack() }
    }

    override fun initView() {
        val imageUri = arguments?.getString("imageUri").toString()
        GlideApp.with(requireContext()).load(imageUri).into(binding.ivPloggingImage)
    }
}