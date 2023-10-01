package ru.avem.stand.tests.business

import ru.avem.library.polling.IDeviceController
import ru.avem.stand.formatPoint
import ru.avem.stand.io.DevicePoller.DD2
import ru.avem.stand.io.DevicePoller.PAV41
import ru.avem.stand.io.DevicePoller.PC71
import ru.avem.stand.io.DevicePoller.UZ91
import ru.avem.stand.tests.KSPADTest
import ru.avem.stand.tests.model.Field
import ru.avem.stand.tests.model.TestRow
import ru.avem.stand.view.composables.MotorType
import ru.avem.stand.view.composables.table.*
import ru.avem.stand.view.composables.table.TableScheme.Companion.named

object TransformationRatio : KSPADTest(
    abbr = "КТР",
    tag = "Ktr",
    name = "Определение коэффициента трансформации",
    tables(
        table1(
            columns(
                TestRow::c1 named "Uuv1, В",
                TestRow::c2 named "Uvw1, В",
                TestRow::c3 named "Uwu1, В",
                TestRow::c4 named "U ср. 1, В",
            )
        ),
        table2(
            columns(
                TestRow::c1 named "Uuv2, В",
                TestRow::c2 named "Uvw2, В",
                TestRow::c3 named "Uwu2, В",
                TestRow::c4 named "U ср. 2, В",
            )
        ),
        table3(
            columns(
                TestRow::c1 named "Ктр, о.е.",
            )
        ),
        table4(
            columns(
                TestRow::c1 named "Состояние",
            )
        )
    )
) {
    override val availableMotors: List<MotorType>
        get() = MotorType.wrs

    private val voltage = Field(100)

    enum class Stage {
        BEFORE,
        AFTER
    }

    private var stage = Stage.BEFORE

    private val testItemVoltageABMeas: Field = Field {
        when (stage) {
            Stage.BEFORE -> testItemVoltageABMeasStat.value = it.d
            Stage.AFTER -> testItemVoltageABMeasRot.value = if (it.d < 10.0) 0.0 else it.d
        }
    } pollBy with(PAV41) { this to model.U_AB_REGISTER }
    private val testItemVoltageBCMeas: Field = Field {
        when (stage) {
            Stage.BEFORE -> testItemVoltageBCMeasStat.value = it.d
            Stage.AFTER -> testItemVoltageBCMeasRot.value = if (it.d < 10.0) 0.0 else it.d
        }
    } pollBy with(PAV41) { this to model.U_BC_REGISTER }
    private val testItemVoltageCAMeas: Field = Field {
        when (stage) {
            Stage.BEFORE -> testItemVoltageCAMeasStat.value = it.d
            Stage.AFTER -> testItemVoltageCAMeasRot.value = if (it.d < 10.0) 0.0 else it.d
        }
    } pollBy with(PAV41) { this to model.U_CA_REGISTER }

    private val out1UFI = Field(init = 0.0, min = 0.0, max = 400.0) {
        UZ91.setVoltage(it.d)
        UZ91.setObjectFCur(it.d / 7.6)
    }


    private val testItemVoltageABMeasStat: Field = Field(id = "Uuv1", numOfSymbols = 1) {
        testItemVoltageMeasStat.value = (it.d + testItemVoltageBCMeasStat.d + testItemVoltageCAMeasStat.d) / 3.0
    } bindTo table1r1.c1
    private val testItemVoltageBCMeasStat: Field = Field(id = "Uvw1", numOfSymbols = 1) {
        testItemVoltageMeasStat.value = (testItemVoltageABMeasStat.d + it.d + testItemVoltageCAMeasStat.d) / 3.0
    } bindTo table1r1.c2
    private val testItemVoltageCAMeasStat: Field = Field(id = "Uwu1", numOfSymbols = 1) {
        testItemVoltageMeasStat.value = (testItemVoltageABMeasStat.d + testItemVoltageBCMeasStat.d + it.d) / 3.0
    } bindTo table1r1.c3
    private val testItemVoltageMeasStat: Field = Field(id = "Usr1", numOfSymbols = 1) bindTo table1r1.c4

    private val testItemVoltageABMeasRot: Field = Field(id = "Uuv2", numOfSymbols = 1) {
        testItemVoltageMeasRot.value = (it.d + testItemVoltageBCMeasRot.d + testItemVoltageCAMeasRot.d) / 3.0
    } bindTo table2r1.c1
    private val testItemVoltageBCMeasRot: Field = Field(id = "Uvw2", numOfSymbols = 1) {
        testItemVoltageMeasRot.value = (testItemVoltageABMeasRot.d + it.d + testItemVoltageCAMeasRot.d) / 3.0
    } bindTo table2r1.c2
    private val testItemVoltageCAMeasRot: Field = Field(id = "Uwu2", numOfSymbols = 1) {
        testItemVoltageMeasRot.value = (testItemVoltageABMeasRot.d + testItemVoltageBCMeasRot.d + it.d) / 3.0
    } bindTo table2r1.c3
    private val testItemVoltageMeasRot: Field = Field(id = "Usr2", numOfSymbols = 1) bindTo table2r1.c4

    private val ktr: Field = Field(id = "Ktr", numOfSymbols = 2) bindTo table3r1.c1

    private val rpm: Field = Field(id = "Speed") pollBy with(PC71) { this to model.RPM }

    override val stateCell = table4r1.c1

    override val fields = listOf(
        testItemVoltageABMeas,
        testItemVoltageBCMeas,
        testItemVoltageCAMeas,

        testItemVoltageABMeasStat,
        testItemVoltageBCMeasStat,
        testItemVoltageCAMeasStat,
        testItemVoltageMeasStat,

        testItemVoltageABMeasRot,
        testItemVoltageBCMeasRot,
        testItemVoltageCAMeasRot,
        testItemVoltageMeasRot,

        ktr,

        rpm,

        out1UFI,
    )

    override val checkedDevices = mutableListOf<IDeviceController>(PAV41, PC71)

    override val alertMessages = listOf("Подключите провода U, V, W к статору ОИ, провода U2, V2, W2 к ротору ОИ")

    init {
        define(
            initVariables = {
                stage = Stage.BEFORE
            },
            assemblyCircuit = {
                DD2.onIdling()
                loadUZ91()
            },
            process = {
                if (isRunning) while (rpm.d > 50) {
                    state = "Ожидание останова"
                    wait(1)
                }

                if (isRunning) setVoltageByUZ91()
                if (isRunning) wait(5)
                stage = Stage.AFTER
                if (isRunning) DD2.switchKTR()
                if (isRunning) wait(5)
                val ktrRaw = testItemVoltageMeasStat.d / testItemVoltageMeasRot.d
                if (isRunning) if (ktrRaw <= 0 || ktrRaw > 1000) ktr.stringValue = "∞" else ktr.value = ktrRaw
            },
            finish = {
                UZ91.offFreewheeling(out1UFI)
                if (ktr.stringValue == "∞") cause = "Обмотка ротора не подключена"
            }
        )
    }

    private fun setVoltageByUZ91() {
        state = "Инициализация ЧП"
        if (isRunning) UZ91.setObjectParamsRun()
        if (isRunning) UZ91.setVoltage(0.0)
        if (isRunning) UZ91.setObjectFCur(0.0)
        if (isRunning) UZ91.startObject()
        if (isRunning) wait(3)

        if (isRunning) state = "Подъём напряжения до ${voltage.d.formatPoint()} В (контроль PAV41) по UZ91"
        if (isRunning) regulation(
            out1UFI,
            voltage.d,
            deltaMin = 1,
            deltaMax = 3,
            influenceStep = 0.6,
            waitSec = .05
        ) { testItemVoltageMeasStat.d }

        if (isRunning) state = "Напряжение установлено"
    }
}
