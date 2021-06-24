package com.plogging.ecorun.ui.auth

import androidx.activity.viewModels
import com.plogging.ecorun.base.BaseActivity
import com.plogging.ecorun.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)
    override val viewModel by viewModels<MainViewModel>()
}