package ru.avem.stand.tests.business

import ru.avem.stand.io.DevicePoller.DD2
import ru.avem.stand.io.DevicePoller.PA62
import ru.avem.stand.io.DevicePoller.PV61
import ru.avem.stand.testitem.TIManager
import ru.avem.stand.tests.KSPADTest
import ru.avem.stand.tests.model.Field
import ru.avem.stand.tests.model.TestRow
import ru.avem.stand.view.composables.SchemeType
import ru.avem.stand.view.composables.table.*
import ru.avem.stand.view.composables.table.TableScheme.Companion.named

object DCWindingResistance : KSPADTest(
    abbr = "ИКАС",
    tag = "IKAS",
    name = "Измерение сопротивления обмоток при постоянном токе в практически холодном состоянии",
    tables(
        table1(
            columns(
                TestRow::c1 named "Ruv, Ом",
                TestRow::c2 named "Rvw, Ом",
                TestRow::c3 named "Rwu, Ом",
            )
        ),
        table2(
            columns(
                TestRow::c1 named "Ru, Ом",
                TestRow::c2 named "Rv, Ом",
                TestRow::c3 named "Rw, Ом",
            )
        ),
        table3(
            columns(
                TestRow::c1 named "ΔR, %",
            )
        ),
        table4(
            columns(
                TestRow::c1 named "Состояние",
            )
        ),
    )
) {
    private val testItemVoltage: Field = Field(abs = true) pollBy with(PV61) { this to model.U_TRMS }
    private val testItemCurrent: Field = Field(abs = true) pollBy with(PA62) { this to model.AMPERAGE }

    private val R_UV: Field = Field(id = "Ruv", numOfSymbols = 2) bindTo table1r1.c1
    private val R_VW: Field = Field(id = "Rvw", numOfSymbols = 2) bindTo table1r1.c2
    private val R_WU: Field = Field(id = "Rwu", numOfSymbols = 2) bindTo table1r1.c3

    private val R_U: Field = Field(id = "Ru", numOfSymbols = 2) bindTo table2r1.c1
    private val R_V: Field = Field(id = "Rv", numOfSymbols = 2) bindTo table2r1.c2
    private val R_W: Field = Field(id = "Rw", numOfSymbols = 2) bindTo table2r1.c3

    private val delta: Field = Field(id = "D_R", numOfSymbols = 2) bindTo table3r1.c1

    override val stateCell = table4r1.c1

    override val fields = listOf(
        testItemVoltage, testItemCurrent,

        R_UV,
        R_VW,
        R_WU,

        R_U,
        R_V,
        R_W,

        delta
    )

    override val checkedDevices = listOf(PV61, PA62)

    override val alertMessages = listOf("Подключите провода U, V, W к ОИ")

    private var scheme = ""

    init {
        define(
            initVariables = {
                scheme = TIManager.testItem.scheme
            },
            process = {
                if (isRunning) meas1()
                if (isRunning) meas2()
                if (isRunning) meas3()
                if (isRunning) calcR()
                if (isRunning) calcDelta()
            }
        )
    }

    private fun meas1() {
        state = "Измерение сопротивление между u и v"
        DD2.onIKASa()
        DD2.onIKASba()
        wait(10)
        R_UV.value = testItemVoltage.d / testItemCurrent.d
        DD2.offIKASa()
        DD2.offIKASba()
        wait(10)
    }

    private fun meas2() {
        state = "Измерение сопротивление между v и w"
        DD2.onIKASbc()
        DD2.onIKASc()
        wait(10)
        R_VW.value = testItemVoltage.d / testItemCurrent.d
        DD2.offIKASbc()
        DD2.offIKASc()
        wait(10)
    }

    private fun meas3() {
        state = "Измерение сопротивление между w и u"
        DD2.onIKASa()
        DD2.onIKASc()
        wait(10)
        R_WU.value = testItemVoltage.d / testItemCurrent.d
        DD2.offIKASa()
        DD2.offIKASc()
    }

    private fun calcR() {
        if (scheme == SchemeType.STAR.toString()) {
            R_U.value = (R_WU.d + R_UV.d - R_VW.d) / 2.0
            R_V.value = (R_UV.d + R_VW.d - R_WU.d) / 2.0
            R_W.value = (R_VW.d + R_WU.d - R_UV.d) / 2.0
        } else {
            R_U.value = 2.0 * R_UV.d * R_VW.d / (R_UV.d + R_VW.d - R_WU.d) - (R_UV.d + R_VW.d - R_WU.d) / 2.0
            R_V.value = 2.0 * R_VW.d * R_WU.d / (R_VW.d + R_WU.d - R_UV.d) - (R_VW.d + R_WU.d - R_UV.d) / 2.0
            R_W.value = 2.0 * R_WU.d * R_UV.d / (R_WU.d + R_UV.d - R_VW.d) - (R_WU.d + R_UV.d - R_VW.d) / 2.0
        }
    }

    private fun calcDelta() {
        if (minOf(R_U.d, R_V.d, R_W.d) > 0.001) {
            delta.value = maxOf(R_U.d, R_V.d, R_W.d) / minOf(R_U.d, R_V.d, R_W.d) * 100.0 - 100.0
        } else {
            cause = "Проверьте правильность подключения"
        }
    }
}
