package ru.avem.stand.db.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object TestItems : IntIdTable() {
    val name = varchar("name", 256)
}

class TestItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestItem>(TestItems)

    var name by TestItems.name

    val fieldsIterable by TestItemField referrersOn (TestItemFields.testItem)

    val fields: Map<String, TestItemField>
        get() = transaction { fieldsIterable.associateBy { it.key } }

    override fun toString() = name
}
