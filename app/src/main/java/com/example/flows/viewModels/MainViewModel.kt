package com.example.flows.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val countDownTimer = flow<Int> {
        val startingValue = 10
        var currentValue = startingValue

        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }

    init {
        viewModelScope.launch {
            sharedFlow.collect {
                delay(2000L)
                println("FIRST FLOW The received number is $it")
            }
        }
        viewModelScope.launch {
            sharedFlow.collect {
                delay(3000L)
                println("SECOND FLOW The received number is $it")
            }
        }
        squareNumber(3)
    }

    private fun collectFlow() {
        countDownTimer.onEach { timer ->
            println("ONEACH=== $timer")
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            countDownTimer.filter { time ->
                time % 2 == 0
            }
                .map { time ->
                    time * time
                }.onEach { timer ->
                    println("Time $timer")
                }
                .collect { timer ->
                    delay(1500L)
                    println("Current time $timer")
                }

            countDownTimer.collectLatest { timer ->
                delay(1500L)
                println("Current time $timer")
            }
        }
    }

    private fun countFlow() {
        viewModelScope.launch {
            val count = countDownTimer.count {
                it % 2 == 0
            }
            println("Current time $count")
        }
    }

    private fun reduceFlow() {
        viewModelScope.launch {
            val accum = countDownTimer.reduce { accumalator, value ->
                accumalator + value
            }
            println("Current time $accum")
        }
    }

    private fun foldFlow() {
        viewModelScope.launch {
            val accum = countDownTimer
                .fold(100) { accumalator, value ->
                    accumalator + value
                }
            println("Current time $accum")
        }
    }

    private fun flatMapConcat() {
        /*val flow1 = (1..5).asFlow()
        viewModelScope.launch {
            flow1.flatMapConcat { id->
            flow1.flatMapMerge { id->
            flow1.flatMapLatest { id->
                //getRecipeById(id)
            }.collect{ value->
                println("Current time $value")
            }
        }*/
    }

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()
    fun incrementCounter() {
        _stateFlow.value += 1
    }


    private val _sharedFlow = MutableSharedFlow<Int>(0)
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun squareNumber(number: Int) {
        viewModelScope.launch {
            _sharedFlow.emit(number * number)
        }
    }
}