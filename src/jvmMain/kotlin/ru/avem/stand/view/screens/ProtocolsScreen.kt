package ru.avem.stand.view.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import ru.avem.stand.db.entities.Protocol
import ru.avem.stand.db.entities.ProtocolField
import ru.avem.stand.protocol.ProtocolManager
import ru.avem.stand.tests.Tests
import ru.avem.stand.view.composables.ComboBox
import ru.avem.stand.view.composables.EnabledTextButton
import ru.avem.stand.view.composables.ScrollableLazyColumn
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class ProtocolsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var allProtocols by remember { mutableStateOf(emptyList<Protocol>()) }

        var serials by remember { mutableStateOf(emptyList<String>()) }
        val selectedSerial = remember { mutableStateOf<String?>(null) }

        var filteredProtocols by remember { mutableStateOf(emptyList<Protocol>()) }

        var groupedTestItems = mutableMapOf<String, MutableList<LocalProtocolItem>>()

        if (ProtocolManager.toastText.value.isNotEmpty()) {
            thread {
                sleep(5000)
                ProtocolManager.toastText.value = ""
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Snackbar(modifier = Modifier.padding(8.dp).width(1000.dp).border(4.dp, Color(0xFFFF9F9F))) {
                    Text(
                        text = ProtocolManager.toastText.value,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h3
                    )
                }
            }
        }

        LaunchedEffect(allProtocols.size) {
            launch {
                allProtocols = ProtocolManager.all

                serials = allProtocols.groupBy { it.serial }.keys.toList()
                selectedSerial.value = serials.firstOrNull()

            }
        }

        LaunchedEffect(selectedSerial.value) {
            launch {
                filteredProtocols = allProtocols.filter { it.serial == selectedSerial.value }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text("Выберите заводской номер для просмотра:")
            ComboBox(modifier = Modifier.fillMaxWidth(),
                selectedItem = selectedSerial,
                items = serials,
                onSelect = { serial ->
                    selectedSerial.value = serial
                })
            if (filteredProtocols.isEmpty()) {
                CircularProgressIndicator()
            } else {
                ScrollableLazyColumn(
                    modifier = Modifier.padding(top = 8.dp).height(800.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val groupedProtocolFields = mutableMapOf<String, MutableList<ProtocolField>>()
                    val tags = Tests.getAllTests().map {
                        it.tag
                    }

                    filteredProtocols.flatMap { it.filledFields }.forEach { field ->
                        tags.forEach { tag ->
                            if (field.key.startsWith(tag)) {
                                groupedProtocolFields.getOrPut(tag) { mutableListOf() }.add(field)
                            }
                        }
                    }

                    groupedProtocolFields.forEach { tag, protocolFields ->
                        var listProtocolFields: MutableList<MutableList<ProtocolField>>? = null
                        var idx = -1
                        protocolFields.forEach {
                            if (it.key.contains("Status")) {
                                idx++
                                if (listProtocolFields == null) {
                                    listProtocolFields = mutableListOf(mutableListOf<ProtocolField>())
                                } else {
                                    listProtocolFields!!.add(mutableListOf())
                                }
                            }
                            listProtocolFields!!.get(idx).add(it)
                        }
                        listProtocolFields!!.forEach { local ->
                            groupedTestItems.getOrPut(Tests.getTestNameByTag(tag)) {
                                mutableListOf()
                            }.add(LocalProtocolItem(tag, local))
                            groupedTestItems.forEach { (testName, testItems) ->
                                val listTimes = mutableListOf<Long>()
                                testItems.forEach { testItem ->
                                    listTimes.add(SimpleDateFormat("DD.MM.YYYY-HH:mm").parse("${testItem.date}-${testItem.time}").time)
                                    if (SimpleDateFormat("DD.MM.YYYY-HH:mm").parse("${testItem.date}-${testItem.time}").time == listTimes.max()) {
                                        testItems.forEach {
                                            it.isChecked.value = false
                                        }
                                        testItem.isChecked.value = true
                                    }
                                }
                            }
                        }
                    }

                    groupedTestItems.forEach { t, u ->
                        u.forEach {
                            println(it.protocolFields.map {
                                it.value
                            })
                        }
                    }

                    groupedTestItems.forEach { testName, testItems ->
                        item {
                            Text(text = testName, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            testItems.forEach { testItem ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 16.dp)
                                        .clickable {
                                            testItems.forEach {
                                                it.isChecked.value = false
                                            }
                                            testItem.isChecked.value = !testItem.isChecked.value
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Checkbox(
                                        modifier = Modifier.scale(2f).height(48.dp).width(48.dp),
                                        checked = testItem.isChecked.value,
                                        onCheckedChange = null
                                    )
                                    Text(
                                        text = "Дата: ${testItem.date}", fontSize = 28.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    Text(
                                        text = "Время: ${testItem.time}", fontSize = 28.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    Text(
                                        text = "Результат: ${testItem.status}", fontSize = 28.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    Text(
                                        text = "Оператор: ${testItem.operator}", fontSize = 28.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )

                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
                EnabledTextButton("Начальное меню") {
                    navigator.popUntilRoot()
                }
                EnabledTextButton("Открыть") {
                    ProtocolManager.open(groupedTestItems.values.flatMap { it.filter { it.isChecked.value } }
                        .flatMap { it.protocolFields })
                }
            }
        }
    }
}

class LocalProtocolItem(tag: String, val protocolFields: List<ProtocolField>) {
    val testName = Tests.getTestNameByTag(tag)
    val date = protocolFields.first { it.key.contains("Date") }.value
    val time = protocolFields.first { it.key.contains("Time") }.value
    val operator = protocolFields.first { it.key.contains("Operator") }.value
    val status = protocolFields.first { it.key.contains("Status") }.value
    val isChecked = mutableStateOf(false)
}
