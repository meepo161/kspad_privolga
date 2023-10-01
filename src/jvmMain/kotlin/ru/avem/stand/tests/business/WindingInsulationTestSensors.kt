package ru.avem.stand.tests.business

import ru.avem.library.polling.IDeviceController
import ru.avem.stand.formatPoint
import ru.avem.stand.io.DevicePoller.DD2
import ru.avem.stand.io.DevicePoller.PAV41
import ru.avem.stand.io.DevicePoller.PV24
import ru.avem.stand.io.DevicePoller.UZ91
import ru.avem.stand.testitem.TIManager
import ru.avem.stand.tests.KSPADTest
import ru.avem.stand.tests.model.Field
import ru.avem.stand.tests.model.TestRow
import ru.avem.stand.view.composables.MotorType
import ru.avem.stand.view.composables.table.*
import ru.avem.stand.view.composables.table.TableScheme.Companion.named
import kotlin.concurrent.thread

object WindingInsulationTestSensors : KSPADTest(
    abbr = "ВИУ ДТЧК",
    tag = "VIUT",
    name = "Испытание электрической прочности изоляции встроенных термодатчиков относительно корпуса и между обмотками",
    tables(
        table1(
            columns(
                TestRow::c1 named "U заданное, В",
                TestRow::c2 named "I максимальное, мА",
                TestRow::c3 named "Время заданное, с",
            )
        ),
        table2(
            columns(
                TestRow::c1 named "U измеренное, В",
                TestRow::c2 named "I измеренное, мА",
                TestRow::c3 named "Время, с",
            )
        ),
        table3(
            columns(
                TestRow::c1 named "Состояние",
            )
        )
    )
) {
    override val availableMotors: List<MotorType>
        get() = MotorType.withSensors

    private const val I_RATIO_VIU = 1.0 / 5.0

    private val voltage = Field(id = "Ubd") bindTo table1r1.c1
    private val current = Field(id = "Ibd") bindTo table1r1.c2
    private val testTime = Field(id = "Tbd") bindTo table1r1.c3

    private val testItemVoltageMeas: Field =
        Field(id = "U", abs = true) pollBy with(PV24) { this to model.U_TRMS } bindTo table2r1.c1

    private val testItemCurrentRawMeas: Field = Field(k = I_RATIO_VIU * 1000.0) {
        testItemCurrentMeas.value = it.d
    } pollBy with(PAV41) { this to model.I_A_REGISTER }

    private val testItemCurrentMeas: Field = Field(id = "I", abs = true) bindTo table2r1.c2

    private val timePassed = Field(id = "T") bindTo table2r1.c3

    private val out1UFI = Field(init = 0.0, min = 0.0, max = 400.0) { UZ91.setVoltage(it.d) }

    override val stateCell = table3r1.c1

    private var timeEnded = false

    override val fields = listOf(
        voltage,
        current,
        testTime,

        testItemVoltageMeas,
        testItemCurrentRawMeas,
        testItemCurrentMeas,

        timePassed,

        out1UFI,
    )

    override val checkedDevices = mutableListOf<IDeviceController>(PV24, PAV41)

    override val alertMessages =
        listOf("Подключите провод ВИУ (ХА1) к испытуемой точке, провод РЕ (ХА2) к точке относительно которой будет проходит проверка")

    init {
        define(
            initVariables = {
                voltage.value = TIManager.testItem.hvSVoltage
                current.value = TIManager.testItem.hvSCurrent
                testTime.value = TIManager.testItem.hvSTestTime
            },
            assemblyCircuit = {
                DD2.onHV()
                loadUZ91()
            },
            process = {
                if (isRunning) {
                    timeEnded = false
                    setVoltageByUZ91()

                    if (isRunning) state = "Ожидание $testTime s..."
                    wait(testTime.value) {
                        if (testItemCurrentMeas.d >= current.d) cause = "Ток превысил заданный"
                        timePassed.value = it
                        state = "Осталось ${(testTime.d - it).formatPoint()} с"
                    }
                    timeEnded = true
                }
            },
            finish = {
                UZ91.off(out1UFI)
            }
        )
    }

    private fun setVoltageByUZ91() {
        state = "Инициализация ЧП"
        if (isRunning) UZ91.setObjectParamsRun()
        if (isRunning) UZ91.setObjectFCur(50.0)
        if (isRunning) UZ91.startObject()
        if (isRunning) wait(3)

        if (isRunning) startCheckKTR()

        if (isRunning) state =
            "Подъём напряжения до ${(voltage.d * .9).formatPoint()} В (90% ном) (контроль PV24) по UZ91 (грубо)"
        if (isRunning) regulation(
            out1UFI,
            voltage.d * .9,
            deltaMin = 10,
            influenceStep = 1,
            waitSec = .05,
            isNeedContinue = { isRunning && testItemCurrentMeas.d < current.d }) { testItemVoltageMeas.d }

        if (isRunning) state = "Подъём напряжения до ${voltage.d.formatPoint()} В (контроль PV24) по UZ91 (точно)"
        if (isRunning) regulation(
            out1UFI,
            voltage.d,
            deltaMin = 0,
            deltaMax = 3,
            influenceStep = .5,
            waitSec = .1,
            isNeedContinue = { isRunning && testItemCurrentMeas.d < current.d }) { testItemVoltageMeas.d }

        if (testItemCurrentMeas.d >= current.d) {
            cause = "Ток превысил заданный"
        } else if (isRunning) {
            state = "Напряжение установлено: $testItemVoltageMeas В"
        }
    }

    private fun startCheckKTR() {
        thread(isDaemon = true) {
            wait(5)
            var countCause = 0
            while (!timeEnded) {
                if (out1UFI.d - 50 > testItemVoltageMeas.d / 13 || out1UFI.d + 50 < testItemVoltageMeas.d / 13) {
                    countCause++
                } else {
                    countCause = 0
                }
                if (countCause > 3) {
                    cause = "Коэффициент трансформации не соответствует"
                }

                wait(2)
            }
        }
    }
}
