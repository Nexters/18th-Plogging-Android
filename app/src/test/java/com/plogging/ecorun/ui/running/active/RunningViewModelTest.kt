package com.plogging.ecorun.ui.running.active

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.observers.TestObserver
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RunningViewModelTest : TestCase() {
    private lateinit var runningViewModel: RunningViewModel
    private lateinit var testDistanceSubject: TestObserver<Float>
    private lateinit var testTimeSubject: TestObserver<Int>

    @Before
    fun setUpViewModel() {
        runningViewModel = RunningViewModel()
        testDistanceSubject = TestObserver.create()
        testTimeSubject = TestObserver.create()
    }

    @Test
    fun lastDistance_addBaseDistance_returnTotalDistance() {
        runningViewModel.getDistance()
        runningViewModel.distanceMeter.subscribe(testDistanceSubject)
        runningViewModel.distanceMeter.onNext(1f)
        runningViewModel.lastDistance.onNext(1f)
        assertEquals(2f, testDistanceSubject.values().last())
    }

    @Test
    fun startReadyTimer_beforeRunning_returnCount() {
        runningViewModel.readySeconds.subscribe(testTimeSubject)
        runningViewModel.readyTimer()
        Thread.sleep(4000)
        assertEquals(listOf(3, 2, 1, 0), testTimeSubject.values())
        assertEquals(RunningViewModel.RunningState.START, runningViewModel.runningState.value)
    }

    @Test
    fun startRunningTimer_running_returnSecond() {
        // 활동 상태에서 4초가 지나면 5초가 나와야한다. 초기값은 1
        runningViewModel.runningState.onNext(RunningViewModel.RunningState.ACTIVE)
        runningViewModel.runningSeconds.subscribe(testTimeSubject)
        runningViewModel.runningTimer()
        Thread.sleep(4500)
        assertEquals(5, testTimeSubject.values().last())

        // 일시정지 상태에서 4초가 지나면 5초가 나와야한다.
        runningViewModel.runningState.onNext(RunningViewModel.RunningState.PAUSE)
        Thread.sleep(4000)
        assertEquals(5, testTimeSubject.values().last())

        // 활동 상태에서 4초가 지나면 기존 값에 초마다 1씩 더해져 9초가 나와야한다.
        runningViewModel.runningState.onNext(RunningViewModel.RunningState.ACTIVE)
        Thread.sleep(4000)
        assertEquals(9, testTimeSubject.values().last())
    }
}