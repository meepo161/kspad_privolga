package ru.avem.stand.tests.business

import ru.avem.library.polling.IDeviceController
import ru.avem.stand.io.DevicePoller.DD2
import ru.avem.stand.io.DevicePoller.PA62
import ru.avem.stand.io.DevicePoller.PV61
import ru.avem.stand.testitem.TIManager
import ru.avem.stand.tests.KSPADTest
import ru.avem.stand.tests.model.Field
import ru.avem.stand.tests.model.TestRow
import ru.avem.stand.view.composables.MotorType
import ru.avem.stand.view.composables.table.TableScheme.Companion.named
import ru.avem.stand.view.composables.table.columns
import ru.avem.stand.view.composables.table.table1
import ru.avem.stand.view.composables.table.table2
import ru.avem.stand.view.composables.table.tables

object DCWindingResistanceSensors : KSPADTest(
    abbr = "ИКАС ДТЧК",
    tag = "IKAST",
    name = "Измерение сопротивления встроенных термодатчиков при постоянном токе в практически холодном состоянии",
    tables(
        table1(
            columns(
                TestRow::c1 named "R, Ом",
            )
        ),
        table2(
            columns(
                TestRow::c1 named "Состояние",
            )
        ),
    )
) {
    override val availableMotors: List<MotorType>
        get() = MotorType.withSensors

    private val testItemVoltage: Field = Field(abs = true) pollBy with(PV61) { this to model.U_TRMS }
    private val testItemCurrent: Field = Field(abs = true) pollBy with(PA62) { this to model.AMPERAGE }

    private val R: Field = Field(id = "R", numOfSymbols = 4) bindTo table1r1.c1

    override val stateCell = table2r1.c1

    override val fields = listOf(
        testItemVoltage, testItemCurrent,

        R,
    )

    override val checkedDevices = mutableListOf<IDeviceController>(PV61, PA62)

    override val alertMessages = listOf("Подключите провода U и V к ОИ")

    private var scheme = ""

    init {
        define(
            initVariables = {
                scheme = TIManager.testItem.scheme
            },
            process = {
                if (isRunning) meas()
            }
        )
    }

    private fun meas() {
        state = "Измерение сопротивление между u и v"
        DD2.onIKASa()
        DD2.onIKASba()
        wait(10)
        R.value = testItemVoltage.d / testItemCurrent.d
        DD2.offIKASa()
        DD2.offIKASba()
    }
}
