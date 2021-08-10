package com.plogging.ecorun.ui.running.save

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.plogging.ecorun.base.BaseViewModel
import com.plogging.ecorun.data.model.Score
import com.plogging.ecorun.data.model.SendPlogging
import com.plogging.ecorun.data.model.SendPloggingDetail
import com.plogging.ecorun.data.model.Trash
import com.plogging.ecorun.data.repository.plogging.PloggingRepository
import com.plogging.ecorun.data.response.BaseResponse
import com.plogging.ecorun.data.response.ScoreResponse
import com.plogging.ecorun.util.observer.DefaultSingleObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class SaveViewModel @Inject constructor(private val repository: PloggingRepository) :
    BaseViewModel() {
    private lateinit var sendPloggingData: SendPlogging
    val trashList = MutableLiveData<IntArray>()
    var imageBody: MultipartBody.Part? = null
    val responseCode = MutableLiveData<Int>()
    val distance = MutableLiveData<Float>()
    val runningTime = MutableLiveData<Int>()
    val calorie = MutableLiveData<Double>()
    val score = MutableLiveData<Score>()
    val trash = mutableListOf<Trash>()
    var uri: Uri? = null

    fun calculateScore() {
        calorie.value ?: return
        distance.value ?: return
        runningTime.value ?: return
        if (calorie.value!! < 1.0) calorie.value = 1.0
        if (distance.value!! < 1.0) distance.value = 1.0f
        val ploggingMetaData = SendPloggingDetail(
            calorie.value?.toInt()!!,
            distance.value!!.toInt(),
            runningTime.value!!
        )
        trash.clear()
        trashList.value?.mapIndexed { index, i ->
            if (i != 0) trash.add(Trash(trashType = index + 1, pickCount = i))
        }
        sendPloggingData = SendPlogging(ploggingMetaData, trash)
        repository.getScore(sendPloggingData)
            .subscribe(object : DefaultSingleObserver<ScoreResponse>() {
                override fun onSuccess(response: ScoreResponse) {
                    score.value = response.score
                }
            })
    }

    fun savePlogging() {
        calorie.value ?: return
        distance.value ?: return
        runningTime.value ?: return
        imageBody ?: return
        val ploggingMetaData = SendPloggingDetail(
            calorie.value!!.toInt(),
            distance.value!!.toInt(),
            runningTime.value!!
        )
        sendPloggingData = SendPlogging(ploggingMetaData, trash)
        repository.savePlogging(imageBody!!, sendPloggingData)
            .subscribe(object : DefaultSingleObserver<BaseResponse>() {
                override fun onSuccess(response: BaseResponse) {
                    responseCode.value = response.rc + 1
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.e("error", "${e.stackTraceToString()}")
                }
            })
    }
}