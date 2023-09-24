package ru.avem.stand.db.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object TestItemFields : IntIdTable() {
    val testItem = reference("testItem", TestItems)

    val key = varchar("key", 128)
    val title = varchar("title", 512)

    val typeEnterRaw = varchar("typeEnterRaw", 10)
    val typeFormatRaw = varchar("typeFormatRaw", 10)

    val minValue = varchar("minValue", 512)
    val value = varchar("value", 512)
    val maxValue = varchar("maxValue", 512)
    val unit = varchar("unit", 10)

    val permittedValuesString = varchar("permittedValuesString", 1024)
    val permittedTitlesString = varchar("permittedTitlesString", 1024)

    val blockName = varchar("blockName", 1024)

    val isNotVoid = bool("isNotVoid")
}

class TestItemField(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestItemField>(TestItemFields)

    var testItem by TestItem referencedOn TestItemFields.testItem

    var key by TestItemFields.key
    var title by TestItemFields.title

    var typeEnterRaw by TestItemFields.typeEnterRaw
    var typeFormatRaw by TestItemFields.typeFormatRaw

    var minValue by TestItemFields.minValue
    var value by TestItemFields.value
    var maxValue by TestItemFields.maxValue
    var unit by TestItemFields.unit

    var permittedValuesString by TestItemFields.permittedValuesString
    var permittedTitlesString by TestItemFields.permittedTitlesString

    var blockName by TestItemFields.blockName

    var isNotVoid by TestItemFields.isNotVoid

    override fun toString() = "$title${if (unit.isNotEmpty()) ", $unit" else ""}"
}
