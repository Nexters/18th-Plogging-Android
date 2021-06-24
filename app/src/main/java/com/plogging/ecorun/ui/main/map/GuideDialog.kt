package com.plogging.ecorun.ui.main.map

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.plogging.ecorun.R
import com.plogging.ecorun.databinding.FragmentDialogPloggingGuideBinding

class GuideDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentDialogPloggingGuideBinding.inflate(inflater, container, false).root


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
        return super.onCreateDialog(savedInstanceState)
    }
}