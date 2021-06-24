package com.plogging.ecorun.ui.main.rank.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.databinding.FragmentRankMonthBinding
import com.plogging.ecorun.ui.main.rank.RankViewModel
import com.plogging.ecorun.util.extension.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankMonthFragment : BaseFragment<FragmentRankMonthBinding, RankViewModel>() {
    override fun getViewBinding() = FragmentRankMonthBinding.inflate(layoutInflater)
    private val rankRecyclerAdapter by lazy { RankRecyclerAdapter() }
    override val viewModel: RankViewModel by viewModels()
    private lateinit var rankViewModel: RankViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSharedViewModel()
        refreshRanking()
        initEmptyView()
        responseApi()
    }

    private fun initSharedViewModel() {
        parentFragment?.let { rankViewModel = ViewModelProvider(it).get(RankViewModel::class.java) }
    }

    private fun refreshRanking() {
        binding.slRankList.setOnRefreshListener { rankViewModel.getGlobalRanking() }
    }

    private fun responseApi() {
        rankViewModel.monthRankList.observe(viewLifecycleOwner) {
            binding.slRankList.isRefreshing = false
            rankRecyclerAdapter.submitList(it)
        }
        rankViewModel.isSignIn.observe(viewLifecycleOwner) {
            if (it && !rankViewModel.isRequestGlobalRanking.value!!)
                rankViewModel.getGlobalRanking()
        }
    }

    private fun initEmptyView() {
        binding.rvRankList.adapter = rankRecyclerAdapter
        rankViewModel.isEmptyMonthlyList.observe(viewLifecycleOwner) {
            if (it) requireContext().toast(getString(R.string.no_monthly_data))
            binding.slRankList.isRefreshing = false
            binding.rvRankList.isVisible = !it
        }
    }

    override fun clickListener() {}
}