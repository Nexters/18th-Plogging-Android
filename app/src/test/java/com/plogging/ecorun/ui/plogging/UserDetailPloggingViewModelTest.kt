package com.plogging.ecorun.ui.plogging

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plogging.ecorun.FakePloggingRepository
import com.plogging.ecorun.data.model.Plogging
import com.plogging.ecorun.data.model.Trash
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.plogging.ecorun.data.model.PloggingDetail as PloggingDetail1

@RunWith(AndroidJUnit4::class)
class UserDetailPloggingViewModelTest : TestCase() {
    private lateinit var userDetailPloggingViewModel: UserDetailPloggingViewModel
    private lateinit var ploggingRepository: FakePloggingRepository

    @Before
    fun setUpViewModel() {
        ploggingRepository = FakePloggingRepository()
        val ploggingDetail = PloggingDetail1(
            calories = 1000,
            createdTime = "12",
            distance = 120,
            ploggingActivityScore = 100,
            ploggingEnvironmentScore = 80,
            ploggingImg = "https://image.shutterstock.com/image-vector/check-back-soon-hand-lettering-600w-1379832464.jpg",
            ploggingTime = 12,
            ploggingTotalScore = 12,
            userId = "ploggingteam@gmail.com:google"
        )
        val trashList = listOf(
            Trash(9, 0),
            Trash(93, 1),
            Trash(12, 2),
            Trash(4, 3),
            Trash(0, 4),
            Trash(44, 5),
        )
        val plogging = Plogging(
            id = "12341234",
            ploggingDetail = ploggingDetail,
            trashList = trashList
        )
        ploggingRepository.addPlogging(plogging)
        userDetailPloggingViewModel = UserDetailPloggingViewModel(ploggingRepository)
    }

    @Test
    fun ploggingId_deletePlogging_returnResult() {
        userDetailPloggingViewModel.ploggingId.value = null
        userDetailPloggingViewModel.ploggingImageName.value = null
        userDetailPloggingViewModel.deleteMyPlogging()
        assertNull(userDetailPloggingViewModel.responseCode.value)

        userDetailPloggingViewModel.ploggingId.value = "12341234"
        userDetailPloggingViewModel.ploggingImageName.value = "ploggingImage"
        userDetailPloggingViewModel.deleteMyPlogging()
        assertEquals(200, userDetailPloggingViewModel.responseCode.value)

        userDetailPloggingViewModel.ploggingId.value = "errorId"
        userDetailPloggingViewModel.ploggingImageName.value = "ploggingImage"
        userDetailPloggingViewModel.deleteMyPlogging()
        assertEquals(500, userDetailPloggingViewModel.responseCode.value)
    }
}