package ru.avem.stand.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.avem.stand.db.DBManager
import ru.avem.stand.protocol.ProtocolManager
import ru.avem.stand.testitem.TIManager
import ru.avem.stand.tests.Tests.getTestsForDisplay
import ru.avem.stand.tests.Test
import ru.avem.stand.view.composables.EnabledTextButton
import ru.avem.stand.view.composables.MotorType
import ru.avem.stand.view.composables.table.Table
import ru.avem.stand.view.composables.table.TableScheme.Companion.create
import ru.avem.stand.view.composables.table.TableScheme.Companion.named

class ResultScreen(private val selectedTests: List<Test>) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Результаты",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h1
            )
            Table(
                selectedItem = null,
                items = generateResultData(),
                tableScheme = create(
                    columns = listOf(
                        ResultItem::name named "Испытание",
                        ResultItem::result named "Статус",
                    )
                ),
                rowHeight = 48.dp,
                fontSize = 32.sp,
                weights = listOf(0.6f, 0.4f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
                EnabledTextButton("Начальное меню") {
                    navigator.popUntilRoot()
                }
                EnabledTextButton("Сохранить протокол") {
                    ProtocolManager.fillAndSave { DBManager.saveProtocol(it) }
                    navigator.pop()
                    navigator.push(ProtocolsScreen())
                }
            }
        }
    }

    data class ResultItem(val name: MutableState<String>, val result: MutableState<String>)

    private fun generateResultData(): List<ResultItem> {
        return getTestsForDisplay(MotorType.valueOfText(TIManager.testItem.motor)).map {
            ResultItem(
                mutableStateOf(it.toString()),
                mutableStateOf(if (selectedTests.contains(it)) it.result else "Не был выбран")
            )
        }
    }
}
