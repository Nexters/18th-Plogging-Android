package com.plogging.ecorun.ui.plogging

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.data.model.MyDatabasePlogging
import com.plogging.ecorun.data.model.Trash
import com.plogging.ecorun.databinding.FragmentDetailPloggingBinding
import com.plogging.ecorun.ui.main.MainViewModel
import com.plogging.ecorun.ui.running.save.TrashRecyclerAdapter
import com.plogging.ecorun.util.extension.*
import dagger.hilt.android.AndroidEntryPoint
import java.net.URL

@AndroidEntryPoint
class UserDetailPloggingFragment :
    BaseFragment<FragmentDetailPloggingBinding, UserDetailPloggingViewModel>() {
    private val plogging by lazy { arguments?.getSerializable("ploggingData") as MyDatabasePlogging }
    override fun getViewBinding() = FragmentDetailPloggingBinding.inflate(layoutInflater)
    override val viewModel: UserDetailPloggingViewModel by viewModels()
    private val adapter by lazy { TrashRecyclerAdapter() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomViewDown()
        initRecycler(plogging.trashList)
        responseApi()
        initView()
    }

    private fun bottomViewDown() {
        parentFragment?.parentFragment?.let {
            ViewModelProvider(it).get(MainViewModel::class.java).showBottomNav.value = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val totalTrashCount = plogging.trashList.sumOf { it.pickCount }.toString()
        val time = plogging.createdTime
        binding.tvPloggingDetailName.text = SharedPreference.getUserName(requireContext())
        binding.tvPloggingDetailTotalTrashTitle.text = "총 ${totalTrashCount}개의 쓰레기를 주웠어요!"
        binding.tvPloggingDetailDate.text =
            time.substring(0, 4) + "." + time.substring(4, 6) + "." + time.substring(6, 8)
        binding.tvPloggingDetailScoreNumber.text = plogging.ploggingTotalScore.toString()
        binding.tvPloggingDetailTimeNumber.text = plogging.ploggingTime.toSplitTime()
        binding.tvPloggingDetailTrashTotalNumber.text = totalTrashCount
        binding.tvPloggingDetailDistanceNumber.text =
            plogging.distance.toFloat().meterToKilometer()
        binding.ivPloggingDetailUserProfile.setImageURI(
            SharedPreference.getUserImage(requireContext())
        )
        Glide.with(requireContext()).load(plogging.ploggingImg).into(binding.ivPloggingDetail)
    }

    private fun responseApi() {
        viewModel.responseCode.observe(viewLifecycleOwner) {
            if (it == 200) findNavController().popBackStack(R.id.nav_user, false)
        }
    }

    private fun initRecycler(trashList: List<Trash>) {
        adapter.submitList(
            trashList.map { value ->
                Trash(trashType = value.trashType - 1, pickCount = value.pickCount)
            })
        binding.rvPloggingDetailTrash.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvPloggingDetailTrash.adapter = adapter
    }

    override fun clickListener() {
        binding.ivPloggingDetailUserBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnPloggingDetailSaving.setOnClickListener { sharedPloggingImage() }
        binding.tvPloggingDetailDelete.setOnClickListener {
            val bundle =
                bundleOf(
                    getString(R.string.plogging_id) to plogging.id,
                    getString(R.string.plogging_img_name) to plogging.ploggingImg
                )
            findNavController().navigate(R.id.action_user_detail_delete_dialog, bundle)
        }
    }

    private fun sharedPloggingImage() {
        Intent().apply {
            action = Intent.ACTION_SEND
            URL(plogging.ploggingImg)
                .toBitmap()
                .flatMap { it.saveImageIn(requireContext().contentResolver) }
                .composeSchedulers()
                .subscribe({
                    putExtra(Intent.EXTRA_STREAM, it)
                    startActivity(Intent.createChooser(this, "에코런 입니다."))
                }, {})
            type = "image/jpeg"
        }
    }
}