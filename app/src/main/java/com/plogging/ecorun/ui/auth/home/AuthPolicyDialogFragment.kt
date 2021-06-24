package com.plogging.ecorun.ui.auth.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.plogging.ecorun.R
import com.plogging.ecorun.databinding.FragmentDialogAuthPolicyBinding

class AuthPolicyDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentDialogAuthPolicyBinding.inflate(inflater, container, false).root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
        return super.onCreateDialog(savedInstanceState)
    }
}