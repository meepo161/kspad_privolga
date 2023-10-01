package ru.avem.stand.tests.business

import ru.avem.library.polling.IDeviceController
import ru.avem.stand.af
import ru.avem.stand.formatPoint
import ru.avem.stand.io.DevicePoller.DD2
import ru.avem.stand.io.DevicePoller.PAV41
import ru.avem.stand.io.DevicePoller.PS81
import ru.avem.stand.io.DevicePoller.UZ91
import ru.avem.stand.tests.KSPADTest
import ru.avem.stand.tests.model.Field
import ru.avem.stand.tests.model.TestRow
import ru.avem.stand.view.composables.table.*
import ru.avem.stand.view.composables.table.TableScheme.Companion.named
import kotlin.concurrent.thread

object ShortCircuit : KSPADTest(
    abbr = "КЗ",
    tag = "KZ",
    name = "Определение тока и потерь короткого замыкания",
    tables(
        table1(
            columns(
                TestRow::c1 named "U, В",
                TestRow::c2 named "I, А",
                TestRow::c3 named "P1, кВт",
                TestRow::c4 named "cos ф, о.е.",
            )
        ),
        table2(
            columns(
                TestRow::c1 named "t раб., °C",
                TestRow::c2 named "t пол., °C",
            )
        ),
        table3(
            columns(
                TestRow::c1 named "Состояние",
            )
        )
    )
) {
    private var I_RATIO = 300.0 / 5.0

    private val voltage = Field(100)

    private val testItemVoltageABMeas: Field = Field {
        testItemVoltageMeas.value = (it.d + testItemVoltageBCMeas.d + testItemVoltageCAMeas.d) / 3.0
    } pollBy with(PAV41) { this to model.U_AB_REGISTER }
    private val testItemVoltageBCMeas: Field = Field {
        testItemVoltageMeas.value = (testItemVoltageABMeas.d + it.d + testItemVoltageCAMeas.d) / 3.0
    } pollBy with(PAV41) { this to model.U_BC_REGISTER }
    private val testItemVoltageCAMeas: Field = Field {
        testItemVoltageMeas.value = (testItemVoltageABMeas.d + testItemVoltageBCMeas.d + it.d) / 3.0
    } pollBy with(PAV41) { this to model.U_CA_REGISTER }
    private val testItemVoltageMeas: Field = Field(id = "U") bindTo table1r1.c1

    private val testItemCurrentAMeas: Field = Field(k = I_RATIO) {
        testItemCurrentMeas.value = (it.d + testItemCurrentBMeas.d + testItemCurrentCMeas.d) / 3.0
    } pollBy with(PAV41) { this to model.I_A_REGISTER }
    private val testItemCurrentBMeas: Field = Field(k = I_RATIO) {
        testItemCurrentMeas.value = (testItemCurrentAMeas.d + it.d + testItemCurrentCMeas.d) / 3.0
    } pollBy with(PAV41) { this to model.I_B_REGISTER }
    private val testItemCurrentCMeas: Field = Field(k = I_RATIO) {
        testItemCurrentMeas.value = (testItemCurrentAMeas.d + testItemCurrentBMeas.d + it.d) / 3.0
    } pollBy with(PAV41) { this to model.I_C_REGISTER }
    private val testItemCurrentMeas: Field = Field(id = "I", numOfSymbols = 1) bindTo table1r1.c2

    private val powerRaw =
        Field(abs = true) { power.value = it.d * I_RATIO } pollBy with(PAV41) { this to model.P_REGISTER }
    private val power = Field(id = "P1", numOfSymbols = 2) bindTo table1r1.c3
    private val cos = Field(id = "Cos", numOfSymbols = 3) pollBy with(PAV41) { this to model.COS_REGISTER } bindTo table1r1.c4

    private val tempShaftside: Field = Field(id = "Temp1", numOfSymbols = 1) pollBy with(PS81) { this to model.T_1 } bindTo table2r1.c1
    private val tempFanside: Field = Field(id = "Temp2", numOfSymbols = 1) pollBy with(PS81) { this to model.T_2 } bindTo table2r1.c2

    private val out1UFI = Field(init = 0.0, min = 0.0, max = 400.0) {
        UZ91.setVoltage(it.d)
    }

    override val stateCell = table3r1.c1

    override val fields = listOf(
        testItemVoltageABMeas,
        testItemVoltageBCMeas,
        testItemVoltageCAMeas,
        testItemVoltageMeas,

        testItemCurrentAMeas,
        testItemCurrentBMeas,
        testItemCurrentCMeas,
        testItemCurrentMeas,

        powerRaw,
        power,
        cos,

        tempShaftside,
        tempFanside,

        out1UFI,
    )

    override val checkedDevices = mutableListOf<IDeviceController>(PAV41, PS81)

    override val alertMessages = listOf("Подключите провода U, V, W к ОИ. Зафиксируйте вал ОИ")

    init {
        define(
            initVariables = {
                I_RATIO = 300.0 / 5.0
            },
            assemblyCircuit = {
                DD2.onShortCircuit()
                loadUZ91()
            },
            process = {
                if (isRunning) {
                    setVoltageByUZ91()
                    selectCurrentStage({ testItemCurrentMeas.d }) { I_RATIO = it }
                }
            }
        )
    }

    private fun setVoltageByUZ91() {
        state = "Инициализация ЧП"
        if (isRunning) UZ91.setObjectParamsRun()
        if (isRunning) UZ91.setVoltage(0.0)
        if (isRunning) UZ91.setObjectFCur(50.0)
        if (isRunning) UZ91.startObject()
        if (isRunning) wait(3)

        if (isRunning) {
            thread(isDaemon = true) {
                if (isRunning) wait(5)
                var countCause = 0
                while (isRunning) {
                    if (testItemCurrentMeas.d > 1.5) {
                        if (testItemCurrentAMeas.d * 1.8 < testItemCurrentBMeas.d || testItemCurrentAMeas.d * 0.2 > testItemCurrentBMeas.d
                            || testItemCurrentBMeas.d * 1.8 < testItemCurrentCMeas.d || testItemCurrentBMeas.d * 0.2 > testItemCurrentCMeas.d
                            || testItemCurrentCMeas.d * 1.8 < testItemCurrentAMeas.d || testItemCurrentCMeas.d * 0.2 > testItemCurrentAMeas.d
                        ) {
                            countCause++
                        } else {
                            countCause = 0
                        }
                        if (countCause > 3) cause =
                            "Асимметрия токов. A = ${testItemCurrentAMeas.d.af()}  B = ${testItemCurrentAMeas.d.af()}  C = ${testItemCurrentCMeas.d.af()}"
                    }
                    wait(2)
                }
            }
        }
        
        if (isRunning) state = "Подъём напряжения до ${voltage.d.formatPoint()} В (контроль PAV41) по UZ91"
        if (isRunning) regulation(
            out1UFI,
            voltage.d,
            deltaMin = 1,
            deltaMax = 3,
            influenceStep = 2,
            waitSec = .05
        ) { testItemVoltageMeas.d }

        if (testItemCurrentMeas.d >= 260) {
            cause = "Сработала токовая защита"
        } else if (isRunning) {
            state = "Напряжение установлено"
        }
    }
}
