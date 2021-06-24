package com.plogging.ecorun.ui.setting.profile

import androidx.fragment.app.viewModels
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.databinding.FragmentAlbumBinding

class ProfileAlbumFragment : BaseFragment<FragmentAlbumBinding, ProfileViewModel>() {
    override fun getViewBinding() = FragmentAlbumBinding.inflate(layoutInflater)
    override val viewModel: ProfileViewModel by viewModels()
    override fun clickListener() {}
}