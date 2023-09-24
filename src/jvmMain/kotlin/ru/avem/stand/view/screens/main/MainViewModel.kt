package ru.avem.stand.view.screens.main

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import ru.avem.stand.testitem.TIManager

class MainViewModel : ScreenModel {
    var serialTI = TIManager.serialTIState

    val isNewTIDialogVisible = mutableStateOf(false)
    val isCheckFieldsDialogVisible = mutableStateOf(false)
    val isEditTIDialogVisible = mutableStateOf(false)

    val testItemsViewModel = mutableStateListOf(*TIManager.all.toTypedArray())
}
