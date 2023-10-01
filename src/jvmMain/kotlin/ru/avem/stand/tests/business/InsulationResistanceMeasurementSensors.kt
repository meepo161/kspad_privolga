package ru.avem.stand.tests.business

import ru.avem.library.polling.IDeviceController
import ru.avem.stand.formatPoint
import ru.avem.stand.io.DevicePoller.DD2
import ru.avem.stand.io.DevicePoller.PR65
import ru.avem.stand.io.DevicePoller.PS81
import ru.avem.stand.io.avem.avem9.AVEM9Model
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

object InsulationResistanceMeasurementSensors : KSPADTest(
    abbr = "МГР ДТЧК",
    tag = "TMGR",
    name = "Измерение сопротивления изоляции встроенных термодатчиков относительно корпуса и между обмотками (фазами)",
    tables(
        table1(
            columns(
                TestRow::c1 named "R15, MОм",
                TestRow::c2 named "R60, MОм",
                TestRow::c3 named "Kabs",
                TestRow::c4 named "Ur, В",
                TestRow::c5 named "t окр. ср., °C",
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

    private val r15: Field = Field(id = "R15", numOfSymbols = 2) bindTo table1r1.c1
    private val r60: Field = Field(id = "R60", numOfSymbols = 2) bindTo table1r1.c2
    private val kABS: Field = Field(id = "KABS", numOfSymbols = 2) bindTo table1r1.c3
    private val measU: Field = Field(id = "U") bindTo table1r1.c4
    private val temp: Field =
        Field(id = "T", numOfSymbols = 1) pollBy with(PS81) { this to model.T_1 } bindTo table1r1.c5

    private val status: Field = Field() pollBy with(PR65) { this to model.STATUS }

    override val stateCell = table2r1.c1

    override val fields = listOf(
        r15,
        r60,
        kABS,
        measU,
        temp,
        status,
    )

    override val checkedDevices = mutableListOf<IDeviceController>(PS81)

    override val alertMessages =
        listOf("Подключите провод ВИУ (ХА1) к испытуемой точке, провод РЕ (ХА2) к точке относительно которой будет проходит проверка")

    private var specU = 0

    init {
        define(
            initVariables = {
                specU = TIManager.testItem.meggerSVoltage.toInt()
            },
            assemblyCircuit = {
                if (isRunning) DD2.onMGRDevice()
                wait(2)
                if (isRunning) DD2.offMGRDevice()
                checkedDevices.add(PR65)
                wait(2)
                if (isRunning && PR65.isResponding) DD2.onMGR()
            },
            process = {
                if (isRunning) measureR()
            },
            finish = {
                checkedDevices.remove(PR65)
            }
        )
    }

    private fun measureR() {
        state = "Измерение сопротивления"

        val u = when (specU) {
            in 0..750 -> AVEM9Model.SpecifiedVoltage.V500
            in 750..1750 -> AVEM9Model.SpecifiedVoltage.V1000
            else -> AVEM9Model.SpecifiedVoltage.V2500
        }

        PR65.startMeasurement(AVEM9Model.MeasurementMode.AbsRatio, u)
        wait(3)

        var isNeedContinue = { isRunning }
        wait(60, isNeedContinue = isNeedContinue) {
            if (status.i == 4) isNeedContinue = { false }
            state = "Измерение. Прошло ${it.toInt()} секунд"
        }
        wait(2)

        PR65.getRegisterById(PR65.model.R15_MEAS).apply {
            PR65.readRegister(this)
            r15.value = value
        }
        PR65.getRegisterById(PR65.model.R60_MEAS).apply {
            PR65.readRegister(this)
            r60.value = value
        }
        PR65.getRegisterById(PR65.model.ABSORPTION).apply {
            PR65.readRegister(this)
            kABS.value = value
        }
        PR65.getRegisterById(PR65.model.VOLTAGE).apply {
            PR65.readRegister(this)
            measU.value = value
        }

        if (isRunning) {
            DD2.offAllKMs()
            DD2.light()
            waitWithDescription(30) { "Заземление и разряд объекта. Осталось ${(30 - it).formatPoint()} секунд" }
        }
    }
}
