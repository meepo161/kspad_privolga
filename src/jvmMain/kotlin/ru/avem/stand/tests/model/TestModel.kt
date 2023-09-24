package ru.avem.stand.tests.model

import androidx.compose.runtime.mutableStateOf
import mu.KotlinLogging

class TestModel {
    val alertNotification = mutableStateOf("")
    val alertMessage = mutableStateOf("")

    val commandMessage = mutableStateOf("")
    var isCommandMessageDialogVisible = false

    val isRunning = mutableStateOf(false)
    val isStopping = mutableStateOf(false)
    val isFinished = mutableStateOf(true)

    private val log = mutableListOf<Pair<String, Boolean>>()

    fun setRunningState() {
        isRunning.value = true
        isStopping.value = false
        isFinished.value = false
    }

    fun setStoppingState() {
        isRunning.value = false
        isStopping.value = true
    }

    fun setFinishedState() {
        isRunning.value = false
        isStopping.value = false
        isFinished.value = true
    }

    fun addMessageToLog(message: String, isError: Boolean = false) {
        log += message to isError
        if (isError) {
            KotlinLogging.logger("TEST").error(message)
        } else {
            KotlinLogging.logger("TEST").info(message)
        }
    }
}
