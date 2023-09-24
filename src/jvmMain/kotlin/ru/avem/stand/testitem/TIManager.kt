package ru.avem.stand.testitem

import androidx.compose.runtime.mutableStateOf
import ru.avem.stand.db.DBManager
import ru.avem.stand.db.entities.TestItem

object TIManager {
    val all: List<TestItemLocal>
        get() = DBManager.testItems.map { it.convertToLocalType() }

    fun TestItem.convertToLocalType() = TestItemLocal(
        id = id.value,
        name = fields["name"]!!.value,
        nominalPower = fields["nominalPower"]!!.value.toDouble(),
        nominalVoltage = fields["nominalVoltage"]!!.value.toDouble(),
        nominalCurrent = fields["nominalCurrent"]!!.value.toDouble(),
        nominalN = fields["nominalN"]!!.value.toDouble(),
        nominalCos = fields["nominalCos"]!!.value.toDouble(),
        motor = fields["motor"]!!.value,
        scheme = fields["scheme"]!!.value,
        frequency = fields["frequency"]!!.value.toDouble(),
        hvVoltage = fields["hvVoltage"]!!.value.toDouble(),
        hvCurrent = fields["hvCurrent"]!!.value.toDouble(),
        meggerVoltage = fields["meggerVoltage"]!!.value.toDouble(),
        idleTestTime = fields["idleTestTime"]!!.value.toDouble(),
        runoutTestTime = fields["runoutTestTime"]!!.value.toDouble(),
        hvTestTime = fields["hvTestTime"]!!.value.toDouble(),
        meggerSVoltage = fields["meggerSVoltage"]!!.value.toDouble(),
        hvSVoltage = fields["hvSVoltage"]!!.value.toDouble(),
        hvSCurrent = fields["hvSCurrent"]!!.value.toDouble(),
        hvSTestTime = fields["hvSTestTime"]!!.value.toDouble(),
    )

    val testItemState = mutableStateOf(all.first())

    val testItem: TestItemLocal
        get() = testItemState.value

    val serialTIState = mutableStateOf("")

    val serialTI: String
        get() = serialTIState.value
}
