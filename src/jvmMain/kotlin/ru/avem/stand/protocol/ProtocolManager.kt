package ru.avem.stand.protocol

import androidx.compose.runtime.mutableStateOf
import ru.avem.stand.db.DBManager
import ru.avem.stand.db.entities.Protocol
import ru.avem.stand.db.entities.ProtocolDurable
import ru.avem.stand.testitem.TIManager
import java.awt.Desktop

object ProtocolManager {
    private var durableProtocol: ProtocolDurable = ProtocolDurable()

    val all: List<Protocol>
        get() = DBManager.protocols

    var toastText = mutableStateOf("")

    fun open(ids: Collection<Int>) {
        val protocols = all.filter { it.myId in ids }
        protocols.forEach {
            println(it.time)
        }

        if (protocols.isNotEmpty()) {
            try {
                save(protocols).let {
                    try {
                        Desktop.getDesktop().open(it.toFile())
                    } catch (e: Exception) {
                        errorNotification(
                            title = "Не удалось открыть протокол",
                            text = "Причины: $e"
                        )
                    }
                }
            } catch (e: Exception) {
                errorNotification(
                    title = "Не удалось сохранить протокол",
                    text = "Причины: $e"
                )
            }
        } else {
            errorNotification("Выберите заводской номер и необходимые для сохранения опыты")
        }
    }

    private fun save(protocols: List<Protocol>) = saveProtocolsAsWorkbook(protocols, "protocol_template.xlsx")
        .apply { infoNotification(text = "Протокол сохранён по пути $this") }

    private fun errorNotification(title: String = "Ошибка", text: String = "") {
        toastText.value = "$title $text"
    }

    private fun infoNotification(title: String = "Инфо", text: String = "") {
        toastText.value = "$title $text"
    }

    fun saveField(data: Pair<String, String>) {
        durableProtocol.fields.removeIf { it.first == data.first && it.first != "TEST_NAME" }
        durableProtocol.fields.add(data)
    }

    fun getField(key: String, idSlot: Int = 0) = durableProtocol.fields.firstOrNull { it.first == key }?.second

    fun fillAndSave(onSave: (ProtocolDurable) -> Protocol) {
        fillDurableProtocol()
        if (durableProtocol.serial.isNotEmpty()) {
            onSave(durableProtocol)
        }
        clearDurableProtocol()
    }

    private fun fillDurableProtocol() {
        durableProtocol.serial = TIManager.serialTI
        durableProtocol.testItemType = TIManager.testItem.name
    }

    private fun clearDurableProtocol() {
        durableProtocol = ProtocolDurable()
    }
}
