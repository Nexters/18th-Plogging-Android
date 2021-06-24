package com.plogging.ecorun.ui.main.onboarding

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.plogging.ecorun.R
import com.plogging.ecorun.base.BaseFragment
import com.plogging.ecorun.data.local.SharedPreference
import com.plogging.ecorun.data.model.OnBoardView
import com.plogging.ecorun.databinding.FragmentOnBoardingBinding

class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding, OnBoardingViewModel>() {
    override fun getViewBinding() = FragmentOnBoardingBinding.inflate(layoutInflater)
    private lateinit var pageChangeCallback: ViewPager2.OnPageChangeCallback
    private val pageAdapter by lazy { OnBoardingViewPagerAdapter() }
    override val viewModel: OnBoardingViewModel by viewModels()
    private lateinit var imageArray: List<OnBoardView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPreference.setFirstUsed(requireContext())
        initImageList()
        initListAdapter()
        initViewPage()
    }

    private fun initImageList() {
        imageArray = listOf(
            OnBoardView(
                R.string.on_board_title_0,
                R.string.on_board_sub_0,
                R.drawable.on_board_0
            ),
            OnBoardView(
                R.string.on_board_title_1,
                R.string.on_board_sub_1,
                R.drawable.on_board_1
            ),
            OnBoardView(
                R.string.on_board_title_2,
                R.string.on_board_sub_2,
                R.drawable.on_board_2
            )
        )
    }

    private fun initViewPage() {
        pageChangeCallback = object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        binding.ivOnBoardDotFirst.setImageResource(R.drawable.rectangle_green)
                        binding.ivOnBoardDotSecond.setImageResource(R.drawable.oval_light_green)
                        binding.ivOnBoardDotThird.setImageResource(R.drawable.oval_light_green)
                    }
                    1 -> {
                        binding.ivOnBoardDotFirst.setImageResource(R.drawable.oval_light_green)
                        binding.ivOnBoardDotSecond.setImageResource(R.drawable.rectangle_green)
                        binding.ivOnBoardDotThird.setImageResource(R.drawable.oval_light_green)
                        binding.tvMainOnBoard.setTextColor((Color.parseColor("#898989")))
                        binding.tvMainOnBoard.setText(R.string.next_page)
                        binding.tvMainOnBoard.isSelected = false
                    }
                    2 -> {
                        binding.ivOnBoardDotFirst.setImageResource(R.drawable.oval_light_green)
                        binding.ivOnBoardDotSecond.setImageResource(R.drawable.oval_light_green)
                        binding.ivOnBoardDotThird.setImageResource(R.drawable.rectangle_green)
                        binding.tvMainOnBoard.setTextColor(Color.WHITE)
                        binding.tvMainOnBoard.setText(R.string.confirm)
                        binding.tvMainOnBoard.isSelected = true
                    }
                }
            }
        }
        binding.pagerMainOnBoard.registerOnPageChangeCallback(pageChangeCallback)
    }

    private fun initListAdapter() {
        binding.pagerMainOnBoard.adapter = pageAdapter
        pageAdapter.submitList(imageArray)
    }

    override fun clickListener() {
        binding.tvMainOnBoard.setOnClickListener {
            if (binding.pagerMainOnBoard.currentItem == 2)
                findNavController().navigate(R.id.action_on_boarding_to_main)
            binding.pagerMainOnBoard.currentItem = binding.pagerMainOnBoard.currentItem + 1
        }
    }
}