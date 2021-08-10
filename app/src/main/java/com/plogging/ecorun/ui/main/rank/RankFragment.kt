package com.plogging.ecorun.ui.main.rank

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.databinding.FragmentRankBinding
import com.plogging.ecorun.ui.main.MainViewModel
import com.plogging.ecorun.util.constant.Constant.MONTHLY
import com.plogging.ecorun.util.constant.Constant.WEEKLY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankFragment : BaseFragment<FragmentRankBinding, RankViewModel>() {
    override fun getViewBinding(): FragmentRankBinding = FragmentRankBinding.inflate(layoutInflater)
    private val rankViewPagerAdapter by lazy { RankViewPagerAdapter(this@RankFragment) }
    override val viewModel: RankViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSharedViewModel()
        initTitle()
        initMyRanking()
        backPress()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        responseApi()
        initEmptyView()
    }

    override fun onResume() {
        super.onResume()
        initBottomView()
        initViewPager()
    }

    @SuppressLint("SetTextI18n")
    private fun initTitle() {
        val name = SharedPreference.getUserName(requireContext())
        val text = "<b>${name}</b>님의<br> 랭킹을 확인하세요!"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvRankTitle.text = Html.fromHtml(text, 0)
        } else binding.tvRankTitle.text = "${name}님의\n 랭킹을 확인하세요"
    }

    private fun initEmptyView() {
        viewModel.isEmptyMyData.observe(viewLifecycleOwner) {
            if (it) {
                binding.tvRankMyRank.text = "_ 위"
                binding.tvRankMyScore.text = "0 점"
            }
        }
    }

    private fun initMyRanking() {
        viewModel.userId.value = SharedPreference.getUserEmail(requireContext())
        viewModel.getGlobalRanking()
        viewModel.getMyRanking()
        binding.vpRank.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            var beforeOffset = 0f
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (beforeOffset < 0.5f && positionOffset > 0.5f) {
                    viewModel.rankType.value = MONTHLY
                    viewModel.getGlobalRanking()
                    viewModel.getMyRanking()
                } else if (beforeOffset > 0.5f && positionOffset < 0.5f && positionOffset > 0f) {
                    viewModel.rankType.value = WEEKLY
                    viewModel.getGlobalRanking()
                    viewModel.getMyRanking()
                }
                beforeOffset = positionOffset
            }
        })
    }

    private fun initViewPager() {
        // 뒤로 되돌아 올때 viewpager 상태 보존되지 않아야 다시 되돌아 올 수 있다.
        binding.vpRank.isSaveEnabled = false
        binding.vpRank.adapter = rankViewPagerAdapter
        TabLayoutMediator(binding.tabRank, binding.vpRank) { tab, position ->
            tab.text = resources.getStringArray(R.array.rank)[position]
        }.attach()
    }

    private fun initBottomView() {
        binding.ivRankMyProfile.setImageURI(SharedPreference.getUserImage(requireContext()))
    }

    private fun initSharedViewModel() {
        parentFragment?.parentFragment?.let {
            ViewModelProvider(it).get(MainViewModel::class.java).showBottomNav.value = true
        }
    }

    private fun stateViewPager(position: Int) {
        when (position) {
            0 -> viewModel.rankType.value = WEEKLY
            1 -> viewModel.rankType.value = MONTHLY
        }
        viewModel.getMyRanking()
    }

    @SuppressLint("SetTextI18n")
    private fun responseApi() {
        viewModel.userRankData.observe(viewLifecycleOwner) {
            binding.tvRankMyRank.text = (it.rank + 1).toString() + "위"
            binding.tvRankMyScore.text = it.score + "점"
        }
    }

    override fun clickListener() {
        binding.ivRankQuestion.setOnClickListener {
            findNavController().navigate(R.id.action_rank_to_score_dialog)
        }
    }

    // 뒤로가기 클릭 시 라이프 사이클을 해당 프레그먼트에만 뒤로가기를 설정한다.
    private fun backPress() {
        activity?.onBackPressedDispatcher?.addCallback(this) { activity?.finish() }
    }
}