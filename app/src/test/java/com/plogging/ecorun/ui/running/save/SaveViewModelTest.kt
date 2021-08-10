package com.plogging.ecorun.ui.running.save

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plogging.ecorun.FakePloggingRepository
import com.plogging.ecorun.data.model.Trash
import junit.framework.TestCase
import okhttp3.MultipartBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SaveViewModelTest : TestCase() {
    private lateinit var saveViewModel: SaveViewModel
    private lateinit var ploggingRepository: FakePloggingRepository

    @Before
    fun setUpViewModel() {
        ploggingRepository = FakePloggingRepository()
        saveViewModel = SaveViewModel(ploggingRepository)
        saveViewModel.calorie.value = 2000.toDouble()
        saveViewModel.distance.value = 300f
        saveViewModel.runningTime.value = 3600
        saveViewModel.trashList.value = intArrayOf(33, 20, 10, 9, 0, 0)
        saveViewModel.trashList.value?.mapIndexed { index, i ->
            if (i != 0) saveViewModel.trash.add(Trash(trashType = index + 1, pickCount = i))
        }
        saveViewModel.imageBody =
            MultipartBody.Part.createFormData("parameterName", "Image")
    }

    @Test
    fun runningData_calculate_returnScore() {
        saveViewModel.calculateScore()
        saveViewModel.score.value?.activityScore
        assertEquals(72, saveViewModel.score.value?.environmentScore)
        assertEquals(5900, saveViewModel.score.value?.activityScore)
        assertEquals(5972, saveViewModel.score.value?.totalScore)
    }

    @Test
    fun runningData_save_returnResponseCode() {
        saveViewModel.savePlogging()
        assertEquals(201, saveViewModel.responseCode.value)
    }
}