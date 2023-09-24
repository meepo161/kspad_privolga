package ru.avem.stand.db.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

object Protocols : IntIdTable() {
    val serial = varchar("serial", 128)
    val testItemType = varchar("testItemType", 256)

    val date = varchar("date", 10)
    val time = varchar("time", 8)
}

class Protocol(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Protocol>(Protocols)

    val myId get() = transaction { id.value }

    var serial by Protocols.serial
    var testItemType by Protocols.testItemType

    var date by Protocols.date
    var time by Protocols.time

    val fields by ProtocolField referrersOn (ProtocolFields.protocol)

    val filledFields
        get() = transaction {
            fields.toList()
        }

    override fun toString() = "$id. $testItemType №$serial [$date - $time]"

    fun toMills(): Long {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.ENGLISH)
        val localDate = LocalDateTime.parse("$date $time", formatter)
        return localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
}

class ProtocolDurable(
    var serial: String = "",
    var testItemType: String = "",
    val fields: MutableList<Pair<String, String>> = mutableListOf(),
)
