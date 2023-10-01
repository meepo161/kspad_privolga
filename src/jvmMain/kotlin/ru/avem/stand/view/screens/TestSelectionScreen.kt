package ru.avem.stand.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.avem.stand.testitem.TIManager
import ru.avem.stand.tests.Tests
import ru.avem.stand.tests.Test
import ru.avem.stand.view.composables.EnabledTextButton
import ru.avem.stand.view.composables.MotorType
import ru.avem.stand.view.screens.run.RunScreen

class TestSelectionScreen : Screen {
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val checkedStates by remember { mutableStateOf(mutableMapOf<Test, MutableState<Boolean>>()) }
        var atLeastOneIsChecked by remember { mutableStateOf(false) }

        fun changeCheckedState(test: Test, newState: Boolean): Boolean {
            checkedStates.getOrPut(test) { mutableStateOf(false) }.value = newState
            atLeastOneIsChecked = checkedStates.values.any { it.value }
            return newState
        }

        fun switchCheckedState(test: Test): Boolean {
            return changeCheckedState(test, !checkedStates.getOrPut(test) { mutableStateOf(false) }.value)
        }

        fun getCheckedTests() = checkedStates.entries.filter { it.value.value }.map { it.key }

        var hint by remember { mutableStateOf("") }
        var hintIdx by remember { mutableStateOf(0) }
        var hintCoord by remember { mutableStateOf(Offset.Zero) }

        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "Объект испытания № ${TIManager.serialTI} типа «${TIManager.testItem}»",
                style = MaterialTheme.typography.h3
            )
            Text(text = "Выберите одно или несколько испытаний:", style = MaterialTheme.typography.h1)
            LazyColumn {
                Tests.getTestsForDisplay(MotorType.valueOfText(TIManager.testItem.motor)).forEachIndexed { idx, test ->
                    item {
                        Row(
                            Modifier.clickable {
                                val newState = switchCheckedState(test)
                                test.linkedTests.forEach { changeCheckedState(it, newState) }
                            }.onPointerEvent(eventType = PointerEventType.Move) {
                                hint = test.abbr
                                hintIdx = idx
                                hintCoord = it.changes.first().position
                            }.onPointerEvent(eventType = PointerEventType.Exit) {
                                hint = ""
                                hintIdx = 0
                                hintCoord = Offset.Zero
                            }.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Checkbox(checked = checkedStates.getOrPut(test) { mutableStateOf(false) }.value,
                                onCheckedChange = {
                                    val newState = changeCheckedState(test, it)
                                    test.linkedTests.forEach { changeCheckedState(it, newState) }
                                })
                            Text(text = test.toString(), style = MaterialTheme.typography.h5)
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
                EnabledTextButton(text = "Начальное меню", enabled = !atLeastOneIsChecked) { navigator.pop() }
                EnabledTextButton(text = "Выбрать все") {
                    checkedStates.values.forEach {
                        it.value = true
                    }
                    atLeastOneIsChecked = checkedStates.values.any { it.value }
                }
                EnabledTextButton(text = "Снять все") {
                    checkedStates.values.forEach {
                        it.value = false
                    }
                    atLeastOneIsChecked = checkedStates.values.any { it.value }
                }
                EnabledTextButton(
                    text = "Начать выбранные испытания",
                    enabled = atLeastOneIsChecked
                ) { navigator.push(RunScreen(getCheckedTests())) }
            }
        }
        if (hintCoord.x != 0f && hintCoord.y != 0f) {
            Text(
                text = hint,
                modifier = Modifier.offset((hintCoord.x + 50).dp, (hintCoord.y + 200 + hintIdx * 64).dp),
                style = MaterialTheme.typography.h3
            )
        }
    }
}
