package com.plogging.ecorun.ui.running.active

import android.Manifest
import android.content.*
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.SphericalUtil
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.addPolyline
import com.google.maps.android.ktx.awaitMap
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.databinding.FragmentRunningBinding
import com.plogging.ecorun.ui.main.MainViewModel
import com.plogging.ecorun.util.extension.*
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo

@AndroidEntryPoint
class RunningFragment : BaseFragment<FragmentRunningBinding, RunningViewModel>() {
    override fun getViewBinding() = FragmentRunningBinding.inflate(layoutInflater)
    private val runningBroadcastReceiver by lazy { RunningBroadcastReceiver() }
    private lateinit var runningServiceConnection: ServiceConnection
    override val viewModel: RunningViewModel by viewModels()
    val latLngList: MutableList<LatLng> = mutableListOf()
    private var runningService: RunningService? = null
    private lateinit var mainViewModel: MainViewModel
    private var runningLocationServiceBound = false
    var currentMarker: Marker? = null
    private lateinit var map: GoogleMap
    var startMarker: Marker? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initServiceConnection()
        initView()
        readyTimer()
        bottomViewDown()
        manageRunningState()
        getDistance()
        getTimerNumber()
        getTrashCount()
        trashClickListener()
    }

    override fun onStart() {
        super.onStart()
        backPress()
        val serviceIntent = Intent(requireContext(), RunningService()::class.java)
        requireActivity().bindService(
            serviceIntent,
            runningServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            runningBroadcastReceiver,
            IntentFilter(RunningService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        )
        if (!allGranted()) requireContext().toast(getString(R.string.deny_permission))
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(
            runningBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        if (runningLocationServiceBound) {
            requireActivity().unbindService(runningServiceConnection)
            runningLocationServiceBound = false
        }
        super.onStop()
    }

    override fun onDestroy() {
        runningService?.unsubscribeToLocationUpdates() // service 해제
        super.onDestroy()
    }

    private fun bottomViewDown() {
        parentFragment?.parentFragment?.let {
            ViewModelProvider(it).get(MainViewModel::class.java).showBottomNav.value = false
        }
    }

    private fun initView() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_running) as SupportMapFragment

        // google 맵
        lifecycle.coroutineScope.launchWhenCreated {
            map = mapFragment.awaitMap()
            val latitude = SharedPreference.getLatitude(requireContext()).toDouble()
            val longitude = SharedPreference.getLongitude(requireContext()).toDouble()
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 17f))
        }
    }

    private fun initServiceConnection() {
        runningServiceConnection = object : ServiceConnection { // service와 연결되었는지 상태 확인
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = service as RunningService.LocalBinder
                runningService = binder.service
                runningLocationServiceBound = true
                runningService?.subscribeToLocationUpdates()
            }

            override fun onServiceDisconnected(name: ComponentName) {
                runningService = null
                runningLocationServiceBound = false
            }
        }
    }

    private fun locationToScreen(location: ArrayList<Location>) {
        if (viewModel.runningState.value == RunningViewModel.RunningState.PAUSE) return
        val currentLatLng = LatLng(location.last().latitude, location.last().longitude)
        if (latLngList.isNotEmpty()) { // 거리 구하기
            val lastLatLng = latLngList.last()
            latLngList.clear()
            viewModel.lastDistance.onNext(
                SphericalUtil.computeDistanceBetween(lastLatLng, currentLatLng).toFloat()
            )
        }
        location.map { latLngList.add(LatLng(it.latitude, it.longitude)) }
        //현재 위치 마커 표시
        startMarker = startMarker ?: map.addMarker {
            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start_point))
            position(currentLatLng)
        }
        if (currentMarker != null) currentMarker?.remove()
        currentMarker = map.addMarker {
            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_point))
            position(currentLatLng)
        }
        // 지도 선 그리기
        map.addPolyline {
            color(Color.parseColor("#ff8090"))
            width(20f)
            addAll(latLngList)
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
    }

    // 쓰레기 변경 클릭할 때마다 숫자 변경
    private fun trashClickListener() {
        viewModel.trashTypeList.apply {
            binding.ivTrashVinylPlus.setOnClickListener { value!![0] += 1; onNext(value!!) }
            binding.ivTrashVinylMinus.setOnClickListener {
                if (value!![0] > 0) value!![0] -= 1; onNext(value!!)
            }
            binding.ivTrashGlassPlus.setOnClickListener { value!![1] += 1; onNext(value!!) }
            binding.ivTrashGlassMinus.setOnClickListener {
                if (value!![1] > 0) value!![1] -= 1; onNext(value!!)
            }
            binding.ivTrashPaperPlus.setOnClickListener { value!![2] += 1; onNext(value!!) }
            binding.ivTrashPaperMinus.setOnClickListener {
                if (value!![2] > 0) value!![2] -= 1; onNext(value!!)
            }
            binding.ivTrashPlasticPlus.setOnClickListener { value!![3] += 1; onNext(value!!) }
            binding.ivTrashPlasticMinus.setOnClickListener {
                if (value!![3] > 0) value!![3] -= 1; onNext(value!!)
            }
            binding.ivTrashCanPlus.setOnClickListener { value!![4] += 1; onNext(value!!) }
            binding.ivTrashCanMinus.setOnClickListener {
                if (value!![4] > 0) value!![4] -= 1; onNext(value!!)
            }
            binding.ivTrashExtraPlus.setOnClickListener { value!![5] += 1; onNext(value!!) }
            binding.ivTrashExtraMinus.setOnClickListener {
                if (value!![5] > 0) value!![5] -= 1; onNext(value!!)
            }
        }
    }

    private fun getTrashCount() {
        viewModel.trashTypeList.observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                binding.tvTrashPlasticNum.text = it[3].toString()
                binding.tvTrashVinylNum.text = it[0].toString()
                binding.tvTrashGlassNum.text = it[1].toString()
                binding.tvTrashPaperNum.text = it[2].toString()
                binding.tvTrashExtraNum.text = it[5].toString()
                binding.tvTrashCanNum.text = it[4].toString()
            }, {})
            .addTo(disposables)
    }

    private fun getDistance() {
        viewModel.getDistance()
        viewModel.distanceMeter
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                binding.tvRunningCalorieNum.text = it.meterToCalorie().toString()
                binding.tvRunningDistanceNum.text = it.meterToKilometer()
            }, {})
            .addTo(disposables)
    }

    private fun getTimerNumber() {
        viewModel.runningSeconds
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ binding.tvRunningTimeNum.text = it.toSplitTime() }, {})
            .addTo(disposables)
    }

    // 달리기 상태에 따라 뷰가 바뀜
    private fun manageRunningState() {
        viewModel.runningState.subscribe({ status ->
            when (status) {
                RunningViewModel.RunningState.START -> {
                    activity?.startService(Intent(requireContext(), RunningService::class.java))
                    viewModel.runningTimer()
                    binding.mlRunning.setTransition(
                        R.id.start_count_disappear,
                        R.id.end_count_disappear
                    )
                    binding.mlRunning.transitionToEnd()
                    viewModel.runningState.onNext(RunningViewModel.RunningState.ACTIVE)
                }
                RunningViewModel.RunningState.ACTIVE -> binding.ivRunningPlay.isSelected = false
                RunningViewModel.RunningState.PAUSE -> binding.ivRunningPlay.isSelected = true
                RunningViewModel.RunningState.STOP -> {
                    activity?.stopService(Intent(requireContext(), RunningService::class.java))
                    binding.btnRunningFinish.visibility = VISIBLE
                    binding.ivRunningPlay.visibility = INVISIBLE
                    binding.ivRunningStop.visibility = INVISIBLE
                }
                RunningViewModel.RunningState.INITIAL -> {
                }
            }
        }, {})
            .addTo(disposables)
    }

    // 달리기 시작 전 준비 카운터, motion layout의 에니메이션 적용
    private fun readyTimer() {
        viewModel.readyTimer()
        viewModel.readySeconds.observeOn(AndroidSchedulers.mainThread()).subscribe { second ->
            binding.tvRunningReadyCount.text = second.toString()
            binding.clRunningReady.progress = 0f
            when (second) {
                0 -> binding.tvRunningReadyCount.text = "1"
                1 -> {
                    binding.tvRunningReadyCount.setTextColor(Color.parseColor("#ff697a"))
                    binding.clRunningReady.setTransitionDuration(1200)
                    binding.clRunningReady.transitionToEnd()
                }
                2 -> {
                    binding.tvRunningReadyCount.setTextColor(Color.parseColor("#ffbf00"))
                    binding.clRunningReady.transitionToEnd()
                }
                3 -> {
                    binding.tvRunningReadyCount.setTextColor(Color.parseColor("#37d5ab"))
                    binding.clRunningReady.transitionToEnd()
                }
            }
        }.addTo(disposables)
    }

    override fun clickListener() {
        binding.ivRunningStop.setOnClickListener {
            runningService?.unsubscribeToLocationUpdates()
            viewModel.runningState.onNext(RunningViewModel.RunningState.STOP)
        }
        binding.ivRunningPlay.setOnClickListener {
            if (viewModel.runningState.value == RunningViewModel.RunningState.ACTIVE)
                viewModel.runningState.onNext(RunningViewModel.RunningState.PAUSE)
            else viewModel.runningState.onNext(RunningViewModel.RunningState.ACTIVE)
        }
        binding.btnRunningSaveTrash.setOnClickListener {
            binding.mlRunning.setTransition(
                R.id.start_show_trash_dialog,
                R.id.end_show_trash_dialog
            )
            binding.mlRunning.transitionToEnd()
        }
        binding.btnTrashSave.setOnClickListener {
            binding.mlRunning.transitionToStart()
            binding.tvRunningTrashCount.text = viewModel.trashTypeList.value?.sum().toString()
        }
        binding.btnRunningFinish.setOnClickListener {
            val bundle = bundleOf(
                getString(R.string.distance) to viewModel.distanceMeter.value,
                getString(R.string.trash_type) to viewModel.trashTypeList.value,
                getString(R.string.running_time) to viewModel.runningSeconds.value,
            )
            findNavController().navigate(R.id.action_plogging_running_to_running_finish, bundle)
        }
    }

    private fun allGranted() =
        requireContext().isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) &&
                requireContext().isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)

    //뒤로가기 클릭 시
    private fun backPress() {
        activity?.onBackPressedDispatcher?.addCallback {
            val bundle = bundleOf("stop" to "stop")
            findNavController().navigate(R.id.action_plogging_running_to_running_finish, bundle)
        }
    }

    private inner class RunningBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val locationList = intent.getParcelableArrayListExtra<Location>(
                RunningService.EXTRA_LOCATION
            )
            // DB에 저장
            if (locationList?.isNotEmpty()!!) {
                locationToScreen(locationList)
                SharedPreference.setLatitude(
                    requireContext(),
                    locationList.last().latitude.toFloat()
                )
                SharedPreference.setLongitude(
                    requireContext(),
                    locationList.last().longitude.toFloat()
                )
            }
        }
    }

}