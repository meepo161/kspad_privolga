package ru.avem.stand.view.screens.run

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.avem.stand.tests.Test
import ru.avem.stand.view.composables.CustomDialog
import ru.avem.stand.view.composables.EnabledTextButton
import ru.avem.stand.view.composables.table.Table
import ru.avem.stand.view.screens.ResultScreen

class RunScreen(val tests: List<Test>) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = remember { RunModel(tests) }

        var isBackRequested by remember { mutableStateOf(false) }
        var isToStartRequested by remember { mutableStateOf(false) }

        var isToNextTestRequested by remember { mutableStateOf(false) }
        var isToResultsRequested by remember { mutableStateOf(false) }

        if (isBackRequested) {
            CustomDialog(
                title = "Внимание",
                text = "Вы уверены, что не хотите сохранить протокол?",
                yesButton = "Да",
                noButton = "Нет",
                yesCallback = {
                    isBackRequested = false
                    navigator.pop()
                },
                noCallback = { isBackRequested = false }
            )
        }
        if (isToStartRequested) {
            CustomDialog(
                title = "Внимание",
                text = "Вы уверены, что не хотите сохранить протокол?",
                yesButton = "Да",
                noButton = "Нет",
                yesCallback = {
                    isToStartRequested = false
                    navigator.popUntilRoot()
                },
                noCallback = { isToStartRequested = false }
            )
        }
        if (isToNextTestRequested) {
            CustomDialog(
                title = "Внимание",
                text = "Вы уверены, что хотите перейти к следующему испытанию?",
                yesButton = "Да",
                noButton = "Нет",
                yesCallback = {
                    isToNextTestRequested = false
                    vm.test.onPass()
                    vm.test = vm.testIterator.next().also { it.onShow() }
                },
                noCallback = { isToNextTestRequested = false }
            )
        }
        if (isToResultsRequested) {
            CustomDialog(
                title = "Внимание",
                text = "Вы уверены, что хотите перейти к результатам?",
                yesButton = "Да",
                noButton = "Нет",
                yesCallback = {
                    isToResultsRequested = false
                    vm.test.onPass()
                    navigator.pop()
                    navigator.push(ResultScreen(tests))
                },
                noCallback = { isToResultsRequested = false }
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = vm.test.toString(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h2
            )

            vm.test.tables.forEachIndexed { i, tableScheme ->
                Table(
                    modifier = Modifier,
                    items = vm.test.tablesData[i],
                    tableScheme = vm.test.tables[i],
                    colorHeader = Color(
                        if (i % 2 == 0) 0.200f else 0.350f,
                        if (i % 2 == 0) 0.225f else 0.300f,
                        if (i % 2 == 0) 0.250f else 0.250f
                    ),
                    rowHeight = 75.dp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnabledTextButton(
                    text = "Начальное меню",
                    enabled = !vm.test.isRunning && !vm.test.model.isStopping.value
                ) {
                    if (vm.test.result != "Не запускался") {
                        isToStartRequested = true
                    } else {
                        navigator.popUntilRoot()
                    }
                }
                EnabledTextButton(
                    text = "Выбор испытаний",
                    enabled = !vm.test.isRunning && !vm.test.model.isStopping.value
                ) {
                    if (vm.test.result != "Не запускался") {
                        isBackRequested = true
                    } else {
                        navigator.pop()
                    }
                }
                EnabledTextButton(
                    text = "Нажмите для запуска",
                    enabled = !vm.test.model.isRunning.value && !vm.test.model.isStopping.value,
                    color = Color(0xFF57965C),
                ) { vm.test.switchState() }
                EnabledTextButton(
                    text = "Нажмите для остановки",
                    enabled = !vm.test.model.isFinished.value && !vm.test.model.isStopping.value,
                    color = Color(0xFFEB7171),
                ) { vm.test.switchState() }
                if (vm.testIterator.hasNext()) {
                    EnabledTextButton(
                        text = "Следующее испытание",
                        enabled = !vm.test.isRunning && !vm.test.model.isStopping.value
                    ) {
                        if (vm.test.result == "Не запускался") {
                            isToNextTestRequested = true
                        } else {
                            vm.test.onPass()
                            vm.test = vm.testIterator.next().also { it.onShow() }
                        }
                    }
                } else {
                    EnabledTextButton(
                        text = "Результаты",
                        enabled = !vm.test.isRunning && !vm.test.model.isStopping.value
                    ) {
                        if (vm.test.result == "Не запускался") {
                            isToResultsRequested = true
                        } else {
                            vm.test.onPass()
                            navigator.pop()
                            navigator.push(ResultScreen(tests))
                        }
                    }
                }
            }
        }

        if (vm.test.model.alertNotification.value.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Snackbar(modifier = Modifier.padding(8.dp).width(1000.dp).border(4.dp, Color(0xFFFF9F9F))) {
                    Text(
                        text = vm.test.model.alertNotification.value,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h3
                    )
                }
            }
        }

        if (vm.test.model.alertMessage.value.isNotEmpty()) {
            vm.alertMessageTitle.value = vm.test.model.alertMessage.value.split('|').firstOrNull() ?: ""
            vm.alertMessageText.value = vm.test.model.alertMessage.value.split('|').getOrNull(1) ?: ""
            vm.test.model.alertMessage.value = ""
            vm.isAlertMessageDialogVisible.value = true
        }

        if (vm.isAlertMessageDialogVisible.value) {
            CustomDialog(
                modifier = Modifier.width(1000.dp),
                title = vm.alertMessageTitle.value,
                text = vm.alertMessageText.value.split(" / ").reduce { acc, s -> "$acc\n$s" },
                okCallback = {
                    vm.isAlertMessageDialogVisible.value = false
                },
            )
        }

        if (vm.test.model.commandMessage.value.isNotEmpty()) {
            vm.commandMessageTitle.value = vm.test.model.commandMessage.value.split('|').firstOrNull() ?: ""
            vm.commandMessageText.value = vm.test.model.commandMessage.value.split('|').getOrNull(1) ?: ""
            vm.test.model.commandMessage.value = ""
            vm.isCommandMessageDialogVisible.value = true
            vm.test.model.isCommandMessageDialogVisible = true
        }

        if (vm.isCommandMessageDialogVisible.value) {
            CustomDialog(
                modifier = Modifier.width(1000.dp),
                title = vm.commandMessageTitle.value,
                text = vm.commandMessageText.value.split(" / ").reduce { acc, s -> "$acc\n$s" },
                yesButton = "ОК",
                noButton = "Отмена",
                yesCallback = {
                    vm.isCommandMessageDialogVisible.value = false
                    vm.test.model.isCommandMessageDialogVisible = false
                },
                noCallback = {
                    vm.isCommandMessageDialogVisible.value = false
                    vm.test.model.isCommandMessageDialogVisible = false
                    vm.test.cancel()
                },
            )
        }
    }
}
