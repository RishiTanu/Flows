package com.example.flows.viewModels

import app.cash.turbine.test
import com.example.flows.TestDispatchers
import kotlinx.coroutines.runBlocking
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var testDispatchers: TestDispatchers

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(testDispatchers)
        testDispatchers = TestDispatchers()
    }

    @Test
    fun `countDownFlow, properly counts down from 5 to 0`() = runBlocking {

        mainViewModel.countDownTimer.test {

            for (i in 5 downTo 0) {
                testDispatchers.testDispatcher.scheduler.apply { advanceTimeBy(1000L); runCurrent() }
                val emission = awaitItem()
                assertThat(emission).isEqualTo(i)
            }
            cancelAndConsumeRemainingEvents()
        }
    }
}