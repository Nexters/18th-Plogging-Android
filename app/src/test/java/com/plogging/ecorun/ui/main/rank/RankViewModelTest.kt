package com.plogging.ecorun.ui.main.rank

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plogging.ecorun.FakeRankRepository
import com.plogging.ecorun.data.model.UserRank
import com.plogging.ecorun.util.constant.Constant.WEEKLY
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RankViewModelTest : TestCase() {
    private lateinit var rankRepository: FakeRankRepository
    private lateinit var rankViewModel: RankViewModel

    @Before
    fun setUpViewModel() {
        rankRepository = FakeRankRepository()
        for (i in 1..15) {
            rankRepository.addRankUser(
                UserRank(
                    userId = "user$i@gmail.com:google",
                    rank = i,
                    score = (100 - i).toString(),
                    displayName = "testUser$i",
                    profileImg = "https://static.ebs.co.kr/images/public/lectures/2014/06/19/10/bhpImg/44deb98d-1c50-4073-9bd7-2c2c28d65f9e.jpg"
                )
            )
        }
        rankViewModel = RankViewModel(rankRepository)
    }

    @Test
    fun getMyRankData_intoRankFragment_showMyRankData() {
        val userId = "user1@gmail.com:google"
        rankViewModel.userId.value = userId
        rankViewModel.rankType.value = WEEKLY
        rankViewModel.getMyRanking()
        assertFalse(rankViewModel.isEmptyMyData.value!!)
        assertEquals(rankViewModel.userRankData.value?.userId, userId)
    }

    @Test
    fun getGlobalRankData_intoRankFragment_showGlobalRankData() {
        rankViewModel.rankType.value = WEEKLY
        rankViewModel.getGlobalRanking()
        assertNotNull(rankViewModel.weekRankList.value)
        assertEquals(rankViewModel.weekRankList.value?.size, 10)
    }
}