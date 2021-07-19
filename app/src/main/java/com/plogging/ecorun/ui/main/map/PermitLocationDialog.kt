package com.plogging.ecorun.ui.main.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.plogging.ecorun.R
import com.plogging.ecorun.databinding.FragmentPermitLocationBinding
import com.plogging.ecorun.util.extension.GpsExtension

class PermitLocationDialog : BottomSheetDialogFragment() {

    private val gpsHelper by lazy { GpsExtension(requireContext(), requireActivity()) }
    private lateinit var binding: FragmentPermitLocationBinding
    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { true } && !gpsHelper.isGPSOn.value!!) {
                gpsHelper.checkGPS()
            }
        }

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

    private fun permissionCheck() {
        permissionRequest.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
    }

    private fun setClickListener() {
        binding.btnPermitLocationCancel.setOnClickListener { this.dismiss() }
        binding.btnPermitLocationOk.setOnClickListener { permissionCheck(); this.dismiss() }
    }
}