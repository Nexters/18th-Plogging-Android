package com.plogging.ecorun.ui.main.rank

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.plogging.ecorun.ui.main.rank.detail.RankMonthFragment
import com.plogging.ecorun.ui.main.rank.detail.RankWeekFragment
import com.plogging.ecorun.util.constant.Constant.MONTHLY
import com.plogging.ecorun.util.constant.Constant.WEEKLY

class RankViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val viewPagerList = arrayListOf(WEEKLY, MONTHLY)

    override fun getItemCount() = viewPagerList.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RankWeekFragment()
            else -> RankMonthFragment()
        }
    }
}