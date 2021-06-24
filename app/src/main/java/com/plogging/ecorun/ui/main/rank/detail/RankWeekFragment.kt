package com.plogging.ecorun.ui.main.rank.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.databinding.FragmentRankWeekBinding
import com.plogging.ecorun.ui.main.rank.RankViewModel
import com.plogging.ecorun.util.extension.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankWeekFragment : BaseFragment<FragmentRankWeekBinding, RankViewModel>() {
    override fun getViewBinding() = FragmentRankWeekBinding.inflate(layoutInflater)
    override val viewModel: RankViewModel by viewModels()
    private lateinit var rankViewModel: RankViewModel
    private val rankRecyclerAdapter by lazy { RankRecyclerAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSharedViewModel()
        refreshRanking()
        initEmptyView()
        responseApi()
    }

    private fun initSharedViewModel() {
        parentFragment?.let {
            rankViewModel = ViewModelProvider(it).get(RankViewModel::class.java)
        }
    }

    private fun refreshRanking() {
        binding.slRankListWeek.setOnRefreshListener {
            rankViewModel.getGlobalRanking()
        }
    }

    private fun responseApi() {
        rankViewModel.weekRankList.observe(viewLifecycleOwner) {
            binding.slRankListWeek.isRefreshing = false
            rankRecyclerAdapter.submitList(it)
        }

        rankViewModel.isSignIn.observe(viewLifecycleOwner) {
            if (it && !rankViewModel.isRequestGlobalRanking.value!!) rankViewModel.getGlobalRanking()
        }
    }

    private fun initEmptyView() {
        binding.rvRankListWeek.adapter = rankRecyclerAdapter
        rankViewModel.isEmptyWeeklyList.observe(viewLifecycleOwner) {
            if (it) requireContext().toast(getString(R.string.no_weekly_data))
            binding.slRankListWeek.isRefreshing = false
            binding.rvRankListWeek.isVisible = !it
        }
    }

    override fun clickListener() {}
}