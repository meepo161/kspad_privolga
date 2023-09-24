package ru.avem.stand.db

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.stand.db.entities.*
import ru.avem.stand.testitem.TestItemLocal
import ru.avem.stand.view.composables.MotorType
import ru.avem.stand.view.composables.SchemeType
import java.sql.Connection
import java.text.SimpleDateFormat

object DBManager {
    init {
        Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        validateData()
    }

    private fun validateData() {
        transaction { SchemaUtils.create(TestItems, TestItemFields, Protocols, ProtocolFields) }
        if (transaction { TestItem.all().count() } == 0) initTestItems()
    }

    private fun initTestItems() {
        listOf(
            TestItemLocal(
                0,
                "Тестовый",
                45.0,
                380.0,
                20.0,
                750.0,
                0.87,
                MotorType.A.toString(),
                SchemeType.STAR.toString(),
                50.0,
                1000.0,
                20.0,
                500.0,
                60.0,
                60.0,
                60.0,
                500.0,
                1000.0,
                20.0,
                60.0,
            ),
        ).map(TestItemLocal::convertToDBType).forEach { saveTI(it) }
    }

    private fun saveTI(data: List<TestItemFieldScheme>): TestItem {
        return transaction {
            TestItem.new {
                this.name = data.find { it.key == "name" }!!.value
            }.also { ti ->
                data.forEach { field ->
                    TestItemField.new {
                        testItem = ti

                        key = field.key
                        title = field.title

                        typeEnterRaw = field.typeEnterRaw
                        typeFormatRaw = field.typeFormatRaw

                        minValue = field.minValue
                        value = field.value
                        maxValue = field.maxValue
                        unit = field.unit

                        permittedValuesString = field.permittedValuesString
                        permittedTitlesString = field.permittedTitlesString

                        blockName = field.blockName

                        isNotVoid = field.isNotVoid
                    }
                }
            }
        }
    }

    val testItems
        get() = transaction { TestItem.all().toList() }

    fun addTI(ti: TestItemLocal) {
        saveTI(ti.convertToDBType())
    }

    fun replaceTI(oldTI: TestItemLocal, ti: TestItemLocal): TestItem {
        deleteTestItemById(oldTI.id)
        return saveTI(ti.convertToDBType())
    }

    fun deleteTestItemByEntity(ti: TestItem) {
        transaction {
            ti.fieldsIterable.forEach {
                it.delete()
            }
            ti.delete()
        }
    }

    fun deleteTestItemById(id: Int) {
        transaction {
            TestItems.deleteWhere {
                TestItems.id eq id
            }
            TestItemFields.deleteWhere {
                TestItemFields.testItem eq id
            }
        }
    }

    fun saveProtocol(protocol: ProtocolDurable) = transaction {
        Protocol.new {
            serial = protocol.serial
            testItemType = protocol.testItemType

            val millis = System.currentTimeMillis()
            date = SimpleDateFormat("dd.MM.yyyy").format(millis)
            time = SimpleDateFormat("HH:mm").format(millis)
        }.also {
            ProtocolField.new {
                this.protocol = it
                key = "PROTOCOL_NUMBER"
                value = it.id.toString()
            }
            ProtocolField.new {
                this.protocol = it
                key = "TEST_TYPE"
                value = it.testItemType
            }
            ProtocolField.new {
                this.protocol = it
                key = "SERIAL"
                value = it.serial
            }
            protocol.fields.forEach { register ->
                ProtocolField.new {
                    this.protocol = it
                    key = register.first
                    value = register.second
                }
            }
            ProtocolField.new {
                this.protocol = it
                key = "DATE"
                value = it.date
            }
            ProtocolField.new {
                this.protocol = it
                key = "TIME"
                value = it.time
            }
        }
    }

    val protocols
        get() = transaction { Protocol.all().toList() }

    fun deleteProtocolByEntity(p: Protocol) { // TODO del?
        transaction {
            p.fields.forEach {
                it.delete()
            }
            p.delete()
        }
    }

    fun deleteProtocolById(id: EntityID<Int>) { // TODO del?
        transaction {
            Protocols.deleteWhere {
                Protocols.id eq id
            }
            ProtocolFields.deleteWhere {
                ProtocolFields.protocol eq id
            }
        }
    }
}
