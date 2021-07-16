package com.plogging.ecorun.ui.running.save

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.data.model.Trash
import com.plogging.ecorun.databinding.FragmentSaveBinding
import com.plogging.ecorun.ui.auth.MainActivity
import com.plogging.ecorun.ui.main.MainViewModel
import com.plogging.ecorun.util.extension.meterToCalorie
import com.plogging.ecorun.util.extension.meterToKilometer
import com.plogging.ecorun.util.extension.toSplitTime
import com.plogging.ecorun.util.extension.uriToRequestBody
import com.plogging.ecorun.util.glide.GlideApp
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

@AndroidEntryPoint
class SaveFragment : BaseFragment<FragmentSaveBinding, SaveViewModel>() {
    override fun getViewBinding() = FragmentSaveBinding.inflate(layoutInflater)
    private val adapter by lazy { TrashRecyclerAdapter() }
    override val viewModel: SaveViewModel by viewModels()
    private lateinit var mainViewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bottomViewDown()
        calculateScore()
        backPress()
        initImage()
        initRecycler()
        responseApi()
    }

    private fun bottomViewDown() {
        parentFragment?.parentFragment?.let {
            ViewModelProvider(it).get(MainViewModel::class.java).showBottomNav.value = false
        }
    }

    private fun initRecycler() {
        val list = mutableListOf<Trash>()
        viewModel.trashList.value
            ?.mapIndexed { idx, i -> if (i != 0) list.add(Trash(trashType = idx, pickCount = i)) }
        adapter.submitList(list)
        binding.rvSaveTrash.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvSaveTrash.adapter = adapter
    }

    private fun initImage() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Uri>("uri")
            ?.observe(viewLifecycleOwner) {
                GlideApp.with(requireContext()).load(it).into(binding.ivSavedPhoto)
                viewModel.uri = it
            }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        //bottom bar 숨기기
        parentFragment?.parentFragment?.let {
            mainViewModel = ViewModelProvider(it).get(MainViewModel::class.java)
            mainViewModel.showBottomNav.value = false
        }
        //서버에 meter로 사용자에게 kilo meter로 보여준다.
        binding.tvSaveDistanceNumber.text =
            (arguments?.get(getString(R.string.distance)) as Float).meterToKilometer()
        binding.tvSaveTimeNumber.text =
            (arguments?.get(getString(R.string.running_time)) as Int).toSplitTime()
        if (arguments?.get(getString(R.string.distance)) == 0.0) viewModel.distance.value = 1.00f
        else viewModel.distance.value = arguments?.get(getString(R.string.distance)) as Float
        viewModel.trashList.value = (arguments?.get(getString(R.string.trash_type)) as IntArray)
        viewModel.runningTime.value = arguments?.get(getString(R.string.running_time)) as Int
        binding.tvSaveCalorieNumber.text = viewModel.distance.value?.meterToCalorie().toString()
        binding.tvSaveTrashTitle.text = "총 ${viewModel.trashList.value!!.sum()}개의 쓰레기를 주웠어요!"
        binding.tvSaveTrashTotalNumber.text = viewModel.trashList.value!!.sum().toString()
        viewModel.calorie.value = viewModel.distance.value?.meterToCalorie()
    }

    private fun responseApi() {
        viewModel.responseCode.observe(viewLifecycleOwner) {
            if (it == 201) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                parentFragment?.activity?.finish()
            }
        }
    }

    private fun calculateScore() {
        viewModel.calculateScore()
        viewModel.score.observe(viewLifecycleOwner) {
            binding.tvSaveWorkOutScore.text = it.activityScore.toString()
            binding.tvSaveEcoScore.text = it.environmentScore.toString()
        }
    }

    override fun clickListener() {
        binding.tvSaveScoreQuestion.setOnClickListener { findNavController().navigate(R.id.action_save_to_dialog_score) }
        binding.btnSaveDelete.setOnClickListener { findNavController().navigate(R.id.action_save_to_dialog_delete) }
        binding.btnSaveSaving.setOnClickListener {
            if (viewModel.uri == null) {
                findNavController().navigate(R.id.action_save_to_dialog_no_image, setBundle())
            } else setImageBody()
        }
        binding.vSavePicture.setOnClickListener {
            findNavController().navigate(R.id.action_save_to_camera, setBundle())
        }
    }

    private fun setBundle(): Bundle = bundleOf(
        getString(R.string.distance) to viewModel.distance.value,
        getString(R.string.trash_num) to viewModel.trashList.value?.sum()
    )

    private fun setImageBody() {
        uriToRequestBody(
            viewModel.uri,
            requireContext().contentResolver,
            getString(R.string.plogging_param)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                viewModel.imageBody = it
                viewModel.savePlogging()
            }, {})
            .addTo(disposables)
    }

    //뒤로가기 클릭 시
    private fun backPress() {
        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(R.id.action_save_to_dialog_delete)
        }
    }
}