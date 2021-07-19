package com.plogging.ecorun.ui.main.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.databinding.FragmentMapBinding
import com.plogging.ecorun.event.EventImpl
import com.plogging.ecorun.event.RxBus
import com.plogging.ecorun.ui.main.MainViewModel
import com.plogging.ecorun.util.extension.GpsExtension
import com.plogging.ecorun.util.extension.createDrawableFromView
import com.plogging.ecorun.util.extension.isPermissionGranted
import com.plogging.ecorun.util.extension.toast

class MapFragment : BaseFragment<FragmentMapBinding, MapViewModel>() {
    private val gpsHelper by lazy { GpsExtension(requireContext(), requireActivity()) }
    override fun getViewBinding() = FragmentMapBinding.inflate(layoutInflater)
    private val markerView by lazy {
        LayoutInflater.from(requireContext()).inflate(R.layout.item_marker, null)
    }
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mainViewModel: MainViewModel
    override val viewModel: MapViewModel by viewModels()
    internal var currLocationMarker: Marker? = null
    private lateinit var markerImg: ImageView
    private var longitude: Double? = 126.9780
    private var latitude: Double? = 37.5665
    private lateinit var map: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSharedViewModel()
        setMarkerImg()
        getMyName()
        initView()
        checkPermission()
        initLocationCallback()
    }

    override fun onStop() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        mapFragment.onExitAmbient()
        super.onStop()
    }

    private fun initSharedViewModel() {
        parentFragment?.parentFragment?.let {
            mainViewModel = ViewModelProvider(it).get(MainViewModel::class.java)
        }
        mainViewModel.showBottomNav.value = true
    }

    private fun initLocationCallback() {
        locationCallback = object : LocationCallback() {
            val bitmapMarker = createDrawableFromView(requireActivity(), markerView)
            override fun onLocationResult(locationResult: LocationResult) {
                val locationList = locationResult.locations
                if (locationList.isNotEmpty()) {
                    val location = locationList.last()
                    val latLng = LatLng(location.latitude, location.longitude)
                    SharedPreference.setLongitude(requireContext(), latLng.longitude.toFloat())
                    SharedPreference.setLatitude(requireContext(), latLng.latitude.toFloat())
                    gpsHelper.isGPSOn.value = true
                    //set marker
                    if (currLocationMarker != null) currLocationMarker?.remove()
                    else map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0F))
                    currLocationMarker = map.addMarker {
                        icon(BitmapDescriptorFactory.fromBitmap(bitmapMarker))
                        position(latLng)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getMyName() {
        val name = SharedPreference.getUserName(requireContext())
        val text = "<b>${name}</b>님 안녕하세요!"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvMapTitle.text = Html.fromHtml(text, 0)
        } else binding.tvMapTitle.text = "${name}님 안녕하세요!"
    }

    private fun initView() {
        //xml에서 MapView는 사용이 안됨
        mapFragment =
            childFragmentManager.findFragmentById(R.id.map_plogging) as SupportMapFragment
        lifecycle.coroutineScope.launchWhenCreated {
            map = mapFragment.awaitMap()
            // 1 초마다 위치 정보 가져오기
            locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                fastestInterval = 1000
                interval = 1000
            }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            longitude = SharedPreference.getLongitude(requireContext()).toDouble()
            latitude = SharedPreference.getLatitude(requireContext()).toDouble()
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude!!, longitude!!), 17f))
            if (allGranted()) getMyLocation()
        }
    }

    private fun setMarkerImg() {
        markerImg = markerView.findViewById(R.id.iv_marker)
        markerImg.setImageURI(SharedPreference.getUserImage(requireContext()))
    }

    private fun checkPermission() {
        if (!allGranted() || !gpsHelper.isGPSOn.value!!) findNavController().navigate(R.id.nav_plogging_permission)
    }

    private fun allGranted() =
        requireContext().isPermissionGranted(ACCESS_FINE_LOCATION) &&
                requireContext().isPermissionGranted(ACCESS_COARSE_LOCATION)

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()!!
        )
    }

    override fun clickListener() {
        binding.btnPloggingStart.setOnClickListener {
            if (mainViewModel.responseCode.value != 200) {
                RxBus.post(EventImpl.NetworkErrorEvent())
                return@setOnClickListener
            }
            if (allGranted() && gpsHelper.isGPSOn.value == true) {
                fusedLocationClient?.removeLocationUpdates(locationCallback)
                findNavController().navigate(R.id.action_map_to_running)
            } else requireContext().toast(getString(R.string.turn_off_gps))
        }
        binding.tvPloggingGuide.setOnClickListener {
            findNavController().navigate(R.id.action_map_to_guide)
        }
        binding.fbMap.setOnClickListener {
            if (allGranted()) {
                fusedLocationClient?.removeLocationUpdates(locationCallback)
                getMyLocation()
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(latitude!!, longitude!!), 17.0F)
                )
            }
        }
    }
}
