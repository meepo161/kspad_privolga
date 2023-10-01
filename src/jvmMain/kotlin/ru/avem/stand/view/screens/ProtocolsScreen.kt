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

        val mapProtocols = remember { mutableStateMapOf<MutableMap<Int, String>, MutableState<Boolean>>() }
        var selectedProtocolField = 0

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

        LaunchedEffect(allProtocols.size)
        {
            launch {
                allProtocols = ProtocolManager.all

                serials = allProtocols.groupBy { it.serial }.keys.toList()
                selectedSerial.value = serials.firstOrNull()
            }
        }

        LaunchedEffect(selectedSerial.value)
        {
            launch { filteredProtocols = allProtocols.filter { it.serial == selectedSerial.value } }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        )
        {
            Text("Выберите заводской номер для просмотра:")
            ComboBox(modifier = Modifier.fillMaxWidth(),
                selectedItem = selectedSerial,
                items = serials,
                onSelect = { serial ->
                    selectedSerial.value = serial
                    filteredProtocols
                        .flatMap { it.filledFields }
                        .filter { it.key == "TEST_NAME" }
                        .map { it.value }
                        .toSet()
                        .forEachIndexed { groupIdx, groupName ->
                            filteredProtocols.flatMap { it.filledFields }.groupBy { it.protocolId }
                                .filter { it.value.map { it.value }.contains(groupName) }
                                .forEach { (protocolId, fields) ->
                                    mapProtocols.getOrPut(mutableMapOf(Pair(protocolId, groupName))) {
                                        mutableStateOf(
                                            false
                                        )
                                    }
                                }
                        }
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
                            .forEachIndexed { groupIdx, groupName ->
                                Text(text = groupName, fontSize = 28.sp, fontWeight = FontWeight.Bold)

                                val idToFields = filteredProtocols.flatMap { it.filledFields }.groupBy { it.protocolId }
                                idToFields.filter { it.value.map { it.value }.contains(groupName) }
                                    .forEach { (protocolId, fields) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 16.dp)
                                                .clickable {
                                                    mapProtocols.getOrPut(
                                                        mutableMapOf(Pair(protocolId, groupName))
                                                    ) { mutableStateOf(false) }
                                                    val mutableState =
                                                        mapProtocols.get(mutableMapOf(Pair(protocolId, groupName)))
                                                    mutableState!!.value = !mutableState.value
                                                },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Start
                                        ) {
                                            Checkbox(
                                                modifier = Modifier.scale(2f).height(48.dp).width(48.dp),
                                                checked = mapProtocols.getOrPut(
                                                    mutableMapOf(Pair(protocolId, groupName))
                                                ) { mutableStateOf(false) }.value,
                                                onCheckedChange = null
                                            )
                                            Text(text = "${fields.first { it.key == "TIME" }.value} ", fontSize = 28.sp)
                                            Text(text = fields.first { it.key == "DATE" }.value, fontSize = 28.sp)
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
                    var mutableList = mutableListOf<Int>()
                    mapProtocols.forEach { (t, u) ->
                        t.forEach { (t2, u2) ->
                            if (u.value) mutableList.add(t2)
                        }
                    }
                    mutableList.forEach { println(it) }
                    ProtocolManager.open(mutableList)
                }
            }
        }
    }
}
