package ru.avem.stand.view.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.avem.stand.db.DBManager
import ru.avem.stand.testitem.TIManager
import ru.avem.stand.testitem.TIManager.convertToLocalType
import ru.avem.stand.testitem.TIManager.testItemState
import ru.avem.stand.view.composables.AddTestItemDialog
import ru.avem.stand.view.composables.ComboBox
import ru.avem.stand.view.composables.CustomDialog
import ru.avem.stand.view.composables.EnabledTextButton
import ru.avem.stand.view.screens.ProtocolsScreen
import ru.avem.stand.view.screens.TestSelectionScreen

class MainMenuScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = rememberScreenModel { MainViewModel() }

        var isNoSerialDialog by remember { mutableStateOf(false) }

        if (isNoSerialDialog) {
            CustomDialog(
                title = "Внимание",
                text = "Введите заводской номер",
                okCallback = {
                    isNoSerialDialog = false
                },
            )
        }

        if (vm.isCheckFieldsDialogVisible.value) {
            CustomDialog(
                title = "Ошибка",
                text = "Неверно введены данные ОИ",
                yesButton = "Изменить",
                noButton = "Отмена",
                yesCallback = { vm.isCheckFieldsDialogVisible.value = false },
                noCallback = {
                    vm.isCheckFieldsDialogVisible.value = false
                    vm.isNewTIDialogVisible.value = false
                    vm.isEditTIDialogVisible.value = false
                }
            )
        }
        if (vm.isNewTIDialogVisible.value) {
            AddTestItemDialog(null, vm.isNewTIDialogVisible, vm = vm) {
                DBManager.addTI(it)
                vm.testItemsViewModel.add(it)
                testItemState.value = TIManager.all.last()
                vm.isNewTIDialogVisible.value = false
            }
        }
        if (vm.isEditTIDialogVisible.value) {
            AddTestItemDialog(testItemState.value, vm.isEditTIDialogVisible, vm = vm) {
                val newItem = DBManager.replaceTI(testItemState.value, it)
                testItemState.value = newItem.convertToLocalType()
                vm.isEditTIDialogVisible.value = false
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
            EnabledTextButton("Протоколы") {
                navigator.push(ProtocolsScreen())
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text("Выберите тип испытуемого объекта:")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ComboBox(
                    modifier = Modifier.fillMaxWidth(0.72f),
                    selectedItem = testItemState,
                    items = vm.testItemsViewModel
                )
                EnabledTextButton("Добавить", onClick = {
                    vm.isNewTIDialogVisible.value = true
                })
                EnabledTextButton("Редактировать", onClick = {
                    vm.isEditTIDialogVisible.value = true
                })
            }
            Text("Введите заводской номер испытуемого объекта:")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = vm.serialTI.value,
                onValueChange = { vm.serialTI.value = it },
                textStyle = MaterialTheme.typography.h4,
                maxLines = 1
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
                EnabledTextButton("Выбор испытаний") {
                    if (vm.serialTI.value.isNotEmpty()) {
                        navigator.push(TestSelectionScreen())
                    } else {
                        isNoSerialDialog = true
                    }
                }
            }
        }
    }
}
