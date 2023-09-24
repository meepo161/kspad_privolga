package ru.avem.stand.view.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import ru.avem.stand.db.entities.Protocol
import ru.avem.stand.protocol.ProtocolManager
import ru.avem.stand.view.composables.ComboBox
import ru.avem.stand.view.composables.EnabledTextButton
import ru.avem.stand.view.composables.ScrollableLazyColumn
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class ProtocolsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var allProtocols by remember { mutableStateOf(emptyList<Protocol>()) }

        var serials by remember { mutableStateOf(emptyList<String>()) }
        val selectedSerial = remember { mutableStateOf<String?>(null) }

        var filteredProtocols by remember { mutableStateOf(emptyList<Protocol>()) }

        val protocolIdsForSaving = remember { mutableStateMapOf<Int, MutableState<Boolean>>() }

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
            launch { filteredProtocols = allProtocols.filter { it.serial == selectedSerial.value } }
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
                    item {
                        filteredProtocols
                            .flatMap { it.filledFields }
                            .filter { it.key == "TEST_NAME" }
                            .map { it.value }
                            .toSet()
                            .forEach { groupName ->
                                Text(text = groupName)

                                val idToFields = filteredProtocols.flatMap { it.filledFields }.groupBy { it.protocolId }
                                idToFields.filter { it.value.map { it.value }.contains(groupName) }
                                    .forEach { (protocolId, fields) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 16.dp)
                                                .clickable {
                                                    val checkedState =
                                                        protocolIdsForSaving.getOrPut(protocolId) { mutableStateOf(false) }
                                                    checkedState.value = !checkedState.value
                                                },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Start
                                        ) {
                                            Checkbox(
                                                checked = protocolIdsForSaving.getOrPut(protocolId) {
                                                    mutableStateOf(
                                                        false
                                                    )
                                                }.value,
                                                onCheckedChange = null
                                            )
                                            Text(text = "${fields.first { it.key == "TIME" }.value} ")
                                            Text(text = fields.first { it.key == "DATE" }.value)
                                        }
                                    }
                            }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)) {
                EnabledTextButton("Начальное меню") {
                    navigator.popUntilRoot()
                }
                EnabledTextButton("Открыть") {
                    ProtocolManager.open(protocolIdsForSaving.filter { it.value.value }.keys)
                }
            }
        }
    }
}
