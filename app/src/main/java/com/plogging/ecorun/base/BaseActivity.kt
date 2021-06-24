package com.plogging.ecorun.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<B : ViewBinding, VM : ViewModel> : AppCompatActivity() {

    protected abstract fun getViewBinding(): B
    protected abstract val viewModel: VM
    protected lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
    }
}