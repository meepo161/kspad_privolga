package ru.avem.stand.view.screens.run

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import ru.avem.stand.tests.Test

class RunModel(tests: List<Test>) : ScreenModel {
    var isAlertMessageDialogVisible = mutableStateOf(false)
    var alertMessageTitle = mutableStateOf("")
    var alertMessageText = mutableStateOf("")

    var isCommandMessageDialogVisible = mutableStateOf(false)
    var commandMessageTitle = mutableStateOf("")
    var commandMessageText = mutableStateOf("")

    private val testIteratorMS = mutableStateOf(tests.iterator())

    val testIterator
        get() = testIteratorMS.value

    private var testMS = mutableStateOf(testIterator.next().also { it.onShow() })

    var test = testMS.value
        get() = testMS.value
        set(value) {
            field = value
            testMS.value = field
        }
}
