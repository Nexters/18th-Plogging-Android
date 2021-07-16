package com.plogging.ecorun.ui.main.map

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.plogging.ecorun.R
import com.plogging.ecorun.databinding.FragmentPermitLocationBinding
import com.plogging.ecorun.util.extension.GpsExtension

class PermitLocationDialog : BottomSheetDialogFragment() {

    private val gpsHelper by lazy { GpsExtension(requireContext(), requireActivity()) }
    private lateinit var binding: FragmentPermitLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentPermitLocationBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        binding.btnPermitLocationCancel.setOnClickListener { this.dismiss() }
        binding.btnPermitLocationOk.setOnClickListener { this.dismiss(); gpsHelper.checkGPS() }
    }
}