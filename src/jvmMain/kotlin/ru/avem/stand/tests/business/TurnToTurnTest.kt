package ru.avem.stand.tests.business

import ru.avem.library.polling.IDeviceController
import ru.avem.stand.af
import ru.avem.stand.formatPoint
import ru.avem.stand.io.DevicePoller.DD2
import ru.avem.stand.io.DevicePoller.PAV41
import ru.avem.stand.io.DevicePoller.PC71
import ru.avem.stand.io.DevicePoller.UZ91
import ru.avem.stand.tests.KSPADTest
import ru.avem.stand.tests.model.Field
import ru.avem.stand.tests.model.TestRow
import ru.avem.stand.view.composables.table.*
import ru.avem.stand.view.composables.table.TableScheme.Companion.named
import kotlin.concurrent.thread
import kotlin.math.abs

object TurnToTurnTest : KSPADTest(
    abbr = "МВ", tag = "MV", name = "Испытание электрической прочности междувитковой изоляции обмоток", tables(
        table1(
            columns(
                TestRow::c1 named "U до, В",
                TestRow::c2 named "Iu до, А",
                TestRow::c3 named "Iv до, А",
                TestRow::c4 named "Iw до, А",
            )
        ), table2(
            columns(
                TestRow::c1 named "1.3U, В",
                TestRow::c2 named "Iu 1.3U, А",
                TestRow::c3 named "Iv 1.3U, А",
                TestRow::c4 named "Iw 1.3U, А",
            )
        ), table3(
            columns(
                TestRow::c1 named "U после, В",
                TestRow::c2 named "Iu после, А",
                TestRow::c3 named "Iv после, А",
                TestRow::c4 named "Iw после, А",
                TestRow::c5 named "макс. ΔI, %",
            )
        ), table4(
            columns(
                TestRow::c1 named "Состояние",
            )
        )
    )
) {
    private var I_RATIO = 300.0 / 5.0

    enum class Stage {
        BEFORE, DURING, AFTER
    }

    private var stage = Stage.BEFORE

    private val testItemVoltageABMeas: Field = Field {
        testItemVoltageMeas.value = (it.d + testItemVoltageBCMeas.d + testItemVoltageCAMeas.d) / 3.0
        when (stage) {
            Stage.BEFORE -> testItemVoltageABMeasBefore.value = it.d
            Stage.DURING -> testItemVoltageABMeasDuring.value = it.d
            Stage.AFTER -> testItemVoltageABMeasAfter.value = it.d
        }
    } pollBy with(PAV41) { this to model.U_AB_REGISTER }
    private val testItemVoltageBCMeas: Field = Field {
        testItemVoltageMeas.value = (testItemVoltageABMeas.d + it.d + testItemVoltageCAMeas.d) / 3.0
        when (stage) {
            Stage.BEFORE -> testItemVoltageBCMeasBefore.value = it.d
            Stage.DURING -> testItemVoltageBCMeasDuring.value = it.d
            Stage.AFTER -> testItemVoltageBCMeasAfter.value = it.d
        }
    } pollBy with(PAV41) { this to model.U_BC_REGISTER }
    private val testItemVoltageCAMeas: Field = Field {
        testItemVoltageMeas.value = (testItemVoltageABMeas.d + testItemVoltageBCMeas.d + it.d) / 3.0
        when (stage) {
            Stage.BEFORE -> testItemVoltageCAMeasBefore.value = it.d
            Stage.DURING -> testItemVoltageCAMeasDuring.value = it.d
            Stage.AFTER -> testItemVoltageCAMeasAfter.value = it.d
        }
    } pollBy with(PAV41) { this to model.U_CA_REGISTER }
    private val testItemVoltageMeas: Field = Field()

    private val testItemCurrentAMeas: Field = Field {
        testItemCurrentMeas.value = (it.d + testItemCurrentBMeas.d + testItemCurrentCMeas.d) / 3.0 * I_RATIO
        when (stage) {
            Stage.BEFORE -> testItemCurrentAMeasBefore.value = it.d * I_RATIO
            Stage.DURING -> testItemCurrentAMeasDuring.value = it.d * I_RATIO
            Stage.AFTER -> testItemCurrentAMeasAfter.value = it.d * I_RATIO
        }
    } pollBy with(PAV41) { this to model.I_A_REGISTER }
    private val testItemCurrentBMeas: Field = Field {
        testItemCurrentMeas.value = (testItemCurrentAMeas.d + it.d + testItemCurrentCMeas.d) / 3.0 * I_RATIO
        when (stage) {
            Stage.BEFORE -> testItemCurrentBMeasBefore.value = it.d * I_RATIO
            Stage.DURING -> testItemCurrentBMeasDuring.value = it.d * I_RATIO
            Stage.AFTER -> testItemCurrentBMeasAfter.value = it.d * I_RATIO
        }
    } pollBy with(PAV41) { this to model.I_B_REGISTER }
    private val testItemCurrentCMeas: Field = Field {
        testItemCurrentMeas.value = (testItemCurrentAMeas.d + testItemCurrentBMeas.d + it.d) / 3.0 * I_RATIO
        when (stage) {
            Stage.BEFORE -> testItemCurrentCMeasBefore.value = it.d * I_RATIO
            Stage.DURING -> testItemCurrentCMeasDuring.value = it.d * I_RATIO
            Stage.AFTER -> testItemCurrentCMeasAfter.value = it.d * I_RATIO
        }
    } pollBy with(PAV41) { this to model.I_C_REGISTER }
    private val testItemCurrentMeas: Field = Field()

    private val out1UFI = Field(init = 0.0, min = 0.0, max = 400.0) {
        UZ91.setVoltage(it.d)

        out1FFI.value = it.d / 5.776
    }

    private val out1FFI = Field(init = 0.0, min = 0.0, max = 50.0) {
        UZ91.setObjectFCur(it.d)
    }


    private val testItemVoltageABMeasBefore: Field = Field {
        testItemVoltageMeasBefore.value = (it.d + testItemVoltageBCMeasBefore.d + testItemVoltageCAMeasBefore.d) / 3.0
    }
    private val testItemVoltageBCMeasBefore: Field = Field {
        testItemVoltageMeasBefore.value = (testItemVoltageABMeasBefore.d + it.d + testItemVoltageCAMeasBefore.d) / 3.0
    }
    private val testItemVoltageCAMeasBefore: Field = Field {
        testItemVoltageMeasBefore.value = (testItemVoltageABMeasBefore.d + testItemVoltageBCMeasBefore.d + it.d) / 3.0
    }
    private val testItemVoltageMeasBefore: Field = Field(id = "UBefore") bindTo table1r1.c1

    private val testItemVoltageABMeasDuring: Field = Field {
        testItemVoltageMeasDuring.value = (it.d + testItemVoltageBCMeasDuring.d + testItemVoltageCAMeasDuring.d) / 3.0
    }
    private val testItemVoltageBCMeasDuring: Field = Field {
        testItemVoltageMeasDuring.value = (testItemVoltageABMeasDuring.d + it.d + testItemVoltageCAMeasDuring.d) / 3.0
    }
    private val testItemVoltageCAMeasDuring: Field = Field {
        testItemVoltageMeasDuring.value = (testItemVoltageABMeasDuring.d + testItemVoltageBCMeasDuring.d + it.d) / 3.0
    }
    private val testItemVoltageMeasDuring: Field = Field(id = "U130") bindTo table2r1.c1

    private val testItemVoltageABMeasAfter: Field = Field {
        testItemVoltageMeasAfter.value = (it.d + testItemVoltageBCMeasAfter.d + testItemVoltageCAMeasAfter.d) / 3.0
    }
    private val testItemVoltageBCMeasAfter: Field = Field {
        testItemVoltageMeasAfter.value = (testItemVoltageABMeasAfter.d + it.d + testItemVoltageCAMeasAfter.d) / 3.0
    }
    private val testItemVoltageCAMeasAfter: Field = Field {
        testItemVoltageMeasAfter.value = (testItemVoltageABMeasAfter.d + testItemVoltageBCMeasAfter.d + it.d) / 3.0
    }
    private val testItemVoltageMeasAfter: Field = Field(id = "UAfter") bindTo table3r1.c1


    private val testItemCurrentAMeasBefore: Field = Field(id = "IuBefore", numOfSymbols = 1) {
//        testItemCurrentMeasBefore.value = (it.d + testItemCurrentBMeasBefore.d + testItemCurrentCMeasBefore.d) / 3.0
    } bindTo table1r1.c2
    private val testItemCurrentBMeasBefore: Field = Field(id = "IvBefore", numOfSymbols = 1) {
//        testItemCurrentMeasBefore.value = (testItemCurrentAMeasBefore.d + it.d + testItemCurrentCMeasBefore.d) / 3.0
    } bindTo table1r1.c3
    private val testItemCurrentCMeasBefore: Field = Field(id = "IwBefore", numOfSymbols = 1) {
//        testItemCurrentMeasBefore.value = (testItemCurrentAMeasBefore.d + testItemCurrentBMeasBefore.d + it.d) / 3.0
    } bindTo table1r1.c4
//    private val testItemCurrentMeasBefore: Field = Field() bindTo table1r1.c5

    private val testItemCurrentAMeasDuring: Field = Field(id = "Iu130", numOfSymbols = 1) {
//        testItemCurrentMeasDuring.value = (it.d + testItemCurrentBMeasDuring.d + testItemCurrentCMeasDuring.d) / 3.0
    } bindTo table2r1.c2
    private val testItemCurrentBMeasDuring: Field = Field(id = "Iv130", numOfSymbols = 1) {
//        testItemCurrentMeasDuring.value = (testItemCurrentAMeasDuring.d + it.d + testItemCurrentCMeasDuring.d) / 3.0
    } bindTo table2r1.c3
    private val testItemCurrentCMeasDuring: Field = Field(id = "Iw130", numOfSymbols = 1) {
//        testItemCurrentMeasDuring.value = (testItemCurrentAMeasDuring.d + testItemCurrentBMeasDuring.d + it.d) / 3.0
    } bindTo table2r1.c4
//    private val testItemCurrentMeasDuring: Field = Field() bindTo table2r1.c5

    private val testItemCurrentAMeasAfter: Field = Field(id = "IuAfter", numOfSymbols = 1) {
//        testItemCurrentMeasAfter.value = (it.d + testItemCurrentBMeasAfter.d + testItemCurrentCMeasAfter.d) / 3.0
    } bindTo table3r1.c2
    private val testItemCurrentBMeasAfter: Field = Field(id = "IvAfter", numOfSymbols = 1) {
//        testItemCurrentMeasAfter.value = (testItemCurrentAMeasAfter.d + it.d + testItemCurrentCMeasAfter.d) / 3.0
    } bindTo table3r1.c3
    private val testItemCurrentCMeasAfter: Field = Field(id = "IwAfter", numOfSymbols = 1) {
//        testItemCurrentMeasAfter.value = (testItemCurrentAMeasAfter.d + testItemCurrentBMeasAfter.d + it.d) / 3.0
    } bindTo table3r1.c4
//    private val testItemCurrentMeasAfter: Field = Field() bindTo table3r1.c5

    private val delta: Field = Field(id = "delta", numOfSymbols = 1) bindTo table3r1.c5

    private val rpm: Field = Field(id = "Speed") pollBy with(PC71) { this to model.RPM }

    override val stateCell = table4r1.c1

    override val fields = listOf(
        testItemVoltageABMeas,
        testItemVoltageBCMeas,
        testItemVoltageCAMeas,
        testItemVoltageMeas,

        testItemCurrentAMeas,
        testItemCurrentBMeas,
        testItemCurrentCMeas,
        testItemCurrentMeas,

        testItemVoltageABMeasBefore,
        testItemVoltageBCMeasBefore,
        testItemVoltageCAMeasBefore,
        testItemVoltageMeasBefore,

        testItemVoltageABMeasDuring,
        testItemVoltageBCMeasDuring,
        testItemVoltageCAMeasDuring,
        testItemVoltageMeasDuring,

        testItemVoltageABMeasAfter,
        testItemVoltageBCMeasAfter,
        testItemVoltageCAMeasAfter,
        testItemVoltageMeasAfter,

        testItemCurrentAMeasBefore,
        testItemCurrentBMeasBefore,
        testItemCurrentCMeasBefore,
//        testItemCurrentMeasBefore,

        testItemCurrentAMeasDuring,
        testItemCurrentBMeasDuring,
        testItemCurrentCMeasDuring,
//        testItemCurrentMeasDuring,

        testItemCurrentAMeasAfter,
        testItemCurrentBMeasAfter,
        testItemCurrentCMeasAfter,
//        testItemCurrentMeasAfter,

        delta,

        rpm,

        out1UFI,
    )

    override val checkedDevices = mutableListOf<IDeviceController>(PAV41, PC71)

    override val alertMessages = listOf("Подключите провода U, V, W к ОИ")

    init {
        define(initVariables = {
            stage = Stage.BEFORE
            I_RATIO = 300.0 / 5.0
        }, assemblyCircuit = {
            DD2.onTTT()
            loadUZ91()
        }, process = {
            if (isRunning) while (rpm.d > 50) {
                state = "Ожидание останова"
                wait(1)
            }
            if (isRunning) initFI()

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

            stage = Stage.BEFORE
            if (isRunning) setVoltageByUZ91(380.0)
            val storedOut1UFI = out1UFI.value
            if (isRunning) selectCurrentStage({ testItemCurrentMeas.d }) { I_RATIO = it }
            wait(5)

            stage = Stage.DURING
            if (isRunning) toMaxCurrentStage { I_RATIO = it }
            if (isRunning) setVoltageByUZ91(380.0 * 1.3)
            if (isRunning) selectCurrentStage({ testItemCurrentMeas.d }) { I_RATIO = it }
            wait(60) {
                state = "Осталось ${(60 - it).formatPoint()} с"
            }

            stage = Stage.AFTER
            if (isRunning) toMaxCurrentStage { I_RATIO = it }
            if (isRunning) setVoltageByUZ91(380.0)
            out1UFI.value = storedOut1UFI
            if (isRunning) selectCurrentStage({ testItemCurrentMeas.d }) { I_RATIO = it }
            wait(5)

            val delta1 = abs(testItemCurrentAMeasBefore.d / testItemCurrentAMeasAfter.d * 100.0 - 100.0)
            val delta2 = abs(testItemCurrentBMeasBefore.d / testItemCurrentBMeasAfter.d * 100.0 - 100.0)
            val delta3 = abs(testItemCurrentCMeasBefore.d / testItemCurrentCMeasAfter.d * 100.0 - 100.0)
            if (isRunning) delta.value = maxOf(delta1, delta2, delta3)
        }, finish = {
            UZ91.offFreewheeling(out1UFI)
        })
    }

    private fun setVoltageByUZ91(voltage: Double) {
        state = "Выставление напряжения ${voltage.formatPoint()} В (контроль PAV41) по UZ91"
        regulation(
            out1UFI, voltage, deltaMin = 1, deltaMax = 3, influenceStep = 1, waitSec = .05
        ) { testItemVoltageMeas.d }

        wait(5)
        if (isRunning) state = "Напряжение установлено"
    }

    private fun initFI() {
        state = "Инициализация ЧП"
        if (isRunning) UZ91.setObjectParamsRun()
        if (isRunning) UZ91.setVoltage(0.0)
        if (isRunning) UZ91.setObjectFCur(0.0)
        if (isRunning) UZ91.startObject()
        wait(3)
    }
}
