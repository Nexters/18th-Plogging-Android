package com.plogging.ecorun.ui.main

import androidx.lifecycle.MutableLiveData
import com.plogging.ecorun.base.BaseViewModel

class MainViewModel : BaseViewModel() {
    val showBottomNav = MutableLiveData(false)
}